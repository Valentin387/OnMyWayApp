package com.valentinConTilde.onmywayapp.ui.main.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.valentinConTilde.onmywayapp.R
import com.valentinConTilde.onmywayapp.data.DTO.UserLocationInMap
import com.valentinConTilde.onmywayapp.data.models.User
import com.valentinConTilde.onmywayapp.databinding.FragmentHomeBinding
import com.valentinConTilde.onmywayapp.io.WebSocketClient
import com.valentinConTilde.onmywayapp.utils.EncryptedPrefsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import com.valentinConTilde.onmywayapp.data.customParsers.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var webSocketClient: WebSocketClient


    private val homeViewModel: HomeViewModel by activityViewModels()

    private lateinit var googleMap: GoogleMap
    private val userMarkers = mutableMapOf<String, Marker>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("HomeFragment", "onCreateView")
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        //Get the map fragment and set up the map
        val mapFragment = childFragmentManager.findFragmentById(com.valentinConTilde.onmywayapp.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("HomeFragment", "onViewCreated")

        // Initialize WebSocketClient
        webSocketClient = WebSocketClient()

        // Get the userId from preferences
        val userId = fetchUserMongoIDFromPreferences()

        // Connect to WebSocket
        webSocketClient.connectToWebSocket(userId = userId) { message ->
            // Handle incoming message
            // Update UI or handle the message as needed
            Log.d("WebSocket", "Received message: $message")
            try{
                val userLocationInMap = parseUserLocationInMap(message)
                Log.d("WebSocket","Deserialized object: $userLocationInMap")
                if(userLocationInMap != null){
                    // Ensure marker updates run on the main thread
                    requireActivity().runOnUiThread {
                        updateUserMarker(userLocationInMap)
                    }
                }
            }catch(e: Exception){
                Log.e("WebSocket", "Error deserializing message: ${e.message}")
            }
        }

        // Handle send button click
    /*    sendButton.setOnClickListener {
            val message = messageInput.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                webSocketClient.sendMessage(message)
            }
        }*/

    }

    private fun fetchUserMongoIDFromPreferences() : String {
        //check is there is a token stored
        //val preferences = requireActivity().getSharedPreferences("defaultPrefs", MODE_PRIVATE)
        val preferences = EncryptedPrefsManager.getPreferences()
        val userString = preferences.getString("user", null)
        val gson = Gson()
        val user = gson.fromJson(userString, User::class.java)
        return user.id ?: ""
    }

    private fun updateUserMarker(userLocation: UserLocationInMap) {
        val userId = userLocation.userId ?: return // Skip if userId is null
        val position = LatLng(userLocation.latitude.toDouble(), userLocation.longitude.toDouble())

        // Check if a marker already exists for this user
        val existingMarker = userMarkers[userId]
        if (existingMarker != null) {
            // Update the marker's position
            existingMarker.position = position
        } else {
            val snippetText = """
                Speed: ${userLocation.speed} m/s
                Battery: ${userLocation.batteryPercentage ?: "N/A"}%
                Accuracy: ${userLocation.locationAccuracy} m
                App Version: ${userLocation.applicationVersion}
            """.trimIndent()
            // Create a new marker with a custom icon
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title("${userLocation.givenName} ${userLocation.familyName}")
                    .snippet(snippetText)
                    .icon(BitmapDescriptorFactory.defaultMarker(getRandomMarkerColor()))
            )
            if (marker != null) {
                userMarkers[userId] = marker
            }
        }
    }

    private fun getRandomMarkerColor(): Float {
        // Hue values range from 0 to 360 (covering the entire color wheel)
        return (0..360).random().toFloat()
    }

    override fun onResume() {
        super.onResume()
        Log.d("HomeFragment", "onResume")
        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)?.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.d("HomeFragment", "onPause")
        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)?.onPause()
    }

    override fun onStop() {
        super.onStop()
        Log.d("HomeFragment", "onStop")
        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("HomeFragment", "onDestroy")
        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)?.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        Log.d("HomeFragment", "onStart")
        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)?.onStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("HomeFragment", "onDestroyView")
        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)?.onDestroyView()

        _binding = null
        webSocketClient.closeWebSocket()  // Close WebSocket when fragment is destroyed
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true // Enable the native button for my location

        // Get the last known location and move the camera
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null && location.accuracy < 10) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                val marker = googleMap.addMarker(
                    MarkerOptions()
                        .position(currentLatLng)
                        .title("You")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                )
                marker?.showInfoWindow()
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
                Log.d("locationAccuracy", location.accuracy.toString())
            }
        }

    }
}