package com.wasa.meterreading

import android.R
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.wasa.meterreading.data.api.ApiHelper
import com.wasa.meterreading.data.api.RetrofitBuilder
import com.wasa.meterreading.data.models.responses.DDRResponse
import com.wasa.meterreading.data.models.responses.RetrieveJobsResponse
import com.wasa.meterreading.databinding.ActivityHomeBinding
import com.wasa.meterreading.utils.Status
import com.wasa.meterreading.viewmodel.HomeViewModel
import com.wasa.meterreading.viewmodel.HomeViewModelFactory

class HomeActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityHomeBinding
    lateinit var viewModel: HomeViewModel
    private lateinit var viewModelFactory: HomeViewModelFactory
    private var selectedDdrID = -1
    private var selectedJobID = -1
    private lateinit var currentDdrList: List<DDRResponse.DDR>
    private lateinit var currentJobs: List<RetrieveJobsResponse.RetrieveJobsResponse1Item>

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            binding = ActivityHomeBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Spinner click listener
            binding.ddrSpinner.onItemSelectedListener = this
            binding.jobsSpinner.onItemSelectedListener = this

            binding.apply {
                btnFetch.setOnClickListener {
                    if (etConsumerCode.text.toString().trim().isEmpty())
                        Toast.makeText(this@HomeActivity, "Please enter consumer code", Toast.LENGTH_LONG).show()
                    else if (selectedDdrID == -1)
                        Toast.makeText(this@HomeActivity, "Please select DDR from dropdown", Toast.LENGTH_LONG).show()
                    else
                        receiverConsumer(etConsumerCode.text.toString(), selectedDdrID)
                }
                btnUpload.setOnClickListener {
                    if (etConsumerCode.text.toString().trim().isEmpty())
                        Toast.makeText(this@HomeActivity, "Please enter consumer code", Toast.LENGTH_LONG).show()
                    if (etRemarks.text.toString().trim().isEmpty())
                        Toast.makeText(this@HomeActivity, "Please write remarks", Toast.LENGTH_LONG).show()
                    else if (selectedJobID == -1)
                        Toast.makeText(this@HomeActivity, "Please select Reading", Toast.LENGTH_LONG).show()
                    else
                        uploadReading(etConsumerCode.text.toString(), etReading.text.toString().toInt(), selectedJobID, etRemarks.text.toString(), 33.366890, -80.313263)
                }
            }

            val apiHelper = ApiHelper(RetrofitBuilder.apiService)
            viewModelFactory = HomeViewModelFactory(apiHelper)
            viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

            loadDDR()
            loadJobs()
        } catch (e: Exception) {
            val exception = "[Exception in HomeActivity:onCreate] [${e.localizedMessage}]".trimIndent()
            Toast.makeText(this@HomeActivity, exception, Toast.LENGTH_LONG).show()
        }
    }

    private fun loadDDR() {
        try {
            viewModel.getDDR().observe(this) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            //progressBar.visibility = View.GONE

                            val ddrResponse = it
                            val ddrList = ArrayList<String>()
                            currentDdrList = ddrResponse.data?.dDR!!
                            for (ddr in currentDdrList) {
                                ddrList.add(ddr.dDRDesc)
                            }

                            // Creating adapter for spinner
                            val dataAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.simple_spinner_item, ddrList)

                            // Drop down layout style - list view with radio button
                            dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

                            // attaching data adapter to spinner
                            binding.ddrSpinner.adapter = dataAdapter
                        }
                        Status.ERROR -> {
                            //progressBar.visibility = View.GONE
                            Toast.makeText(this@HomeActivity, "Error in getDDR :${it.message.toString()}", Toast.LENGTH_LONG).show()
                        }
                        Status.LOADING -> {
                            //progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        } catch (e: Exception) {
            val exception = "[Exception in HomeActivity:loadDDR] [${e.localizedMessage}]".trimIndent()
            Toast.makeText(this@HomeActivity, exception, Toast.LENGTH_LONG).show()
        }
    }

    private fun loadJobs() {
        try {
            viewModel.retrieveJobs().observe(this) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            //progressBar.visibility = View.GONE

                            val jobs = it
                            val jobList = ArrayList<String>()
                            currentJobs = jobs.data!!
                            for (i in currentJobs.indices) {
                                jobList.add(jobs.data[i].jSDetail)
                            }

                            // Creating adapter for spinner
                            val dataAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.simple_spinner_item, jobList)

                            // Drop down layout style - list view with radio button
                            dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

                            // attaching data adapter to spinner
                            binding.jobsSpinner.adapter = dataAdapter
                        }
                        Status.ERROR -> {
                            //progressBar.visibility = View.GONE
                            Toast.makeText(this@HomeActivity, "Error in getDDR :${it.message.toString()}", Toast.LENGTH_LONG).show()
                        }
                        Status.LOADING -> {
                            //progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        } catch (e: Exception) {
            val exception = "[Exception in HomeActivity:loadJobs] [${e.localizedMessage}]".trimIndent()
            Toast.makeText(this@HomeActivity, exception, Toast.LENGTH_LONG).show()
        }
    }

    private fun receiverConsumer(consumerCode: String, ddrId: Int) {
        try {
            viewModel.getCustomers(consumerCode, ddrId).observe(this) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            //progressBar.visibility = View.GONE
//                            binding.etConsumerCode.isEnabled = false
                            val consumer = it
                            val detail = consumer.data!!.customerDetails
                            binding.tvConsumerDetail.text = "Name: ${detail.name} \nAddress: ${detail.address} \nMeter# ${detail.meter} \nAndroidCode: ${detail.androidCode}"
                        }
                        Status.ERROR -> {
                            //progressBar.visibility = View.GONE
                            Toast.makeText(this@HomeActivity, "Error in receiverConsumer :${it.message.toString()}", Toast.LENGTH_LONG).show()
                        }
                        Status.LOADING -> {
                            //progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        } catch (e: Exception) {
            val exception = "[Exception in HomeActivity:receiverConsumer] [${e.localizedMessage}]".trimIndent()
            Toast.makeText(this@HomeActivity, exception, Toast.LENGTH_LONG).show()
        }
    }

    private fun uploadReading(consumerCode: String, reading: Int, jsId: Int, remarks: String, latitude: Double, longitude: Double) {
        try {
            viewModel.uploadReading(consumerCode, reading, jsId, remarks, latitude, longitude).observe(this) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            //progressBar.visibility = View.GONE
                            binding.apply {
                                etConsumerCode.isEnabled = true
                                etConsumerCode.text.clear()
                                etRemarks.text.clear()
                                etReading.text.clear()
                                tvConsumerDetail.text = "Customer Detail"
                            }

                            selectedJobID = -1
                            selectedDdrID = -1
                            val uploaded = it
                            Toast.makeText(this@HomeActivity, uploaded.data?.result, Toast.LENGTH_LONG).show()
                        }
                        Status.ERROR -> {
                            //progressBar.visibility = View.GONE
                            Toast.makeText(this@HomeActivity, "Error in receiverConsumer :${it.message.toString()}", Toast.LENGTH_LONG).show()
                        }
                        Status.LOADING -> {
                            //progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        } catch (e: Exception) {
            val exception = "[Exception in HomeActivity:uploadReading] [${e.localizedMessage}]".trimIndent()
            Toast.makeText(this@HomeActivity, exception, Toast.LENGTH_LONG).show()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // On selecting a spinner item
        try {

            val item: String = parent?.getItemAtPosition(position).toString()
            for (ddr in currentDdrList) {
                if (ddr.dDRDesc == item)
                    selectedDdrID = ddr.id
            }

            for (job in currentJobs) {
                if (job.jSDetail == item)
                    selectedJobID = job.id
            }
            // Showing selected spinner item
            Toast.makeText(this@HomeActivity, "Selected: $item", Toast.LENGTH_LONG).show()
        }catch (e:Exception){

        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(this@HomeActivity, "No Selected", Toast.LENGTH_LONG).show()
    }
}