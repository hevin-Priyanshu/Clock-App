package com.demo.myapplication.viewmodel.mainRepository

import com.demo.myapplication.database.MainDataBase
import com.demo.myapplication.models.LapDetailsModel
import com.demo.myapplication.models.StoreStartTimerModel
import com.demo.myapplication.models.TimeZoneModel
import com.demo.myapplication.utilities.CommonFunctions.getAllTimezone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRepository(private val getDatabase: MainDataBase) {

    private val listOfTimeZone = getAllTimezone()
    fun getAllDataList(): List<LapDetailsModel> {
        return getDatabase.commonDao().getAllLapDetails()
    }

    fun getLatestState(): StoreStartTimerModel? {
        return getDatabase.commonDao().getLatestState()
    }

    fun getAllTimeZoneList(): List<TimeZoneModel> {
        return getDatabase.commonDao().getAllTimeZone()
    }

    suspend fun saveLapDetailsTime(lapDetailsList: MutableList<LapDetailsModel>) {
        withContext(Dispatchers.IO) {
            getDatabase.commonDao().insertLapDetail(lapDetailsList)
        }
    }

    suspend fun deleteAllLapDetails() {
        withContext(Dispatchers.IO) {
            getDatabase.commonDao().deleteAllLapDetails()
        }
    }

    suspend fun deleteAllStates() {
        withContext(Dispatchers.IO) {
            getDatabase.commonDao().deleteAllStates()
        }
    }


    suspend fun saveStopWatchTime(timer: StoreStartTimerModel) {
        withContext(Dispatchers.IO) {
            getDatabase.commonDao().insertStartTimer(timer)
        }
    }

    suspend fun addSelectedTimeZones(selectedArraylist: ArrayList<Int>) {
        withContext(Dispatchers.IO) {
            val filteredList = getFilteredList(selectedArraylist)
            getDatabase.commonDao().insertTimeZone(filteredList)
        }
    }

    private fun getFilteredList(list: ArrayList<Int>): List<TimeZoneModel> {
        return list.mapNotNull { index ->
            if (index in listOfTimeZone.indices) {
                listOfTimeZone[index]
            } else {
                null
            }
        }
    }

}