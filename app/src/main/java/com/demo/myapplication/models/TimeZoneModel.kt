package com.demo.myapplication.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TimeZoneModelTN")
data class TimeZoneModel(
    @PrimaryKey(autoGenerate = true)
    var cityID: Int = 0,
    var timezone: String = "",
    var gmtTime: String = "",
    var cityName: String = ""
)
