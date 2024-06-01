package com.example.vehicalinformation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var mobileEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mobileEditText = findViewById(R.id.edit_text_mobile)
        passwordEditText = findViewById(R.id.edit_text_password)
        loginButton = findViewById(R.id.button_login)
        registerButton = findViewById(R.id.button_register)
        databaseHelper = DatabaseHelper(this)

        loginButton.setOnClickListener {
            val mobile = mobileEditText.text.toString()
            val password = passwordEditText.text.toString()

            val user = databaseHelper.getUser(mobile, password)
            if (user != null) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("user_role", user.role)
                intent.putExtra("user_name", user.name)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
