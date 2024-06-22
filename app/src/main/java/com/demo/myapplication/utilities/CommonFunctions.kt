package com.demo.myapplication.utilities

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.demo.myapplication.database.MainDataBase
import com.demo.myapplication.helper.SharedPreferenceHelper
import com.demo.myapplication.models.TimeZoneModel
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object CommonFunctions {

    // permissions
    const val PERMISSION_REQUEST_CODE = 100
    const val PERMISSION_POST_NOTIFICATIONS = 101
    const val REQUEST_APP_SETTINGS = 102

    private const val EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key"
    private const val EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":settings:show_fragment_args"
    private const val EXTRA_SYSTEM_ALERT_WINDOW = "permission_settings"

    val Context.notificationManager: NotificationManager
        get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val Context.getSharedPref: SharedPreferenceHelper
        get() = SharedPreferenceHelper.getInstance(this)

    val Context.getDatabase: MainDataBase
        get() = MainDataBase.getDatabase(this)


    fun getAllTimezone(): List<TimeZoneModel> {
        val allClockData = mutableListOf<TimeZoneModel>()
        val allZoneIds = ZoneId.getAvailableZoneIds()

        for ((index, zoneId) in allZoneIds.withIndex()) {
            val zone = ZoneId.of(zoneId)
            val slashIndex = zoneId.indexOf('/')
            if (slashIndex != -1) {
                val zoneName = zoneId.substring(slashIndex + 1).replace("_", " ")
                if (!zoneName.contains("GMT")) {
                    val countryName = zoneId.substring(0, slashIndex).replace("_", " ")
                    val offset = ZonedDateTime.now(zone).offset
                    allClockData.add(TimeZoneModel().apply {
                        cityID = index + 1
                        timezone = zoneId
                        gmtTime = "GMT${offset.id}"
                        cityName = "$zoneName, $countryName"
                    })
                }
            }
        }
        return allClockData
    }


    fun getCurrentTimeInTimeZone(timeZoneId: String): String {
        val zoneId = ZoneId.of(timeZoneId)
        val currentTime = ZonedDateTime.now(zoneId)
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        return formatter.format(currentTime)
    }

    fun Context.showAppSettings(context: Activity) {
        val bundle = Bundle()
        bundle.putString(EXTRA_FRAGMENT_ARG_KEY, EXTRA_SYSTEM_ALERT_WINDOW)
        val uri = Uri.fromParts("package", packageName, null)
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(uri)
            .putExtra(EXTRA_FRAGMENT_ARG_KEY, EXTRA_SYSTEM_ALERT_WINDOW)
            .putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, bundle)
        context.startActivityForResult(intent, REQUEST_APP_SETTINGS)
    }

    fun formatTimeForSWNotification(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun Context.hasPermission(permId: Int) = ContextCompat.checkSelfPermission(
        this, getPermissionString(permId)
    ) == PackageManager.PERMISSION_GRANTED


    private fun getPermissionString(id: Int) = when (id) {
        PERMISSION_POST_NOTIFICATIONS -> Manifest.permission.POST_NOTIFICATIONS
        else -> ""
    }
}