package ru.qwelice.smsmanager.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.qwelice.smsmanager.db.dtos.UserDto
import ru.qwelice.smsmanager.db.dtos.UserMessageDto
import ru.qwelice.smsmanager.locating.geocoding.retrofit.RetrofitClient
import ru.qwelice.smsmanager.mailing.MailMessage
import ru.qwelice.smsmanager.mailing.enums.HostType
import ru.qwelice.smsmanager.models.DataModel
import ru.qwelice.smsmanager.models.LocationModel
import ru.qwelice.smsmanager.models.MailModel
import ru.qwelice.smsmanager.ui.ScreenType
import ru.qwelice.smsmanager.utils.UserProfile

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val dataModel = DataModel(getApplication<Application>().applicationContext)
    private val mailModel = MailModel()
    private val locationModel = LocationModel(getApplication<Application>().applicationContext){ location ->
        val newLocation = Pair(location.latitude, location.longitude)
        currentLocation.value = newLocation
    }

    private val screenTypeLive = MutableLiveData<ScreenType>()
    val allUsers = mutableStateListOf<UserDto>()
    val allUserMessages = mutableStateListOf<UserMessageDto>()
    private val profileLive = MutableLiveData<UserProfile>()
    private val currentLocation = mutableStateOf(Pair(0.0, 0.0))
    val currentScreen: LiveData<ScreenType> = screenTypeLive
    val profile: LiveData<UserProfile> = profileLive

    init {
        screenTypeLive.value = ScreenType.Login
        profileLive.value = UserProfile(-1, "", "", HostType.Yandex, "")
        updateUsers()
        locationModel.start()
    }

    fun selectUser(userDto: UserDto){
        if(userDto.id != null){
            profileLive.value = UserProfile(
                userDto.id!!,
                userDto.username,
                userDto.password,
                userDto.hostType,
                userDto.email
            )
            mailModel.setConfiguration(profileLive.value!!.getConfiguration()!!){ subject, message ->
                CoroutineScope(Dispatchers.IO).launch {
                    if (profile.value == null) {
                        return@launch
                    }
                    if(!locationModel.isStarted){
                        locationModel.start()
                    }
                    val loc = currentLocation.value
                    val lat = (loc.first * 100).toInt() / 100.0
                    val lng = (loc.second * 100).toInt() / 100.0
                    val adr = RetrofitClient.getInstance().getAddress(loc.first, loc.second)
                    val msg = MailMessage(
                        profile.value!!.email,
                        profile.value!!.email,
                        subject,
                        message,
                        location = adr
                    )
                    val sendResult = mailModel.sendMessage(msg)
                    val newUserMessageDto = UserMessageDto(
                        null,
                        profile.value!!.email,
                        subject,
                        message,
                        state = sendResult,
                        latitude = loc.first,
                        longitude = loc.second
                    )
                    dataModel.appendNewUserMessage(newUserMessageDto)
                    updateUserMessages()
                }
            }
        }
    }

    fun getUsers() : List<UserDto> {
        updateUsers()
        val result = mutableListOf<UserDto>()
        allUsers.forEach{
            result.add(it)
        }
        return result
    }

    fun getUserMessages() : List<UserMessageDto>{
        updateUsers()
        val result = mutableListOf<UserMessageDto>()
        allUserMessages.forEach{
            result.add(it)
        }
        return result
    }

    fun updateScreen(type: ScreenType){
        screenTypeLive.value = type
    }

    private fun updateUsers(){
        viewModelScope.launch {
            allUsers.clear()
            dataModel.getAllUsers().forEach {
                allUsers.add(it)
            }
        }
    }

    fun updateUserMessages(){
        CoroutineScope(Dispatchers.IO).launch {
            if(profileLive.value!!.id != -1){
                allUserMessages.clear()
                dataModel.getAllUserMessages(profileLive.value!!.getAsDto()).forEach {
                    var elem: UserMessageDto? = it
                    if(!it.state){
                        elem = trySend(it)
                    }
                    if(elem == null){
                        allUserMessages.add(it)
                    }else{
                        allUserMessages.add(elem)
                    }
                }
            }
        }
    }

    private suspend fun trySend(userMessage: UserMessageDto) = CoroutineScope(Dispatchers.IO).async{
        if (profile.value == null) {
            return@async null
        }
        if(!locationModel.isStarted){
            locationModel.start()
        }
        val loc = currentLocation.value
        val lat = (loc.first * 100).toInt() / 100.0
        val lng = (loc.second * 100).toInt() / 100.0
        val msg = MailMessage(
            profile.value!!.email,
            profile.value!!.email,
            userMessage.subject,
            userMessage.message,
            location = "$lat $lng"
        )
        val sendResult = mailModel.sendMessage(msg)
        val newUserMessageDto = UserMessageDto(
            userMessage.id,
            profile.value!!.email,
            userMessage.subject,
            userMessage.message,
            state = sendResult,
            latitude = loc.first,
            longitude = loc.second
        )
        dataModel.updateUserMessage(newUserMessageDto)
        newUserMessageDto
    }.await()

    fun isNewUser(userDto: UserDto) : Boolean{
        return !dataModel.userIsExists(userDto)
    }

    fun addNewUser(userDto: UserDto){
        dataModel.appendNewUser(userDto)
        val user = dataModel.getUser(userDto)
        selectUser(user!!)
        updateUsers()
    }
}