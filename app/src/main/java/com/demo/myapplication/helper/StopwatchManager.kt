package com.demo.myapplication.helper

import android.content.Context
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.demo.myapplication.models.States
import com.demo.myapplication.models.StoreStartTimerModel
import com.demo.myapplication.viewmodel.activitiesViewModel.MainActivityViewModel
import kotlinx.coroutines.launch

class StopwatchManager(
    private val context: Context,
    private val viewModel: MainActivityViewModel,
    private val handler: Handler,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val updateTimerText: (Long) -> Unit,
    private val updateUIState: (States) -> Unit
) {
    private val notificationHelper by lazy { NotificationHelper(context) }
    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private var lastLapTime: Long = 0
    private var state: States = States.STOPPED
    private val updateNotificationRunnable = object : Runnable {
        override fun run() {
            elapsedTime = SystemClock.elapsedRealtime() - startTime
            updateTimerText(elapsedTime)
//            notificationHelper.showNotification(elapsedTime)
            handler.postDelayed(this, 10)
        }
    }

    fun startTimer() {
        Log.d("", "startTimer: $startTime")
        startTime = SystemClock.elapsedRealtime() - elapsedTime
        handler.post(updateNotificationRunnable)
        updateUIState(States.RUNNING)
        saveTimerState(States.RUNNING)
        notificationHelper.showNotification(startTime)
    }

    fun pauseTimer() {
        if (state == States.RUNNING) {
            elapsedTime = SystemClock.elapsedRealtime() - startTime
            handler.removeCallbacks(updateNotificationRunnable)
            updateUIState(States.PAUSED)
            saveTimerState(States.PAUSED)
            notificationHelper.cancelNotification()
        }
    }

    fun resetTimer() {
        handler.removeCallbacks(updateNotificationRunnable)
        elapsedTime = 0
        startTime = 0
        updateTimerText(0)
        state = States.STOPPED
        updateUIState(States.STOPPED)
        saveTimerState(States.STOPPED)
        notificationHelper.cancelNotification()
    }

    private fun saveTimerState(newState: States) {
        state = newState
        lifecycleScope.launch {
            val timerModel = StoreStartTimerModel(
                states = state, startTime = startTime, elapsedTime = elapsedTime
            )
            viewModel.saveStopWatchTime(timerModel)
        }
    }

    fun restoreTimerState(timerModel: StoreStartTimerModel) {
        startTime = timerModel.startTime
        elapsedTime = timerModel.elapsedTime
        state = timerModel.states
        if (state == States.RUNNING) {
            handler.post(updateNotificationRunnable)
            notificationHelper.showNotification(startTime)
        } else if (state == States.PAUSED) {
            updateTimerText(elapsedTime)
        }
        updateUIState(state)
    }

    fun getState(): States {
        return state
    }

    fun getStartTime(): Long {
        return startTime
    }

    fun getLastLapTime(): Long {
        return lastLapTime
    }

    fun setLastLapTime(time: Long) {
        lastLapTime = time
    }
}
