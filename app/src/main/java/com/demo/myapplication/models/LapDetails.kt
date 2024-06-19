package com.demo.myapplication.models

import androidx.annotation.Keep

@Keep
data class LapDetails(
    val lap: Int, val lapTime: String, val overallTime: String
)
