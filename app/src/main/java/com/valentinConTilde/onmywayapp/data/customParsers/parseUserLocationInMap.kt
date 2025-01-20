package com.valentinConTilde.onmywayapp.data.customParsers

import android.util.Log
import com.valentinConTilde.onmywayapp.data.DTO.UserLocationInMap

public fun parseUserLocationInMap(rawString: String): UserLocationInMap? {
    return try {
        // Remove the class name and parentheses
        val content = rawString.substringAfter("UserLocationInMap(").substringBeforeLast(")")

        // Split the key-value pairs
        val keyValuePairs = content.split(", ").associate {
            val (key, value) = it.split("=", limit = 2)
            key to value
        }

        // Map the values to the UserLocationInMap object
        UserLocationInMap(
            userId = keyValuePairs["userId"] ?: "",
            givenName = keyValuePairs["givenName"] ?: "N/A",
            familyName = keyValuePairs["familyName"] ?: "N/A",
            latitude = keyValuePairs["latitude"]?: "N/A",
            longitude = keyValuePairs["longitude"]?: "N/A",
            date = keyValuePairs["date"]?: "N/A",
            dateServer = keyValuePairs["dateServer"]?: "N/A",
            speed = keyValuePairs["speed"]?: "N/A",
            persistence = keyValuePairs["persistence"].toBoolean(),
            batteryPercentage = keyValuePairs["batteryPercentage"]?.toFloat() ?: 0.0F,
            applicationVersion = keyValuePairs["applicationVersion"] ?: "Unknown",
            locationAccuracy = keyValuePairs["locationAccuracy"]?: "N/A"
        )
    } catch (e: Exception) {
        Log.e("WebSocket", "Error parsing UserLocationInMap: ${e.message}")
        null
    }
}
