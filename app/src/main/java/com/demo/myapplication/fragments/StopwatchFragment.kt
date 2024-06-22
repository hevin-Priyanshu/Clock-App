package com.demo.myapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.myapplication.R
import com.demo.myapplication.adapters.LapDetailsAdapter
import com.demo.myapplication.base.BaseFragment
import com.demo.myapplication.databinding.FragmentStopWatchBinding
import com.demo.myapplication.events.ResetStopwatchTimerEvent
import com.demo.myapplication.events.StopTimerEvent
import com.demo.myapplication.events.TimerRunningEvent
import com.demo.myapplication.extensions.gone
import com.demo.myapplication.extensions.visible
import com.demo.myapplication.helper.NotificationHelper
import com.demo.myapplication.models.LapDetailsModel
import com.demo.myapplication.models.States
import com.demo.myapplication.models.StoreStartTimerModel
import com.demo.myapplication.services.StopwatchService
import com.demo.myapplication.utilities.CommonFunctions.getSharedPref
import com.demo.myapplication.viewmodel.activitiesViewModel.MainActivityViewModel
import com.demo.myapplication.viewmodel.factory.ViewModelFactory
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class StopwatchFragment : BaseFragment<FragmentStopWatchBinding>() {

        private val notificationHelper by lazy { NotificationHelper(requireContext()) }
//    private lateinit var timer: CountDownTimer
    private val mainActivityViewModel: MainActivityViewModel by viewModels { ViewModelFactory }
    private lateinit var lapDetailsAdapter: LapDetailsAdapter
    private var lapDetailsList = mutableListOf<LapDetailsModel>()
    private lateinit var timerModel: StoreStartTimerModel
    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private var lapCount: Int = 1
    private var lastLapTime: Long = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var state: States

    companion object {
        @JvmStatic
        fun newInstance(): StopwatchFragment {
            val fragment = StopwatchFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getBindingView(): FragmentStopWatchBinding {
        return FragmentStopWatchBinding.inflate(layoutInflater)
    }

    private val updateNotificationRunnable = object : Runnable {
        override fun run() {
            elapsedTime = SystemClock.elapsedRealtime() - startTime
            updateTimerText(elapsedTime)
            handler.postDelayed(this, 10)
        }
    }

    override fun initData() {

        timerModel = mainActivityViewModel.getAllStates ?: StoreStartTimerModel()
        state = timerModel.states
        Log.d("", "initData: $timerModel")
        Log.d("", "initData:--------state--------- $state")

        if (state == States.RUNNING) {
            startTime = timerModel.startTime
            elapsedTime = SystemClock.elapsedRealtime() - startTime
            handler.post(updateNotificationRunnable)
        }
        setupRecyclerView()
        handleClickListeners()

        binding.buttonReset.setOnClickListener {
            resetTimer()
        }

        binding.buttonLab.setOnClickListener {
            createLapDetail()
        }
    }


    private fun handleClickListeners() {
        Log.d("", "initData:--------state---handleClickListeners------ $state")

        binding.buttonStartStop.setOnClickListener {

            Log.d("", "initData:-------setOnClickListener")
            if (state == States.RUNNING) {
                Log.d("", "initData:-------state == States.RUNNING  ${state == States.RUNNING}")
//                val startStopIntent = Intent(requireContext(), StopwatchService::class.java)
//                startStopIntent.action = StopwatchService.ACTION_STOP
//                mContext.startService(startStopIntent)
                pauseTimer()
            } else {

                Log.d("", "initData:-------state == States.stoped  ${state == States.RUNNING}")
//                val startStopIntent = Intent(requireContext(), StopwatchService::class.java)
//                startStopIntent.action = StopwatchService.ACTION_START
//                mContext.startService(startStopIntent)
                startTimer()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        lapDetailsAdapter = LapDetailsAdapter()
        binding.recyclerView.adapter = lapDetailsAdapter
    }

    private fun createLapDetail() {
        if (state == States.RUNNING) {
            val currentElapsedTime = SystemClock.elapsedRealtime() - startTime
            val lapTime = calculateLapTime(currentElapsedTime)
            val overallTime = elapsedTimeToString(currentElapsedTime)
            val lapDetails = LapDetailsModel(lapCount++, lapTime, overallTime)
            lastLapTime = currentElapsedTime
            lapDetailsList.add(0, lapDetails)
            lapDetailsAdapter.submitList(lapDetailsList.toList())
            lapDetailsAdapter.notifyDataSetChanged()
            binding.recyclerView.scrollToPosition(0)
            mainActivityViewModel.saveLapDetailsTime(lapDetailsList)
        }
    }

    private fun calculateLapTime(currentElapsedTime: Long): String {
        val lapTimeMillis = currentElapsedTime - lastLapTime
        val minutes = (lapTimeMillis / 60000).toInt()
        val seconds = (lapTimeMillis % 60000 / 1000).toInt()
        val milliseconds = (lapTimeMillis % 1000 / 10).toInt()
        return "%02d:%02d.%02d".format(minutes, seconds, milliseconds)
    }


    private fun elapsedTimeToString(elapsedTime: Long): String {
        val minutes = (elapsedTime / 60000).toInt()
        val seconds = (elapsedTime % 60000 / 1000).toInt()
        val milliseconds = (elapsedTime % 1000 / 10).toInt()
        return "%02d:%02d.%02d".format(minutes, seconds, milliseconds)
    }


    private fun startTimer() {

        startTime = SystemClock.elapsedRealtime() - elapsedTime
        handler.post(updateNotificationRunnable)

        binding.apply {
            buttonLab.visible()
            buttonReset.visible()
            buttonStartStop.setImageResource(R.drawable.ic_stop)
        }

        EventBus.getDefault().post(TimerRunningEvent(startTime))
        // Insert a new timer
        val newTimer = StoreStartTimerModel(
            states = States.RUNNING,
            startTime = startTime,
            elapsedTime = elapsedTime
        )
        state = States.RUNNING
//        notificationHelper.showNotification(0)
        mainActivityViewModel.saveStopWatchTime(newTimer)
    }


    private fun updateTimerText(elapsedTime: Long) {
        val minutes = (elapsedTime / 60000).toInt()
        val seconds = (elapsedTime % 60000 / 1000).toInt()
        val milliseconds = (elapsedTime % 1000 / 10).toInt()
        val customText = String.format("%02d:%02d.%02d", minutes, seconds, milliseconds)
        binding.stopwatchTime.text = customText
    }

    private fun pauseTimer() {
        if (state == States.RUNNING) {

            elapsedTime = SystemClock.elapsedRealtime() - startTime

            // Insert a new timer
            val newTimer = StoreStartTimerModel(
                states = States.PAUSED,
                startTime = startTime,
                elapsedTime = elapsedTime
            )
            state = States.PAUSED
            mainActivityViewModel.saveStopWatchTime(newTimer)

            binding.apply {
                buttonLab.gone()
                buttonStartStop.setImageResource(R.drawable.ic_start)
            }
            handler.removeCallbacks(updateNotificationRunnable)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun topTimerEvent(event: StopTimerEvent) {
        pauseTimer()
    }


    private fun resetTimer() {
        state = States.STOPPED
        binding.apply {
            buttonStartStop.setImageResource(R.drawable.ic_start)
            buttonLab.gone()
            buttonReset.gone()
        }
        elapsedTime = 0
        updateTimerText(elapsedTime)
        lapCount = 1
        startTime = 0
        lastLapTime = 0
        lapDetailsList.clear()
        lapDetailsAdapter.submitList(lapDetailsList)
        handler.removeCallbacks(updateNotificationRunnable)
        EventBus.getDefault().post(ResetStopwatchTimerEvent())
        mainActivityViewModel.deleteAllLapDetails()
        mainActivityViewModel.deleteAllStates()
    }


    override fun onDestroy() {
        super.onDestroy()
        mContext.getSharedPref.lastPauseTime = System.currentTimeMillis()
        handler.removeCallbacks(updateNotificationRunnable)
    }
}