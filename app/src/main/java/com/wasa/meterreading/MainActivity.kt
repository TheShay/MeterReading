package com.wasa.meterreading

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.wasa.meterreading.viewmodel.LoginViewModelFactory
import com.wasa.meterreading.data.api.ApiHelper
import com.wasa.meterreading.data.api.RetrofitBuilder
import com.wasa.meterreading.databinding.ActivityMainBinding
import com.wasa.meterreading.utils.Status
import com.wasa.meterreading.utils.Utils
import com.wasa.meterreading.viewmodel.LoginViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: LoginViewModel
    private lateinit var viewModelFactory: LoginViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            Thread.setDefaultUncaughtExceptionHandler(MyExceptionHandler(this))
            val policy = StrictMode.ThreadPolicy.Builder().detectNetwork().penaltyLog().build()
            StrictMode.setThreadPolicy(policy)

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
            e.printStackTrace()
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
                            //progressBar.visibility = View.GONE
                        }
                        Status.ERROR -> {
                            //progressBar.visibility = View.GONE
                            Toast.makeText(this@MainActivity, "Error in AffiliatesAPI :${it.message.toString()}", Toast.LENGTH_LONG).show()
                        }
                        Status.LOADING -> {
                            //progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        } catch (e: Exception) {
            val exception = "[Exception in DetectorActivity:getLogin] [${e.localizedMessage}]".trimIndent()
        }
    }
}