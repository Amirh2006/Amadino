package com.amadino.app.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Extension function to check if SEND_SMS permission is granted.
 */
fun Context.hasSmsPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.SEND_SMS
    ) == PackageManager.PERMISSION_GRANTED
}