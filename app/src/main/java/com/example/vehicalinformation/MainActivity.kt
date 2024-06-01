package com.example.vehicalinformation

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var addBtn: FloatingActionButton
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var adapter: VehicleAdapter
    private lateinit var userRole: String
    private lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        recyclerView = findViewById(R.id.recyclerView)

        addBtn = findViewById(R.id.add_btn)
        databaseHelper = DatabaseHelper(this)
        userRole = intent.getStringExtra("user_role") ?: "user"
        userName = intent.getStringExtra("user_name") ?: "User"
        recyclerView.layoutManager = LinearLayoutManager(this)
        loadData()
        val usernameText: TextView = findViewById(R.id.username_text)
        usernameText.text = "Welcome, $userName"
        if (userRole != "admin") {
            addBtn.hide()
        } else {
            addBtn.show()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        addBtn.setOnClickListener {
            val intent = Intent(this, AddVehicleActivity::class.java)
            startActivity(intent)
        }

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadData() {
        val vehicleList = databaseHelper.getAllVehicles()
        adapter = VehicleAdapter(vehicleList, this,userRole) { id ->
            databaseHelper.deleteVehicle(id)
            loadData() // Refresh data after deletion
        }
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadData() // Refresh data when returning from AddVehicleActivity or EditVehicleActivity
    }
}
