package com.valentinConTilde.onmywayapp.workManagers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.valentinConTilde.onmywayapp.data.models.UserLocation
import com.valentinConTilde.onmywayapp.io.TrackingService
import com.valentinConTilde.onmywayapp.io.UserLocationBroadcastService


class LocationTrackingWorker (context: Context, workerParams: WorkerParameters) : CoroutineWorker(context,
    workerParams
){
    private val userLocationBroadcastService : UserLocationBroadcastService by lazy{
        UserLocationBroadcastService.create(applicationContext)
    }

    override suspend fun doWork(): Result {

        Log.d("LocationTrackingWorker", "Started")

        val userId = inputData.getString("userId")
        val lat = inputData.getString("lat")
        val lon = inputData.getString("lon")
        val date = inputData.getString("date")
        val speed = inputData.getString("speed")
        val persistence = inputData.getBoolean("persistence", false)
        val batteryPercentage = inputData.getString("batteryPercentage")
        val applicationVersion = inputData.getString("applicationVersion")
        val accuracy = inputData.getString("accuracy")

        val userLocation =
            UserLocation(
                userId= userId,
                latitude=lat!!,
                longitude=lon!!,
                date=date!!,
                speed= speed!!,
                persistence = persistence,
                batteryPercentage = batteryPercentage?.toFloat(),
                applicationVersion = applicationVersion!!,
                locationAccuracy = accuracy!!,
            )

        return try {

            val response = userLocationBroadcastService.uploadNewLocation(userLocation)
            if (response.isSuccessful) {
                Log.d("Worker", userLocation.toString())
                Result.success()
            } else {
                Log.e(
                    "Worker",
                    "Error uploading locations: ${response.errorBody()?.string()} retrying"
                )
                Result.retry()
            }


        } catch (e: Exception) {
            Log.e("Worker", "Error uploading location: ${e.message}")
            Result.retry()
        }
    }

    /*    private fun formatTimestamp(timestamp: Long): String {
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val date = Date(timestamp)
            return sdf.format(date)
        }*/
}