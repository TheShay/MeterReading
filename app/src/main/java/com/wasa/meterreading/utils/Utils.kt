package com.wasa.meterreading.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class Utils {
    companion object {
        var BASE_URL = "http://meterreading.sigmasolutions.pk/api/"
        var AUTH_TOKEN = "1|d2IdY6mQhtVb9ki8Z4xQWJXd0lKn8KHmA9GwXjQV"
    }

    fun snack(context: Context,view: View, message: String, duration: Int = Snackbar.LENGTH_LONG) {
        try {
            Snackbar.make(view, message, duration).show()
        } catch (e: Exception) {
            val exception = "Exception in Utils:snack: " + e.localizedMessage
           showToast(context,exception)
        }
    }

    fun showToast(context: Context, message: String) {
        try {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            val exception = "Exception in Utils:showToast: " + e.localizedMessage
        }
    }

    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    /*----------------------------------------------------------------getHHMMSSms---------------------------------------------------------------------------*/
    fun getHHMMSSms(): String {
        val calendar = Calendar.getInstance(Locale.US)
        return String.format(Locale.US, "%1\$tm", calendar) +
                "/" + String.format(Locale.US, "%1\$td", calendar) +
                " " + String.format(Locale.US, "%1\$tH", calendar) +  //Hour
                ":" + String.format(Locale.US, "%1\$tM", calendar) +  //Min
                ":" + String.format(Locale.US, "%1\$tS", calendar) +  //Second
                "." + String.format(Locale.US, "%1\$tL", calendar) //Milliseconds
    }

    fun currentDateTime(secondsToAdd: Int): String {
        var updatedTime = Date()
        if (secondsToAdd > 0) {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            calendar.add(Calendar.SECOND, secondsToAdd)
            updatedTime = calendar.time
        }

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.US)
        val currentDate = sdf.format(updatedTime)
        return (currentDate)
    }

    /*fun checkPermission(): Boolean {
        val camera = ContextCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.CAMERA)
        val audio = ContextCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.RECORD_AUDIO)
        return (camera == PackageManager.PERMISSION_GRANTED && audio == PackageManager.PERMISSION_GRANTED)
    }*/

    fun requestPermission(activity: Activity, permsRequestCode: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), permsRequestCode)
    }
}