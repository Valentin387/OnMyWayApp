package com.valentinConTilde.onmywayapp.ui.main.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.valentinConTilde.onmywayapp.R

class CustomInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {

    private val windowView: View = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null)

    override fun getInfoWindow(marker: Marker): View? {
        // Return null if you don't want to customize the default frame
        return null
    }

    override fun getInfoContents(marker: Marker): View? {
        // Customize the content of the info window here
        val titleTextView: TextView = windowView.findViewById(R.id.info_title)
        val snippetTextView: TextView = windowView.findViewById(R.id.info_snippet)

        titleTextView.text = marker.title ?: "No Title"
        snippetTextView.text = marker.snippet ?: "No Additional Info"

        return windowView
    }
}
