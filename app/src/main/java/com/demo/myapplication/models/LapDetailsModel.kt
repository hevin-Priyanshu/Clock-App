package com.demo.myapplication.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "LapDetailsTN")
data class LapDetailsModel(
    @PrimaryKey(autoGenerate = true) var lap: Int = 0,
    var lapTime: String = "",
    var overallTime: String = ""
)
