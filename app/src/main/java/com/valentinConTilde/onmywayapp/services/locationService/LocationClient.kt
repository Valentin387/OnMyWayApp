package com.valentinConTilde.onmywayapp.services.locationService

import android.location.Location
import kotlinx.coroutines.flow.Flow

//abstraction for location client
interface LocationClient {

    fun getLocationUpdates(interval: Long): Flow<Location>
    //how often we want an update, it returns a flow that emit the new location

    class LocationException(message: String): Exception()
    //if something goes wrong, we can throw an exception
}

/*By keeping the abstraction simple, we can later easily use this
in the view model, because the actual implementation will need a context,
which you don't want to use in the view model.
 */