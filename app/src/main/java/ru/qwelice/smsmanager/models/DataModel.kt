package ru.qwelice.smsmanager.models

import android.content.Context
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import ru.qwelice.smsmanager.db.dtos.UserDto
import ru.qwelice.smsmanager.db.dtos.UserMessageDto
import ru.qwelice.smsmanager.db.entities.UserEntity
import ru.qwelice.smsmanager.db.entities.UserMessageEntity
import ru.qwelice.smsmanager.db.initDatabase
import ru.qwelice.smsmanager.db.tables.Users
import ru.qwelice.smsmanager.db.tables.UsersMessages

class DataModel(private val ctx: Context) {
    private val dbName = "smsmanagerdb"
    private val db = ctx.initDatabase(dbName)

    init {
        transaction(db){
            SchemaUtils.createMissingTablesAndColumns(Users, UsersMessages)
            commit()
        }
    }

    fun getAllUsers() : List<UserDto>{
        val result = mutableListOf<UserDto>()
        transaction(db) {
            val preResult = UserEntity.all()
            preResult.forEach {
                result.add(it.getAsDto())
            }
            commit()
        }
        return result
    }

    fun userIsExists(userDto: UserDto) : Boolean{
        val exists = transaction(db) {
            val entity = UserEntity.find {
                (Users.username eq userDto.username) and (Users.email eq userDto.email)
            }.singleOrNull()
            return@transaction entity != null
        }
        return exists
    }

    fun appendNewUser(userDto: UserDto){
        transaction(db) {
            UserEntity.new {
                username = userDto.username
                password = userDto.password
                email = userDto.email
                hostType = userDto.hostType.toString()
            }
            commit()
        }
    }

    fun appendNewUserMessage(msg: UserMessageDto){
        transaction(db){
            UserMessageEntity.new {
                email = msg.email
                subject = msg.subject
                message = msg.message
                state = if(msg.state) 1 else 0
                latitude = msg.latitude
                longitude = msg.longitude
            }
            commit()
        }
    }

    fun getAllUserMessages(userDto: UserDto) : List<UserMessageDto>{
        val result = mutableListOf<UserMessageDto>()
        if(userDto.id == null){
            return result
        }
        transaction(db) {
            UserMessageEntity.find {
                UsersMessages.email eq userDto.email
            }.forEach {
                result.add(it.getAsDto())
            }
        }
        return result
    }

    fun updateUserMessage(userMessageDto: UserMessageDto){
        if(userMessageDto.id == null){
            return
        }
        transaction(db) {
            val entity = UserMessageEntity[userMessageDto.id!!]
            entity.message = userMessageDto.message
            entity.state = if(userMessageDto.state) 1 else 0
            commit()
        }
    }

    fun getUser(userDto: UserDto) : UserDto?{
        val entity = transaction(db) {
            UserEntity.find {
                (Users.email eq userDto.email) and  (Users.username eq userDto.username)
            }.singleOrNull()
        }
        return entity?.getAsDto()
    }

    fun getUserMessage(userMessage: UserMessageDto) : UserMessageDto?{
        val state = if(userMessage.state) 1 else 0
        val entity = transaction(db) {
            UserMessageEntity.find {
                (UsersMessages.email eq userMessage.email) and (UsersMessages.message eq userMessage.message) and (UsersMessages.state eq state)
            }.singleOrNull()
        }
        return entity?.getAsDto()
    }
}