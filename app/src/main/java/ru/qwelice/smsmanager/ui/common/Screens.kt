package ru.qwelice.smsmanager.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.qwelice.smsmanager.ui.ScreenType
import ru.qwelice.smsmanager.viewmodels.AppViewModel
import ru.qwelice.smsmanager.R
import ru.qwelice.smsmanager.db.dtos.UserDto
import ru.qwelice.smsmanager.mailing.enums.HostType

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier
){
    val vm = viewModel<AppViewModel>()
    val items = vm.allUsers

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if(items.isNotEmpty()){
            var isCurrentUser by remember { mutableStateOf(false) }
            val lastColor = if(isCurrentUser) Color.Magenta else Color.LightGray
            val moreColor = if(!isCurrentUser) Color.Magenta else Color.LightGray
            val item = items.last()
            Row(){
                Button(
                    colors = ButtonDefaults.buttonColors(lastColor),
                    onClick = {
                        isCurrentUser = true
                        vm.selectUser(item)
                    }
                ) {
                    Text(text = item.username)
                }
                if(items.size > 1){
                    Button(
                        colors = ButtonDefaults.buttonColors(moreColor),
                        onClick = {
                            isCurrentUser = false
                        }
                    ) {
                        Text(text = stringResource(id = R.string.more))
                    }
                }
            }
            Button(onClick = {
                vm.updateUserMessages()
                vm.updateScreen(ScreenType.Home)
            }) {
                Text(text = stringResource(id = R.string.login))
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            onClick = {
                vm.updateScreen(ScreenType.Register)
            }) {
            Text(text = stringResource(id = R.string.signup))
        }
    }
}

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier
){
    val vm = viewModel<AppViewModel>()
    var hostPreset by remember{ mutableStateOf(HostType.Yandex) }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            Button(onClick = { vm.updateScreen(ScreenType.Login) }) {
                Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "back")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            val yandexColor = if(hostPreset == HostType.Yandex) Color.Magenta else Color.LightGray
            val googleColor = if(hostPreset == HostType.Google) Color.Magenta else Color.LightGray
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = yandexColor),
                onClick = {
                    hostPreset = HostType.Yandex
                }
            ) {
                Text(text = HostType.Yandex.toString())
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(googleColor),
                onClick = {
                    hostPreset = HostType.Google
                }
            ){
                Text(text = HostType.Google.toString())
            }
        }
        var email by remember{ mutableStateOf("") }
        var username by remember{ mutableStateOf("") }
        var password by remember{ mutableStateOf("") }
        Column() {
            Text(text = stringResource(id = R.string.email))
            TextField(value = email, onValueChange = { email = it })
        }
        Column() {
            Text(text = stringResource(id = R.string.username))
            TextField(value = username, onValueChange = { username = it })
        }
        Column() {
            Text(text = stringResource(id = R.string.password))
            TextField(value = password, onValueChange = { password = it })
        }
        Button(onClick = {
            if(
                email.trim().isEmpty()
                || username.trim().isEmpty()
                || password.trim().isEmpty()
            ){
                return@Button
            }
            val userDto = UserDto(null, email, username, password, hostPreset)
            if(vm.isNewUser(userDto)){
                vm.addNewUser(userDto)
                vm.updateScreen(ScreenType.Home)
            }else{
                return@Button
            }
        }) {
            Text(text = stringResource(id = R.string.getReady))
        }
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
){
    val vm = viewModel<AppViewModel>()
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Button(onClick = {
                vm.updateScreen(ScreenType.Login)
            }) {
                Text(text = stringResource(id = R.string.quit))
            }
            Button(onClick = {
                vm.updateUserMessages()
            }) {
                Text(stringResource(id = R.string.refresh))
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ){
            items(vm.allUserMessages){item ->
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween){
                    Text(
                        item.email,
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(2.dp),
                    )
                    Text(
                        item.message.dropLast(item.message.length / 4),
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(2.dp)
                    )
                    Text(
                        item.state.toString(),
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }
        }
    }
}