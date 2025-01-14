package com.valentinConTilde.onmywayapp.services.locationService

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class DefaultLocationClient (
    private val context: Context, //the app's context
    private val client: FusedLocationProviderClient //dependency to get the user's location by android
): LocationClient {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {

        //1st thing we do is check if we have the permission to get the location

        //we have a callback that is fired constantly, so we better make it into
        //a flow, so we can collect the location updates, like that, they can have some kind of lifecycle
        return callbackFlow {

            //this is the function we defined in ContextExt.kt
            if(!context.hasLocationPermission()) {
                throw LocationClient.LocationException("Missing location permission")
            }

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if(!isGPSEnabled && !isNetworkEnabled) {
                throw LocationClient.LocationException("GPS is disabled")
            }

            //we create a location request, that will be used to get the user's location
            val request = LocationRequest.Builder(interval)
                .setMinUpdateIntervalMillis(interval)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            //Sets the fastest allowed interval of location
            // updates. Location updates may arrive faster than
            // the desired interval (setIntervalMillis(long)),
            // but will never arrive faster than specified here.

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    //the result contains a locations list
                    //the last element of that list is the most recent location
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let {
                        //we send the location to the flow
                            location -> launch { send(location) }
                    }
                }
            }

            //we request the location updates
            //we start the location updates
            client.requestLocationUpdates(
                request.build(),
                locationCallback,
                Looper.getMainLooper()
            )

            //when the flow stops, I have to remove the location updates
            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }

        }
    }

    /*
    this how we transform our location callback into a very
    convenient class with a single responsibility, with a nice
    abstraction which we can use wherever we have to in our code
     */
}