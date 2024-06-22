package com.demo.myapplication.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.myapplication.R
import com.demo.myapplication.adapters.LapDetailsAdapter
import com.demo.myapplication.base.BaseFragment
import com.demo.myapplication.databinding.FragmentStopwatchNewBinding
import com.demo.myapplication.events.StopTimerEvent
import com.demo.myapplication.extensions.gone
import com.demo.myapplication.extensions.visible
import com.demo.myapplication.helper.StopwatchManager
import com.demo.myapplication.models.LapDetailsModel
import com.demo.myapplication.models.States
import com.demo.myapplication.models.StoreStartTimerModel
import com.demo.myapplication.viewmodel.activitiesViewModel.MainActivityViewModel
import com.demo.myapplication.viewmodel.factory.ViewModelFactory
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class StopwatchFragmentNew : BaseFragment<FragmentStopwatchNewBinding>() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels { ViewModelFactory }
    private lateinit var lapDetailsAdapter: LapDetailsAdapter
    private var lapDetailsList = mutableListOf<LapDetailsModel>()
    private lateinit var stopwatchManager: StopwatchManager
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        @JvmStatic
        fun newInstance(): StopwatchFragmentNew {
            val fragment = StopwatchFragmentNew()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getBindingView(): FragmentStopwatchNewBinding {
        return FragmentStopwatchNewBinding.inflate(layoutInflater)
    }

    override fun initData() {

        setupRecyclerView()

        stopwatchManager = StopwatchManager(
            context = mContext,
            viewModel = mainActivityViewModel,
            handler = handler,
            lifecycleScope = lifecycleScope,
            updateTimerText = this::updateTimerText,
            updateUIState = this::updateUIState
        )

        lifecycleScope.launch {
            val timerModel = mainActivityViewModel.getAllStates ?: StoreStartTimerModel()
            stopwatchManager.restoreTimerState(timerModel)
            restoreLapDetails()
        }

        handleClickListeners()

        binding.buttonReset.setOnClickListener {
            stopwatchManager.resetTimer()
            clearLapDetails()
        }

        binding.buttonLab.setOnClickListener {
            createLapDetail()
        }
    }

    private fun restoreLapDetails() {
        lifecycleScope.launch {
            lapDetailsList = mainActivityViewModel.getAllDataList.toMutableList()
            lapDetailsAdapter.submitList(lapDetailsList)
        }
    }

    private fun handleClickListeners() {
        binding.buttonStartStop.setOnClickListener {
            if (stopwatchManager.getState() == States.RUNNING) {
                stopwatchManager.pauseTimer()
            } else {
                stopwatchManager.startTimer()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        lapDetailsAdapter = LapDetailsAdapter()
        binding.recyclerView.adapter = lapDetailsAdapter
    }

    private fun createLapDetail() {
        if (stopwatchManager.getState() == States.RUNNING) {
            val currentElapsedTime = SystemClock.elapsedRealtime() - stopwatchManager.getStartTime()
            val lapTime = calculateLapTime(currentElapsedTime)
            val overallTime = elapsedTimeToString(currentElapsedTime)
            val lapDetails = LapDetailsModel(lapDetailsList.size + 1, lapTime, overallTime)
            lapDetailsList.add(0, lapDetails)
            lapDetailsAdapter.submitList(lapDetailsList.toList())
            binding.recyclerView.scrollToPosition(0)
            mainActivityViewModel.saveLapDetailsTime(lapDetailsList)
        }
    }

    private fun clearLapDetails() {
        lapDetailsList.clear()
        lapDetailsAdapter.submitList(lapDetailsList)
        mainActivityViewModel.deleteAllLapDetails()
        mainActivityViewModel.deleteAllStates()
    }

    private fun calculateLapTime(currentElapsedTime: Long): String {
        val lapTimeMillis = currentElapsedTime - stopwatchManager.getLastLapTime()
        stopwatchManager.setLastLapTime(currentElapsedTime)
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

    private fun updateTimerText(elapsedTime: Long) {
        binding.stopwatchTime.text = elapsedTimeToString(elapsedTime)
    }


    private fun updateUIState(state: States) {
        when (state) {
            States.RUNNING -> {
                binding.buttonLab.visible()
                binding.buttonReset.visible()
                binding.buttonStartStop.setImageResource(R.drawable.ic_stop)
            }

            States.PAUSED -> {
                binding.buttonLab.gone()
                binding.buttonReset.visible()
                binding.buttonStartStop.setImageResource(R.drawable.ic_start)
            }

            States.STOPPED -> {
                binding.buttonLab.gone()
                binding.buttonReset.gone()
                binding.buttonStartStop.setImageResource(R.drawable.ic_start)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun topTimerEvent(event: StopTimerEvent) {
        stopwatchManager.pauseTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}