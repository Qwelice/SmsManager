package ru.qwelice.smsmanager.db.entities

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.qwelice.smsmanager.db.dtos.UserMessageDto
import ru.qwelice.smsmanager.db.tables.UsersMessages

class UserMessageEntity(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, UserMessageEntity>(UsersMessages)

    var email by UsersMessages.email
    var message by UsersMessages.message
    var subject by UsersMessages.subject
    var state by UsersMessages.state
    var latitude by UsersMessages.latitude
    var longitude by UsersMessages.longitude

    fun getAsDto() : UserMessageDto{
        val st = state != 0
        return UserMessageDto(
            id.value, email, subject, message, st, latitude, longitude
        )
    }
}