package com.valentinConTilde.onmywayapp.utils

import android.content.Context
import android.content.Intent
import android.content.BroadcastReceiver
import android.util.Log
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import com.valentinConTilde.onmywayapp.services.locationService.LocationService

class TransitionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (ActivityTransitionResult.hasResult(intent)) {
            Log.d("TransitionReceiver", "ActivityTransitionResult.hasResult(intent)")

            val result00 = ActivityTransitionResult.extractResult(intent)
            for (event in result00?.transitionEvents ?: emptyList()) {
                Log.d("ActivityRecognition", "Transition: ${event.activityType}, ${event.transitionType}")
            }

            val result = ActivityTransitionResult.extractResult(intent)
            result?.transitionEvents?.forEach { event ->
                when (event.activityType) {
                    DetectedActivity.WALKING -> {
                        Log.d("TransitionReceiver", "WALKING")
                        if (event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                            sendStartingIntent(context)
                            Log.d("TransitionReceiver", "WALKING ENTER")
                        }
                        if (event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
                            //stopLocationTracking(context)
                            Log.d("TransitionReceiver", "WALKING EXIT")
                        }
                    }
                    DetectedActivity.STILL -> {
                        Log.d("TransitionReceiver", "STILL")
                        if (event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                            stopLocationTracking(context)
                            Log.d("TransitionReceiver", "STILL ENTER")
                        }
                        if(event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
                            //sendStartingIntent(context)
                            Log.d("TransitionReceiver", "STILL EXIT")
                        }
                    }
                    DetectedActivity.ON_FOOT -> {
                        Log.d("TransitionReceiver", "ON_FOOT")
                        if (event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                            sendStartingIntent(context)
                            Log.d("TransitionReceiver", "ON_FOOT ENTER")
                        }
                        if(event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
                            //stopLocationTracking(context)
                            Log.d("TransitionReceiver", "ON_FOOT EXIT")
                        }
                    }

                    DetectedActivity.IN_VEHICLE -> {
                        Log.d("TransitionReceiver", "IN_VEHICLE")
                        if (event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                            sendStartingIntent(context)
                            Log.d("TransitionReceiver", "IN_VEHICLE ENTER")
                        }
                        if(event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
                            //stopLocationTracking(context)
                            Log.d("TransitionReceiver", "IN_VEHICLE EXIT")
                        }
                    }

                    DetectedActivity.ON_BICYCLE -> {
                        Log.d("TransitionReceiver", "ON_BICYCLE")
                        if (event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                            sendStartingIntent(context)
                            Log.d("TransitionReceiver", "ON_BICYCLE ENTER")
                        }
                        if(event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
                            //stopLocationTracking(context)
                            Log.d("TransitionReceiver", "ON_BICYCLE EXIT")
                        }
                    }
                    DetectedActivity.RUNNING -> {
                        Log.d("TransitionReceiver", "RUNNING")
                        if (event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                            sendStartingIntent(context)
                            Log.d("TransitionReceiver", "RUNNING ENTER")
                        }
                        if(event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
                            //stopLocationTracking(context)
                            Log.d("TransitionReceiver", "RUNNING EXIT")
                        }
                    }
                    DetectedActivity.TILTING -> {
                        Log.d("TransitionReceiver", "TILTING")
                    }
                    DetectedActivity.UNKNOWN -> {
                        Log.d("TransitionReceiver", "UNKNOWN")
                    }



                }
            }
        }else{
            Log.d("ActivityRecognition", "No transition result found.")
        }
    }

    private fun sendStartingIntent(context: Context) {
        val intent = Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_START
        }
        Log.d("TransitionReceiver", "Starting location tracking")
        context.startService(intent)
    }

    private fun stopLocationTracking(context: Context) {
        val intentToStopLocationService = Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
        }
        Log.d("TransitionReceiver", "Stopping location tracking")
        context.startService(intentToStopLocationService)
    }
}