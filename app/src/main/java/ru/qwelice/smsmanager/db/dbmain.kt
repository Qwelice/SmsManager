package ru.qwelice.smsmanager.db

import android.content.Context
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.vendors.SQLiteDialect

fun Context.initDatabase(name: String) : Database {
    return Database.connect(
        "jdbc:${SQLiteDialect.dialectName}:/data/data/$packageName/$name"
    )
}