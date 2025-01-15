package com.valentinConTilde.onmywayapp.services.locationService

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.BatteryManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest.Companion.MIN_BACKOFF_MILLIS
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.valentinConTilde.onmywayapp.BuildConfig
import com.valentinConTilde.onmywayapp.R
import com.valentinConTilde.onmywayapp.data.models.User
import com.valentinConTilde.onmywayapp.utils.EncryptedPrefsManager
import com.valentinConTilde.onmywayapp.workManagers.LocationTrackingWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class LocationService : Service(){

    private var currentLocation: Location? = null
    private val speedThreshold: Float = 1.0f // Threshold for considering you are not significantly moving (in m/s) //1 m/s -> 3.6 km/h
    private var isThisServiceJustStarted = true

    //inner app´s calculation
    private val locationSamples = mutableListOf<Location>()
    private val maxSamples = 2

    //frequency of the location tracking pushes to the tracking worker
    private val locationTrackingSamples = mutableListOf<Location>()
    private val maxTrackingSamples = 10 // this was 10
    private var slowingTrackingServiceFactor = 1 //previously this was slowingSampleTakingFactor

    private val coroutineScope = CoroutineScope(Dispatchers.IO) // Define scope with appropriate dispatcher

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    //it is bound to the lifetime of our service
    /*
    if one job in this scope fails, the others will keep running
     */
    private lateinit var locationClient: LocationClient
    //the abstraction we just created

    override fun onBind(intent: Intent?): IBinder? {
        return null
        //we don't need to bind the service to an activity
    }

    override fun onCreate() {
        super.onCreate()

        Log.d("LocationService", "Service created")

        // Initialize properties here
        locationClient = DefaultLocationClient(
            this,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //this is called for every single intent that is sent to the service
        //we can use the actions of the service
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_content))
            .setSmallIcon(R.drawable.baseline_my_location_24)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)


        startForeground(1, notification.build())
        //the id must be greater than 0
        //be sure to use the same ID for the startForeground and for the getLocationUpdates

        when(intent?.action) {
            ACTION_START -> {
                // Start location tracking
                serviceScope.launch { start() }
            }
            ACTION_STOP -> {
                // Stop location tracking and disconnect the socket
                serviceScope.launch { stop() }
            }
        }

        // Return START_STICKY to keep the service alive and listening for new intents
        return START_STICKY
    }

    private fun start() {
        //Log.d("LocationService", "Service started")

        locationClient.getLocationUpdates(500L) // every  second I get 2 new ones
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                Log.d("LocationService", locationSamples.size.toString())

                locationSamples.add(location)

                if (locationSamples.size >= maxSamples) {
                    processLocationSamples()
                    // Clear the location samples list and start collecting again
                    locationSamples.clear()

                }
            }
            .launchIn(serviceScope) //this binds the callback flow to the lifetime of out service
    }

    //Function to optimize the location samples
    private fun processLocationSamples() {
        Log.d("LocationService", "Processing location samples")
        // Create a copy to avoid concurrent modification
        val locationSamplesCopy = ArrayList(locationSamples)

        // Find the location with the best accuracy (lowest accuracy value), or handle the empty list case
        var bestLocation: Location? = if (locationSamplesCopy.isNotEmpty()) {
            locationSamplesCopy.minByOrNull { it.accuracy }
        } else {
            null // Handle the empty case (e.g., log a message or provide a default)
        }

        if(!isThisServiceJustStarted) {
            //do not consider the best location if the speed is below the threshold
            if ((bestLocation?.speed ?: 0.0f) <= speedThreshold) {
                bestLocation = null
            }
        }else{
            isThisServiceJustStarted = false
        }

        //bestLocation has survived the discard tests
        bestLocation?.let {
            //update the current location
            currentLocation = it

            locationTrackingSamples.add(it)

            //check if it´s time to push this selected point to the server
            if(locationTrackingSamples.size >= (maxTrackingSamples * slowingTrackingServiceFactor)){
                Log.d("LocationService", "persistence = true")
                //send it to the server with persistance = true
                triggerLocationTracking(it.latitude.toString(), it.longitude.toString(), it.speed.toString(), it.accuracy.toString(), true)
                //Log.d("LocationService", "Tracking trigger persistance = true")
                locationTrackingSamples.clear()
            }else{
                Log.d("LocationService", "persistence = false")
                //send it to the server but with persistance = false
                triggerLocationTracking(it.latitude.toString(), it.longitude.toString(), it.speed.toString(), it.accuracy.toString(), false)
                //Log.d("LocationService", "Tracking trigger persistance = false")
            }

        }

    }

    private fun fetchUserMongoIDFromPreferences() : String {
        val preferences = EncryptedPrefsManager.getPreferences()
        val userString = preferences.getString("user", null)
        val gson = Gson()
        val user = gson.fromJson(userString, User::class.java)
        return user.id ?: ""
    }

    private fun triggerLocationTracking(lat: String, lon: String, speed: String, accuracy: String, persistence: Boolean){
        Log.d("LocationService", "Triggering location tracking")

        val userId = fetchUserMongoIDFromPreferences()

        //Get the battery percentage
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            applicationContext.registerReceiver(null, ifilter)
        }

        val batteryPct: Float? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        }
        val appNamePlusVersion : String =
            applicationContext.getString(R.string.app_name) +
                    " - " + BuildConfig.VERSION_CODE +
                    " (" + BuildConfig.VERSION_NAME + ")"

        val argumentsData = Data.Builder()
            .putString("userId", userId)
            .putString("lat", lat)
            .putString("lon", lon)
            .putString("date", System.currentTimeMillis().toString())  //timestamp in milliseconds
            .putString("speed", speed)
            .putBoolean("persistence", persistence)
            .putString("batteryPercentage", batteryPct.toString())
            .putString("applicationVersion", appNamePlusVersion)
            .putString("accuracy", accuracy)
            .build()

        val uploadDataConstraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val workRequest = OneTimeWorkRequestBuilder<LocationTrackingWorker>()
            .setConstraints(uploadDataConstraints)
            .setInputData(argumentsData)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(applicationContext).enqueue(workRequest)

    }


    private fun stop() {
        //Log.d("LocationService", "Service stopped")
        isThisServiceJustStarted = true
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        //we cancel the scope when the service is destroyed
        //so we don't have any memory leaks
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
    /*
    A companion object in Kotlin is a singleton object
    associated with a class, allowing you to define methods
    and properties that belong to the class rather
    than to instances of the class. It serves a similar
    purpose to static members in Java, but with more
    flexibility and power.
    */

}