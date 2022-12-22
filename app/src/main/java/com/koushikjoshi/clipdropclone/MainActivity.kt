package com.koushikjoshi.clipdropclone

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var takePictureButton: Button
    private lateinit var imageView: ImageView
    private lateinit var saveButton: Button
    private lateinit var resetButton: Button
    private lateinit var sendBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        takePictureButton = findViewById(R.id.button_take_picture)
        imageView = findViewById(R.id.image_view)
        saveButton = findViewById(R.id.button_save)
        resetButton = findViewById(R.id.reset_button)
        sendBtn = findViewById(R.id.sendBtn)

        // Disable the save button until an image has been selected
        saveButton.isEnabled = false

        takePictureButton.setOnClickListener {
            takePicture()
        }

        saveButton.setOnClickListener {
            saveImage()
        }

        resetButton.setOnClickListener {
            imageView.setImageBitmap(null)
            saveButton.isEnabled = false
        }

        sendBtn.setOnClickListener {
            onSwipeUp()
        }
    }


    fun onSwipeUp() {
        Log.d("Swipe up", "true")
        // Send the image to the Python app when the user performs a swipe-up action
        val imageBitmap = (imageView.drawable as BitmapDrawable).bitmap
        sendImageToPythonApp(imageBitmap)
    }



    private fun sendImageToPythonApp(imageBitmap: Bitmap) {
        // Convert the image to a byte array
        val byteArrayOutputStream = ByteArrayOutputStream()
        val byteArrayOutputStream2 = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

        val width = imageBitmap.width
        val height = imageBitmap.height
        val scaleFactor = 3.0 // Increase the size by a factor of 2

        val scaledBitmap = Bitmap.createScaledBitmap(imageBitmap, (width * scaleFactor).toInt(), (height * scaleFactor).toInt(), false)
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream2)

        val imageBytes = byteArrayOutputStream2.toByteArray()

        // Create the request body
        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageBytes)

        // Send the request to the Python app
        val api2 = Retrofit.Builder()
            .baseUrl("http://192.168.1.15:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(PythonAppAPI::class.java)

        api2.sendImage(requestBody).enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!response.isSuccessful) {
                    // Handle request failure
                    Log.d("status", "Not sent response")
                    Log.d("response", response.toString())
                    return
                }
                // The request was successful, do something with the response if needed
                Log.d("status", "sent successfully")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle request failure
                Log.d("status", "Not sent")
                Log.d("error", t.message.toString())
            }
        })
    }


    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    private fun saveImage() {
        // Save the image to the device's external storage
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val savedImageURL = MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            "Removed Background",
            "Image with background removed using ClipDrop"
        )

        // Display a message indicating that the image was saved
        Toast.makeText(this, "Image saved to device!", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)

            // Enable the save button now that we have an image to save
            saveButton.isEnabled = true

// Send the image to the ClipDrop API to remove the background

            val api = Retrofit.Builder()
                .baseUrl("https://clipdrop-api.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ClipDropAPI::class.java)

            val file = createTempFile(imageBitmap)
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "image_file",
                    file.name,
                    file.asRequestBody("image/jpeg".toMediaType())
                )
                .build()

            val apiKey = resources.getString(R.string.api_key)
            val call = api.removeBackground(apiKey, requestBody.part(0))

            call.enqueue(object : retrofit2.Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (!response.isSuccessful) {
                        // Handle API request failure
                        return
                    }

                    // Update the image view with the returned image
                    val removedBackgroundImage = BitmapFactory.decodeStream(response.body()!!.byteStream())
                    runOnUiThread {
                        imageView.setImageBitmap(removedBackgroundImage)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // Handle API request failure
                }

            })

        }
    }

    private fun createTempFile(bitmap: Bitmap): File {
        val file = File.createTempFile("temp", ".jpg")
        file.deleteOnExit()
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.close()
        return file
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }
}