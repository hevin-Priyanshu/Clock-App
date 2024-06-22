package com.demo.myapplication.viewmodel.activitiesViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.myapplication.models.LapDetailsModel
import com.demo.myapplication.models.StoreStartTimerModel
import com.demo.myapplication.viewmodel.mainRepository.MainRepository
import kotlinx.coroutines.launch

class MainActivityViewModel(private val mainRepository: MainRepository) : ViewModel() {

    val getAllDataList = mainRepository.getAllDataList()
    val getAllStates = mainRepository.getLatestState()
    val getAllTimeZoneList = mainRepository.getAllTimeZoneList()

    fun saveLapDetailsTime(lapDetailsList: MutableList<LapDetailsModel>) {
        viewModelScope.launch {
            mainRepository.saveLapDetailsTime(lapDetailsList)
        }
    }

    fun deleteAllLapDetails() {
        viewModelScope.launch {
            mainRepository.deleteAllLapDetails()
        }
    }

    fun deleteAllStates() {
        viewModelScope.launch {
            mainRepository.deleteAllStates()
        }
    }

    fun saveStopWatchTime(timerModel: StoreStartTimerModel) {
        viewModelScope.launch {
            mainRepository.saveStopWatchTime(timerModel)
        }
    }

    suspend fun addSelectedTimeZones(selectedArraylist: ArrayList<Int>) {
        viewModelScope.launch {
            mainRepository.addSelectedTimeZones(selectedArraylist)
        }
    }

    /**************************************************************************************/
}