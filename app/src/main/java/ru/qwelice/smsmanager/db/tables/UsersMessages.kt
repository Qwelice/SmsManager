package ru.qwelice.smsmanager.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object UsersMessages : IntIdTable("users_messages") {
    val email = varchar("email", 255)
    val subject = varchar("subject", 255)
    val message = varchar("message", 1000)
    val state = integer("state")
    val latitude = double("latitude")
    val longitude = double("longitude")
}