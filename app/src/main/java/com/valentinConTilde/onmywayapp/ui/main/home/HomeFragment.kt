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
import com.google.android.gms.maps.model.MarkerOptions
import com.valentinConTilde.onmywayapp.R
import com.valentinConTilde.onmywayapp.databinding.FragmentHomeBinding
import com.valentinConTilde.onmywayapp.io.WebSocketClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var webSocketClient: WebSocketClient
    private val messageChannel = Channel<String>()


    private val homeViewModel: HomeViewModel by activityViewModels()

    private lateinit var googleMap: GoogleMap

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
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                Log.d("locationAccuracy", location.accuracy.toString())
            }
        }

    }
}