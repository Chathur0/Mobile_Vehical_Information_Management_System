package com.example.vehicalinformation

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var mobileEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        nameEditText = findViewById(R.id.edit_text_name)
        mobileEditText = findViewById(R.id.edit_text_mobile)
        passwordEditText = findViewById(R.id.edit_text_password)
        registerButton = findViewById(R.id.button_register)
        databaseHelper = DatabaseHelper(this)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString()

            if(mobileEditText.text.length!=10){
                Toast.makeText(this, "Invalid Mobile Number", Toast.LENGTH_SHORT).show()
            }else{
                val mobile = mobileEditText.text.toString()
                val password = passwordEditText.text.toString()
                handleRegister(name, mobile, password, "user")
            }
        }
    }
    private fun handleRegister(name: String, mobile: String, password: String, role: String) {
        val dbHelper = DatabaseHelper(this)
        val result = dbHelper.insertUser(name, mobile, password, role)

        when (result) {
            "Mobile number already registered" -> {
                Toast.makeText(this, "Mobile number already registered", Toast.LENGTH_SHORT).show()
            }
            "User registered successfully" -> {
                Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
