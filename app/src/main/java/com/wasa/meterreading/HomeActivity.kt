package com.wasa.meterreading

import android.R
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Base64
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.wasa.meterreading.data.api.ApiHelper
import com.wasa.meterreading.data.api.RetrofitBuilder
import com.wasa.meterreading.data.models.responses.DDRResponse
import com.wasa.meterreading.data.models.responses.RetrieveJobsResponse
import com.wasa.meterreading.databinding.ActivityHomeBinding
import com.wasa.meterreading.utils.Status
import com.wasa.meterreading.viewmodel.HomeViewModel
import com.wasa.meterreading.viewmodel.HomeViewModelFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class HomeActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityHomeBinding
    lateinit var viewModel: HomeViewModel
    private lateinit var viewModelFactory: HomeViewModelFactory
    private var selectedDdrID = -1
    private var selectedJobID = -1
    private var currentDdrList: List<DDRResponse.DDR> = arrayListOf()
    private var currentJobs: List<RetrieveJobsResponse.RetrieveJobsResponse1Item> = arrayListOf()
    var currentPhotoPath: String? = null
    val REQUEST_IMAGE_CAPTURE = 1
    var img_str = ""
    var outPutfileUri: Uri? = null

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
                    else if (selectedDdrID == -1) {
                        if (currentDdrList.isNotEmpty()) {
                            selectedDdrID = currentDdrList[0].id
                            receiverConsumer(etConsumerCode.text.toString(), selectedDdrID)
                        } else
                            Toast.makeText(this@HomeActivity, "Please select DDR from dropdown", Toast.LENGTH_LONG).show()
                    } else
                        receiverConsumer(etConsumerCode.text.toString(), selectedDdrID)
                }
                btnUpload.setOnClickListener {
                    if (etConsumerCode.text.toString().trim().isEmpty())
                        Toast.makeText(this@HomeActivity, "Please enter consumer code", Toast.LENGTH_LONG).show()
                    if (etRemarks.text.toString().trim().isEmpty())
                        Toast.makeText(this@HomeActivity, "Please write remarks", Toast.LENGTH_LONG).show()
                    else if (img_str.isEmpty())
                        Toast.makeText(this@HomeActivity, "Please select Image", Toast.LENGTH_LONG).show()
                    else if (selectedJobID == -1) {
                        if (currentJobs.isNotEmpty()) {
                            selectedJobID = currentJobs[0].id
                            uploadReading(etConsumerCode.text.toString(), etReading.text.toString().toInt(), selectedJobID, etRemarks.text.toString(), 33.366890, -80.313263)
                        } else
                            Toast.makeText(this@HomeActivity, "Please select Reading", Toast.LENGTH_LONG).show()
                    } else
                        uploadReading(etConsumerCode.text.toString(), etReading.text.toString().toInt(), selectedJobID, etRemarks.text.toString(), 33.366890, -80.313263)
                }

                btnSelect.setOnClickListener {
                    dispatchTakePictureIntent()
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
                                img_str = ""
                                /*val icon = BitmapFactory.decodeResource(
                                    this@HomeActivity.resources,
                                    R.drawable.logo
                                )*/
                                ivPic.setImageBitmap(null)
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

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath
        return image
    }

    private fun resizeBase64Image(image: Bitmap): Bitmap {
//        byte[] encodeByte = Base64.decode(base64image.getBytes(), Base64.DEFAULT);
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPurgeable = true;
//        Bitmap image = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length, options);
        if (image.height <= 800 && image.width <= 800) {
            return image
        }
        val width = image.width
        val height = image.height
        //        float ratioBitmap = (float) width / (float) height;
        val maxWidth = 800
        val maxHeight = 800
        //        float ratioMax = (float) maxWidth / (float) maxHeight;
        var finalWidth = maxWidth
        var finalHeight = maxHeight
        if (width > height) {
            // landscape
            val ratio = width.toFloat() / height.toFloat()
            finalHeight = (maxHeight.toFloat() / ratio).toInt()
        } else if (height > width) {
            // portrait
            val ratio = height.toFloat() / width.toFloat()
            finalWidth = (maxWidth.toFloat() / ratio).toInt()
        }

//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
//
//        byte[] b = baos.toByteArray();
//        System.gc();
//        return Base64.encodeToString(b, Base64.NO_WRAP);
        return Bitmap.createScaledBitmap(image, finalWidth, finalHeight, false)
    }

    fun rotateBitmap(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
                ex.printStackTrace()
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                outPutfileUri = FileProvider.getUriForFile(
                    this,
                    "com.example.android.fileprovider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri)
                takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        updateProfileView()
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                val uri: String = outPutfileUri.toString()
                //            Log.e("uri-:", uri);
//            Toast.makeText(this, outPutfileUri.toString(), Toast.LENGTH_LONG).show();

//            Bitmap myBitmap = BitmapFactory.decodeFile(uri);
//             mImageView.setImageURI(Uri.parse(uri));   OR drawable make image strechable so try bleow also
                try {
                    val bitmap = resizeBase64Image(
                        MediaStore.Images.Media.getBitmap(
                            this.contentResolver,
                            outPutfileUri
                        )
                    )
                    //val bitmap = RotateBitmap (MediaStore.Images.Media.getBitmap(this.getContentResolver(), outPutfileUri), 90);
                    val d: Drawable = BitmapDrawable(resources, bitmap)
                    binding.ivPic.setImageDrawable(d)
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 40, stream)
                    val image = stream.toByteArray()
                    img_str = Base64.encodeToString(image, 0)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
              //Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            profile_image.setImageBitmap(imageBitmap);
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
//            byte[] image = stream.toByteArray();
//
//            img_str = Base64.encodeToString(image, 0);
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(this@HomeActivity, "No Selected", Toast.LENGTH_LONG).show()
    }
}