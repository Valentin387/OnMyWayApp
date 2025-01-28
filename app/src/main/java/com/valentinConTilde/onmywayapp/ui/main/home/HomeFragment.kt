package com.valentinConTilde.onmywayapp.ui.main.home

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
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
import com.valentinConTilde.onmywayapp.io.SubscriptionService
import com.valentinConTilde.onmywayapp.io.TrackingService
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var markerDropdownAdapter: ArrayAdapter<String>
    private lateinit var markerDropdown: AutoCompleteTextView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var webSocketClient: WebSocketClient

    private val trackingService: TrackingService by lazy {
        TrackingService.create(requireContext())
    }

    private val homeViewModel: HomeViewModel by activityViewModels()

    private lateinit var googleMap: GoogleMap
    private val userMarkers = mutableMapOf<String, Marker>()
    private val historyMarkers = mutableListOf<Marker>()

    private var startTimestamp = ""
    private var endTimestamp = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("HomeFragment", "onCreateView")
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Initialize Bottom Sheet Behavior
        val bottomSheet = binding.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        // Set Bottom Sheet to be always visible at the bottom
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = 200  // Adjust this to how much of the bottom sheet you want visible by default

        // Listen for Bottom Sheet State Changes (optional)
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        // Bottom Sheet fully expanded
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        // Bottom Sheet collapsed
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        // Bottom Sheet is being dragged
                    }
                    else -> { /* Other states */ }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Optional: Add animations or effects based on slide offset
            }
        })


        //Get the map fragment and set up the map
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("HomeFragment", "onViewCreated")

        // Dropdown Menu Setup
        markerDropdown = binding.markerDropdown
        markerDropdownAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            //userMarkers.values.map { it.title } // Populate with marker titles
            mutableListOf<String>() // Initially empty
        )
        markerDropdown.setAdapter(markerDropdownAdapter)

        // Variable to store selected userId
        var selectedUserId: String? = null
        markerDropdown.setOnItemClickListener { _, _, position, _ ->
            Log.d("markerDropdown", "click detected at position: $position")
            val selectedTitle = markerDropdownAdapter.getItem(position)
            Log.d("markerDropdown", "Selected title: $selectedTitle")
            selectedUserId = userMarkers.entries.firstOrNull { it.value.title == selectedTitle }?.key
            Log.d("markerDropdown", "selectedUserId: $selectedUserId")
        }

        // Date-Time Pickers
        val startDateButton = binding.startDateButton
        val endDateButton = binding.endDateButton

        startDateButton.setOnClickListener {
            showDateTimePicker { selectedDate ->
                startDateButton.text = selectedDate
                Log.d("startDate",selectedDate)
                startTimestamp = convertToMillis(selectedDate).toString()
                Log.d("startTimestamp",startTimestamp)
            }
        }

        endDateButton.setOnClickListener {
            showDateTimePicker { selectedDate ->
                endDateButton.text = selectedDate
                Log.d("endDate",selectedDate)
                endTimestamp = convertToMillis(selectedDate).toString()
                Log.d("endTimestamp",endTimestamp)
            }
        }

        // Search and Clear Buttons
        val searchButton = binding.searchButton
        val clearButton = binding.clearButton

        searchButton.setOnClickListener {
            if (selectedUserId == null) {
                Toast.makeText(requireContext(), "Please select a marker", Toast.LENGTH_SHORT).show()
            } else {
                showLoadingSpinner()
                // Use selectedUserId and date range for search logic
                lifecycleScope.launch(Dispatchers.IO) {
                    fetchAndDisplayUserHistory(selectedUserId!!, startTimestamp, endTimestamp)
                    withContext(Dispatchers.Main) {
                        hideLoadingSpinner()
                    }
                }
            }
        }

        clearButton.setOnClickListener {
            showLoadingSpinner()
            // Clear search results logic
            if (::googleMap.isInitialized) {
                // Clear previous markers
                historyMarkers.forEach { it.remove() }
                historyMarkers.clear()
            }
            hideLoadingSpinner()
        }

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

        lifecycleScope.launch(Dispatchers.IO) {
            try{
                fetchAndDisplayLatestLocations(userId)
                // Update the dropdown menu after fetching is complete
                withContext(Dispatchers.Main) {
                    updateDropdownMenu()
                }

            }catch(e: Exception){
                Log.e("Error fetching the latest locations of mySubscriptions",e.toString())
            }
        }

        // Handle send button click
        /*sendButton.setOnClickListener {
            val message = messageInput.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                webSocketClient.sendMessage(message)
            }
        }*/
    }

    // Function to update the dropdown menu after fetching data
    private fun updateDropdownMenu() {
        Log.d("userMarkers", userMarkers.toString())

        val markerTitles = userMarkers.values.map { it.title }
        Log.d("markerTitles", markerTitles.toString())

        markerDropdownAdapter.clear()
        markerDropdownAdapter.addAll(markerTitles)
        markerDropdownAdapter.notifyDataSetChanged()
    }

    private fun convertToMillis(dateStr: String): Long {
        val sdf = SimpleDateFormat("yyyy-M-d H:mm", Locale.getDefault()) // Match your format
        sdf.timeZone = TimeZone.getDefault() // Ensure it reads local time correctly
        val date = sdf.parse(dateStr) ?: return 0L
        return date.time // Get timestamp in millis
    }

    private fun showDateTimePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        val formattedDate = "$year-${month + 1}-$dayOfMonth $hourOfDay:$minute"
                        onDateSelected(formattedDate)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private suspend fun fetchAndDisplayLatestLocations(userId: String) {
        try {
            val response = trackingService.fetchMyChannelsLatestLatLong(userId)
            if (response.isSuccessful && response.body() != null) {
                val locations = response.body()!!
                // Ensure the map is ready before adding markers
                if (::googleMap.isInitialized) {
                    locations.forEach { userLocation ->
                        requireActivity().runOnUiThread {
                            // Add a marker for each user location
                            updateUserMarker(userLocation)
                        }
                    }
                }
            } else {
                Log.e("HomeFragment", "Error fetching latest locations: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("HomeFragment", "Exception fetching latest locations: ${e.message}")
        }
    }

    private suspend fun fetchAndDisplayUserHistory(userId: String, startMillis: String, endMillis: String) {
        try {
            val response = trackingService.fetchUserHistory(userId, startMillis, endMillis)
            if (response.isSuccessful && response.body() != null) {
                val locations = response.body()!!

                requireActivity().runOnUiThread {
                    if (::googleMap.isInitialized) {
                        // Clear previous markers
                        historyMarkers.forEach { it.remove() }
                        historyMarkers.clear()

                        // Add new markers
                        locations.forEach { userLocation ->
                            val marker = addMarkerToMap(userLocation)
                            marker?.let { historyMarkers.add(it) }
                        }
                    }
                }
            } else {
                Log.e("HomeFragment", "Error fetching user history: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("HomeFragment", "Exception fetching user history: ${e.message}")
        }
    }

    private fun addMarkerToMap(userLocation: UserLocationInMap): Marker? {
        val position = LatLng(userLocation.latitude.toDouble(), userLocation.longitude.toDouble())

        val snippetText = """
        Speed: ${userLocation.speed} m/s
        Battery: ${userLocation.batteryPercentage ?: "N/A"}%
        Accuracy: ${userLocation.locationAccuracy} m
        Date: ${formatDate(userLocation.date)}
        App Version: ${userLocation.applicationVersion}
    """.trimIndent()

        return googleMap.addMarker(
            MarkerOptions()
                .position(position)
                .title("${userLocation.givenName} ${userLocation.familyName}")
                .snippet(snippetText)
                .icon(BitmapDescriptorFactory.defaultMarker(getRandomMarkerColor()))
        )
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
                Date: ${formatDate(userLocation.date)}
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
        // Hue values should strictly range from 0 to 359, ensuring no 360
        return (0 until 360).random().toFloat()
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

    private fun formatDate(timestamp: String): String {
        // Convert the timestamp string to Long
        val timestampLong = timestamp.toLong()

        // Create a SimpleDateFormat instance to define the date format
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        // Convert the timestamp into a Date object
        val date = Date(timestampLong)

        // Format the Date object into a string with the desired format
        return sdf.format(date)
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

        // Set the custom info window adapter
        googleMap.setInfoWindowAdapter(CustomInfoWindowAdapter(requireContext()))

        //Code to extract once your current location and set a marker to it
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            val currentLatLng = LatLng(location.latitude, location.longitude)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
            Log.d("locationAccuracy", location.accuracy.toString())
        }

    }

    private fun showLoadingSpinner() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingSpinner() {
        binding.progressBar.visibility = View.GONE
    }
}