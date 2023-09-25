package ru.qwelice.smsmanager.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable("users"){
    val email = varchar("email", 255)
    val username = varchar("username", 255)
    val password = varchar("password", 255)
    val hostType = varchar("hosttype", 255)
}