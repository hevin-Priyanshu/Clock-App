package com.demo.myapplication.models

import androidx.room.TypeConverter

enum class States {
    RUNNING, PAUSED, STOPPED
}

class Converters {
    @TypeConverter
    fun fromStates(value: States): String {
        return value.name
    }

    @TypeConverter
    fun toStates(value: String): States {
        return States.valueOf(value)
    }
}