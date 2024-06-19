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
import com.demo.myapplication.helper.SharedPreferenceHelper

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


    fun Context.showAppSettings(context: Activity) {
        val bundle = Bundle()
        bundle.putString(EXTRA_FRAGMENT_ARG_KEY, EXTRA_SYSTEM_ALERT_WINDOW)
        val uri = Uri.fromParts("package", packageName, null)
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(uri)
            .putExtra(EXTRA_FRAGMENT_ARG_KEY, EXTRA_SYSTEM_ALERT_WINDOW)
            .putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, bundle)
        context.startActivityForResult(intent, REQUEST_APP_SETTINGS)
    }

    fun Context.hasPermission(permId: Int) = ContextCompat.checkSelfPermission(
        this, getPermissionString(permId)
    ) == PackageManager.PERMISSION_GRANTED


    private fun getPermissionString(id: Int) = when (id) {
        PERMISSION_POST_NOTIFICATIONS -> Manifest.permission.POST_NOTIFICATIONS
        else -> ""
    }
}