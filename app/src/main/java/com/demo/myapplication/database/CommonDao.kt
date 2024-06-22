package com.demo.myapplication.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.demo.myapplication.models.LapDetailsModel
import com.demo.myapplication.models.StoreStartTimerModel
import com.demo.myapplication.models.TimeZoneModel

@Dao
interface CommonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLapDetail(lapDetail: List<LapDetailsModel>)

    @Query("DELETE FROM LapDetailsTN")
    fun deleteAllLapDetails()

    @Query("SELECT * FROM LapDetailsTN")
    fun getAllLapDetails(): List<LapDetailsModel>


    /**************************************************************************/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStartTimer(storeStartTimerModel: StoreStartTimerModel)

    @Query("SELECT * FROM StartTimerTN ORDER BY id DESC LIMIT 1")
    fun getLatestState(): StoreStartTimerModel?

    @Query("DELETE FROM StartTimerTN")
    fun deleteAllStates()

    /**************************************************************************/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTimeZone(timeZone: List<TimeZoneModel>)

    @Query("SELECT * FROM TimeZoneModelTN")
    fun getAllTimeZone(): List<TimeZoneModel>

}