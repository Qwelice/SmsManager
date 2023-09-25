package ru.qwelice.smsmanager.utils

import ru.qwelice.smsmanager.db.dtos.UserDto
import ru.qwelice.smsmanager.mailing.MailConfiguration
import ru.qwelice.smsmanager.mailing.MailConfigurationBuilder
import ru.qwelice.smsmanager.mailing.enums.HostType

data class UserProfile(
    val id: Int,
    val username: String,
    val password: String,
    val hostType: HostType,
    val email: String
){
    fun getAsDto() : UserDto{
        return UserDto(
            id, email, username, password, hostType
        )
    }

    fun getConfiguration() : MailConfiguration?{
        if(id == -1){
            return null
        }
        return if(hostType == HostType.Yandex) {
            MailConfigurationBuilder()
                .buildYandexPreset(username, password, email)
        }else{
            null
        }
    }
}