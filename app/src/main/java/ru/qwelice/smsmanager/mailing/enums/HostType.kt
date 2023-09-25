package ru.qwelice.smsmanager.mailing.enums

enum class HostType {
    Yandex{
        override fun toString(): String {
            return "yandex"
        }
}   ,
    Google{
        override fun toString(): String {
            return "google"
        }
    }
}