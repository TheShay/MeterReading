package com.wasa.meterreading

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.*

class GPSTracker(private val mContext: Context) : LocationListener {
    // Get Class Name
    private val tag = GPSTracker::class.java.name

    var isGPSEnabled = false
    var isNetworkEnabled = false
    var isGPSTrackingEnabled = false

    var location: Location? = null
    var latitude = 0.0
    var longitude = 0.0

    // How many Geocoder should return our GPSTracker
    private var geocoderMaxResults = 1

    // The minimum distance to change updates in meters
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters

    // The minimum time between updates in milliseconds
    private val minimumTimeForUpdates = (1000 * 30 * 1).toLong()

    private lateinit var locationManager: LocationManager

    // Store LocationManager.GPS_PROVIDER or LocationManager.NETWORK_PROVIDER information
    private lateinit var providerInfo: String

    init {
        initialization()
    }

    private fun initialization() {
        try {
            locationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            // Try to get location if you GPS Service is enabled
            if (isGPSEnabled) {
                isGPSTrackingEnabled = true
                providerInfo = LocationManager.GPS_PROVIDER
            } else if (isNetworkEnabled) {
                isGPSTrackingEnabled = true
                providerInfo = LocationManager.NETWORK_PROVIDER
            }

            // Application can use GPS or Network Provider
            if (providerInfo.isNotEmpty()) {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    return
                }
                locationManager.requestLocationUpdates(
                    providerInfo,
                    minimumTimeForUpdates,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                    this
                )

                locationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager
                val providers: List<String> = locationManager.getProviders(true)
                var bestLocation: Location? = null
                for (provider in providers) {
                    val l: Location = locationManager.getLastKnownLocation(provider) ?: continue
                    if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                        // Found best last known location: %s", l);
                        bestLocation = l
                    }
                }
                location = bestLocation
                //location = locationManager.getLastKnownLocation(providerInfo)
                updateGPSCoordinates()
            }
        } catch (e: Exception) {
            val exception = "[Exception in GPSTracker:initialization] [${e.localizedMessage}]".trimIndent()
            Toast.makeText(mContext, exception, Toast.LENGTH_LONG).show()
        }
    }

    fun getLocation(): String {
        try {
            val currentLatitude = latitude.toString()
            val currentLongitude = longitude.toString()
            val country = getCountryName()
            val city = getLocality()
            val addressLine = getAddressLine()
            return ("Lat :$currentLatitude \nLong: $currentLongitude \nCountry: $country \nCity: $city \nAddress: $addressLine")
        } catch (e: Exception) {
            val exception = "[Exception in GPSTracker:getLocation] [${e.localizedMessage}]".trimIndent()
            Toast.makeText(mContext, exception, Toast.LENGTH_LONG).show()
        }
        return ""
    }


    private fun updateGPSCoordinates() {
        if (location != null) {
            latitude = location!!.latitude
            longitude = location!!.longitude
        }
    }

    @JvmName("getLatitude1")
    fun getLatitude(): Double {
        if (location != null) {
            latitude = location!!.latitude
        }
        return latitude
    }

    @JvmName("getLongitude1")
    fun getLongitude(): Double {
        if (location != null) {
            longitude = location!!.longitude
        }
        return longitude
    }

    fun getIsGPSTrackingEnabled(): Boolean {
        return isGPSTrackingEnabled
    }

    fun stopUsingGPS() {
        locationManager.removeUpdates(this@GPSTracker)
    }

    fun showSettingsAlert() {
        try {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(mContext)

            alertDialog.setTitle("GPS Alert")
            alertDialog.setMessage("Please Turn ON GPS")

            //On Pressing Setting button
            alertDialog.setPositiveButton("Settings",
                DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    mContext.startActivity(intent)
                })

            //On pressing cancel button
            alertDialog.setNegativeButton(R.string.cancel) { dialog, which -> dialog.cancel() }
            alertDialog.show()
        } catch (e: Exception) {
            val exception = "[Exception in GPSTracker:showSettingsAlert] [${e.localizedMessage}]".trimIndent()
            Toast.makeText(mContext, exception, Toast.LENGTH_LONG).show()
        }
    }

    private fun getGeocoderAddress(): List<Address?>? {
        if (location != null) {
            val geocoder = Geocoder(mContext, Locale.ENGLISH)
            try {
                return geocoder.getFromLocation(latitude, longitude, geocoderMaxResults)
            } catch (e: IOException) {
                Log.e(tag, "Impossible to connect to Geocoder", e)
                val exception = "[Exception in GPSTracker:getGeocoderAddress] [${e.localizedMessage}]".trimIndent()
                Toast.makeText(mContext, exception, Toast.LENGTH_LONG).show()
            }
        }
        return null
    }

    private fun getAddressLine(): String {
        val addresses = getGeocoderAddress()
        return if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            address!!.getAddressLine(0)
        } else
            ""
    }

    private fun getLocality(): String? {
        val addresses = getGeocoderAddress()
        return if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            address!!.locality
        } else {
            null
        }
    }

    private fun getCountryName(): String? {
        val addresses = getGeocoderAddress()
        return if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            address!!.countryName
        } else {
            null
        }
    }

    override fun onLocationChanged(location: Location) {
        try {
            latitude = location.latitude
            longitude = location.longitude
        } catch (e: Exception) {
            val exception = "[Exception in GPSTracker:onLocationChanged] [${e.localizedMessage}]".trimIndent()
            Toast.makeText(mContext, exception, Toast.LENGTH_LONG).show()
        }
    }

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
}