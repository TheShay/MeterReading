package com.wasa.meterreading

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.wasa.meterreading.data.api.ApiHelper
import com.wasa.meterreading.data.api.RetrofitBuilder
import com.wasa.meterreading.databinding.ActivityMainBinding
import com.wasa.meterreading.utils.Status
import com.wasa.meterreading.utils.Utils
import com.wasa.meterreading.viewmodel.LoginViewModel
import com.wasa.meterreading.viewmodel.LoginViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: LoginViewModel
    private lateinit var viewModelFactory: LoginViewModelFactory
    private val PermissionRequestCode = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            super.onCreate(savedInstanceState)
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            Thread.setDefaultUncaughtExceptionHandler(MyExceptionHandler(this))
            val policy = StrictMode.ThreadPolicy.Builder().detectNetwork().penaltyLog().build()
            StrictMode.setThreadPolicy(policy)
            if (checkPermission()) {
                basicInitialization()
            } else
                requestPermission(PermissionRequestCode)
        } catch (e: Exception) {
            val exception = "[Exception in MainActivity:onCreate] [${e.localizedMessage}]".trimIndent()
            Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
        }
    }

    private fun basicInitialization() {
        try {
            val apiHelper = ApiHelper(RetrofitBuilder.apiService)
            viewModelFactory = LoginViewModelFactory(apiHelper)
            viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

            binding.apply {
                btnLogin.setOnClickListener {
                    if (etUserName.text.toString().isEmpty())
                        Toast.makeText(this@MainActivity, "Please enter user Name", Toast.LENGTH_LONG).show()
                    else if (etPassword.text.toString().isEmpty())
                        Toast.makeText(this@MainActivity, "Please enter Password", Toast.LENGTH_LONG).show()
                    else
                        getLogin(etUserName.text.toString(), etPassword.text.toString())
                }
            }
        } catch (e: Exception) {
            val exception = "[Exception in MainActivity:basicInitialization] [${e.localizedMessage}]".trimIndent()
            Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
        }
    }


    private fun getLogin(userName: String, password: String) {
        try {
            viewModel.login(userName, password).observe(this) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val loginResponse = it
                            Utils.AUTH_TOKEN = loginResponse.data?.result!!.token
                            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                            finish()
                            //progressBar.visibility = View.GONE
                        }
                        Status.ERROR -> {
                            //progressBar.visibility = View.GONE
                            Toast.makeText(this@MainActivity, "Error MainActivity:getLogin API :${it.message.toString()}", Toast.LENGTH_LONG).show()
                        }
                        Status.LOADING -> {
                            //progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        } catch (e: Exception) {
            val exception = "[Exception in MainActivity:getLogin] [${e.localizedMessage}]".trimIndent()
            Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermission(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return (fineLocation == PackageManager.PERMISSION_GRANTED || coarseLocation == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission(permsRequestCode: Int) {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            ), permsRequestCode
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            when (requestCode) {
                PermissionRequestCode -> if (grantResults.isNotEmpty()) {
                    val accessLocationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val coarseLocationAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if ((accessLocationAccepted || coarseLocationAccepted)) {
                        Snackbar.make(findViewById(android.R.id.content), "All permissions granted..!", Snackbar.LENGTH_LONG).show()
                        basicInitialization()
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), "Permission Denied, Please allow all permissions.", Snackbar.LENGTH_LONG).show()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) || shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) || shouldShowRequestPermissionRationale(
                                    Manifest.permission.READ_PHONE_STATE
                                ) || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            ) {
                                requestPermission(PermissionRequestCode)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            val exception = "Exception in MainActivity:onRequestPermissionsResult: " + e.localizedMessage
            Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
        }
    }
}