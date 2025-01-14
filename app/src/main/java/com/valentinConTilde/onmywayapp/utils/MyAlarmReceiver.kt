package com.valentinConTilde.onmywayapp.utils

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.ActivityRecognition


class MyAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        // Initialize the EncryptedPrefsManager
        EncryptedPrefsManager.init(context)

        Log.d("MyAlarmReceiver", "onReceive")
        // Start activity recognition
        val activityRecognitionClient = ActivityRecognition.getClient(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("MyAlarmReceiver", "Requesting activity updates")
            activityRecognitionClient.requestActivityUpdates(
                5000,  // Detection interval in milliseconds
                getPendingIntent(context)
            )
        }
    }

    private fun getPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, CurrentActivityReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            301, // Different request code for current activity
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }
}