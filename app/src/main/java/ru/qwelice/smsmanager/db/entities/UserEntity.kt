package ru.qwelice.smsmanager.db.entities

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.qwelice.smsmanager.db.dtos.UserDto
import ru.qwelice.smsmanager.db.tables.Users
import ru.qwelice.smsmanager.mailing.enums.HostType

class UserEntity(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, UserEntity>(Users)

    var email by Users.email
    var username by Users.username
    var password by Users.password
    var hostType by Users.hostType

    fun getAsDto() : UserDto{
        val hostTp = when(hostType){
            "yandex" -> HostType.Yandex
            else -> HostType.Google
        }
        return UserDto(
            id.value, email, username, password, hostTp
        )
    }
}