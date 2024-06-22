package com.demo.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.demo.myapplication.models.Converters
import com.demo.myapplication.models.LapDetailsModel
import com.demo.myapplication.models.StoreStartTimerModel
import com.demo.myapplication.models.TimeZoneModel

private const val DB_NAME = "contact_database"

@Database(
    entities = [LapDetailsModel::class, StoreStartTimerModel::class, TimeZoneModel::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MainDataBase : RoomDatabase() {
    abstract fun commonDao(): CommonDao

    companion object {
        private var INSTANCE: MainDataBase? = null
        fun getDatabase(context: Context): MainDataBase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext, MainDataBase::class.java, DB_NAME
            ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
                .also { INSTANCE = it }
        }
    }
}