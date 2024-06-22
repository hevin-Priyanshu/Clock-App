package com.demo.myapplication.events

import androidx.annotation.Keep

@Keep
class SendEvent
@Keep
class TimerRunningEvent(val startTime: Long)

@Keep
class StopTimerEvent
@Keep
class StartTimerEvent
@Keep
class ResetStopwatchTimerEvent