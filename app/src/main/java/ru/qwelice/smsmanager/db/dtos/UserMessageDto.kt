package ru.qwelice.smsmanager.db.dtos

data class UserMessageDto(
    var id: Int?,
    val email: String,
    val subject: String,
    val message: String,
    val state: Boolean,
    val latitude: Double,
    val longitude: Double
)
