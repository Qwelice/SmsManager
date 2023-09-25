package ru.qwelice.smsmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.qwelice.smsmanager.mailing.*
import ru.qwelice.smsmanager.ui.ScreenType
import ru.qwelice.smsmanager.ui.common.HomeScreen
import ru.qwelice.smsmanager.ui.common.LoginScreen
import ru.qwelice.smsmanager.ui.common.RegisterScreen
import ru.qwelice.smsmanager.utils.PermissionsManager
import ru.qwelice.smsmanager.viewmodels.AppViewModel

class MainActivity : ComponentActivity() {
    private val launcher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){
        parseResults(it)
    }

    private fun parseResults(results: Map<String, Boolean>){
        val mainGranted = PermissionsManager.permissionsAreGranted(applicationContext, PermissionsManager.mainPermissions)
        if(mainGranted){
            launcher.launch(arrayOf(PermissionsManager.backLocationPermission))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm = viewModel<AppViewModel>()
            val screen = vm.currentScreen.observeAsState()
            when(screen.value){
                ScreenType.Login -> {
                    LoginScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    )
                }
                ScreenType.Home -> {
                    HomeScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    )
                }
                else -> {
                    RegisterScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    )
                }
            }
        }

        if(PermissionsManager.checkAndRequestMissingPermissions(
                applicationContext,
                PermissionsManager.mainPermissions,
                launcher
            )){
            PermissionsManager.checkAndRequestMissingPermissions(
                applicationContext,
                arrayOf(PermissionsManager.backLocationPermission),
                launcher
            )
        }
    }
}

@Composable
fun MailSendingTest(
    modifier: Modifier = Modifier,
    client: MailClient
){
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ){
        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                val msg = MailMessage(
                    from = "Qwelice@yandex.ru",
                    to ="Qwelice@yandex.ru",
                    subject = "mail test",
                    content = "hello, this is mail test message. this message means that all is working good :)"
                )
                client.sendMessage(msg)
            }
        }) {
            Text("mail message")
        }
    }
}