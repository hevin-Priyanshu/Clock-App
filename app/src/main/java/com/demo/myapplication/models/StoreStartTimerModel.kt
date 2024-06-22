package com.demo.myapplication.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StartTimerTN")
data class StoreStartTimerModel(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var states: States = States.STOPPED,
    var startTime: Long = 0L,
    var elapsedTime: Long = 0L
)
