package com.example.vehicalinformation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddVehicleActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var typeSpinner: Spinner
    private lateinit var descriptionEditText: EditText
    private lateinit var pricePerDayEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var addButton: Button
    private lateinit var vehicleImage: ImageView
    private lateinit var selectImageButton: Button
    private var imageUri: Uri? = null
    private var selectedImagePath: String = ""
    private lateinit var databaseHelper: DatabaseHelper



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_vehicle)

        nameEditText = findViewById(R.id.edit_text_name)
        typeSpinner = findViewById(R.id.spinner_type)
        descriptionEditText = findViewById(R.id.edit_text_description)
        pricePerDayEditText = findViewById(R.id.edit_text_price_per_day)
        cityEditText = findViewById(R.id.edit_text_city)
        addButton = findViewById(R.id.button_add)
        vehicleImage = findViewById(R.id.vehicle_image)
        selectImageButton = findViewById(R.id.select_image_button)
        databaseHelper = DatabaseHelper(this)

        val spinner: Spinner = typeSpinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.spinner_items, // Your string array resource
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        selectImageButton.setOnClickListener {
            // Launch intent to pick image from gallery
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        addButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val type = typeSpinner.selectedItem.toString()
            val description = descriptionEditText.text.toString()
            val pricePerDay = pricePerDayEditText.text.toString().toDouble()
            val city = cityEditText.text.toString()

            if (name.isNotEmpty() && description.isNotEmpty() && pricePerDay >= 0 && city.isNotEmpty()) {
                databaseHelper.addVehicle(name, type, description, pricePerDay,selectedImagePath,city)
                Toast.makeText(this, "Added Successful", Toast.LENGTH_SHORT).show()
                finish() // Close activity after adding vehicle
            }else{
                Toast.makeText(this, "Added Unsuccessful", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null ) {
            val imageUri = data.data
//            selectedImagePath = getPathFromUri(imageUri!!)
            selectedImagePath = imageUri.toString()
            vehicleImage.setImageURI(imageUri)
        }
    }
    private fun getPathFromUri(uri: Uri): String {
        var path = ""
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                path = it.getString(columnIndex)
            }
        }
        return path
    }
    companion object {
        private const val REQUEST_IMAGE_PICK = 100
    }
}