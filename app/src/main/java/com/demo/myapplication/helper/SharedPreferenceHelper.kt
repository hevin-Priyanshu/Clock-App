package com.demo.myapplication.helper

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceHelper constructor(context: Context) {

    init {
        sharedPreferences =
            context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.apply()
    }

    var theme: Int
        get() = sharedPreferences.getInt(THEME_ACTIVE, -1)
        set(theme) {
            editor.putInt(THEME_ACTIVE, theme)
            editor.apply()
        }

    var setOrGetLastFragment: Int
        get() = sharedPreferences.getInt(DEFAULT_FRAGMENT_KEY, DEFAULT_FRAGMENT_VALUE)
        set(fragmentCount) {
            editor.putInt(DEFAULT_FRAGMENT_KEY, fragmentCount).apply()
        }


    companion object {
        const val SHARED_PREFERENCES_NAME = "pref"
        const val LANGUAGE_KEY = "languageKey"
        const val DEFAULT_LANGUAGE = "en"
        const val DEFAULT_FRAGMENT_KEY = "SELECTED_FRAGMENT_INDEX"
        const val DEFAULT_FRAGMENT_VALUE = 0 // Assuming the first fragment is the default
        const val THEME_ACTIVE = "myTheme"
        private lateinit var sharedPreferences: SharedPreferences
        private lateinit var editor: SharedPreferences.Editor

        @Volatile
        private var instance: SharedPreferenceHelper? = null

        fun getInstance(context: Context): SharedPreferenceHelper {
            return instance ?: synchronized(this) {
                instance ?: SharedPreferenceHelper(context).also { instance = it }
            }
        }
    }

}