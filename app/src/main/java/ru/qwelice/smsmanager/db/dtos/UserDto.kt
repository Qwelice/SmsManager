package ru.qwelice.smsmanager.db.dtos

import ru.qwelice.smsmanager.mailing.enums.HostType

data class UserDto(
    var id: Int?,
    val email: String,
    val username: String,
    val password: String,
    val hostType: HostType
)
