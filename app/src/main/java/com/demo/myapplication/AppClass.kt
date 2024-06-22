package com.demo.myapplication

import android.app.Application
import com.demo.myapplication.database.MainDataBase
import com.demo.myapplication.utilities.CommonFunctions.getDatabase
import com.demo.myapplication.viewmodel.mainRepository.MainRepository

class AppClass : Application() {

    lateinit var repository: MainRepository
    private lateinit var dataBase: MainDataBase

    override fun onCreate() {
        super.onCreate()
        initialize()
    }

    private fun initialize() {
        dataBase = applicationContext.getDatabase
        repository = MainRepository(dataBase)
    }

}