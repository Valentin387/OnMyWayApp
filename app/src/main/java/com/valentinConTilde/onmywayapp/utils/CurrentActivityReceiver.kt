package com.valentinConTilde.onmywayapp.utils

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import com.valentinConTilde.onmywayapp.services.locationService.LocationService

class CurrentActivityReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("CurrentActivityReceiver", "onReceive")
        val result = ActivityRecognitionResult.extractResult(intent)
        if (result != null) {
            val mostProbableActivity = result.mostProbableActivity
            Log.d("CurrentActivityReceiver", "Detected activity: ${mostProbableActivity.type}")

            when (mostProbableActivity.type) {
                DetectedActivity.WALKING -> {
                    sendStartingIntent(context)
                    Log.d("CurrentActivityReceiver", "WALKING detected (Periodic Check)")
                }
                DetectedActivity.ON_FOOT -> {
                    sendStartingIntent(context)
                    Log.d("CurrentActivityReceiver", "ON_FOOT detected (Periodic Check)")
                }
                DetectedActivity.RUNNING -> {
                    sendStartingIntent(context)
                    Log.d("CurrentActivityReceiver", "RUNNING detected (Periodic Check)")
                }
                DetectedActivity.ON_BICYCLE -> {
                    sendStartingIntent(context)
                    Log.d("CurrentActivityReceiver", "ON_BICYCLE detected (Periodic Check)")
                }
                DetectedActivity.IN_VEHICLE -> {
                    sendStartingIntent(context)
                    Log.d("CurrentActivityReceiver", "IN_VEHICLE detected (Periodic Check)")
                }
                DetectedActivity.STILL -> {
                    stopLocationTracking(context)
                    Log.d("CurrentActivityReceiver", "STILL detected (Periodic Check)")
                }

                // Handle other activity types if needed
            }
        }else{
            Log.d("CurrentActivityReceiver", "ActivityRecognitionResult.extractResult(intent) is null")
        }
    }

    private fun sendStartingIntent(context: Context) {
        try {
            val intent = Intent(context, LocationService::class.java).apply {
                action = LocationService.ACTION_START
            }
            Log.d("CurrentActivityReceiver", "Starting location tracking")
            // Start the foreground service
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, intent)
            } else {
                context.startService(intent)
            }
        } catch (e: Exception) {
            Log.e("CurrentActivityReceiver", "Error starting location tracking: ${e.message}")
        }
        //context.startService(intent)
        //ContextCompat.startForegroundService(context, intent)
    }

    private fun stopLocationTracking(context: Context) {
        try {
            val intent = Intent(context, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
            }
            Log.d("CurrentActivityReceiver", "Stopping location tracking")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, intent)
            } else {
                context.startService(intent)
            }
        } catch (e: Exception) {
            Log.e("CurrentActivityReceiver", "Error stopping location tracking: ${e.message}")
        }

        //context.startService(intent)
        //ContextCompat.startForegroundService(context, intent)
    }
}