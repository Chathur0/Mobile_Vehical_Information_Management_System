package com.example.vehicalinformation

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class VehicleAdapter(
    private val vehicleList: List<Vehicle>,
    private val context: Context,
    private val userRole: String,  // Added userRole parameter
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<VehicleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.vehicle_image)
        val name: TextView = view.findViewById(R.id.vehicle_name)
        val type: TextView = view.findViewById(R.id.vehicle_type)
        val description: TextView = view.findViewById(R.id.vehicle_description)
        val pricePerDay: TextView = view.findViewById(R.id.vehicle_price_per_day)
        val cityTextView: TextView = view.findViewById(R.id.vehicle_city)
        val editButton: ImageButton = view.findViewById(R.id.edit_button)
        val deleteButton: ImageButton = view.findViewById(R.id.delete_button)
        val locationBtn : Button = view.findViewById(R.id.location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vehicle, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vehicle = vehicleList[position]
        holder.name.text = "Vehicle Number : ${vehicle.name}"
        holder.type.text = "Vehicle Type : ${vehicle.type}"
        holder.description.text = vehicle.description
        holder.pricePerDay.text = "Price per day : ${vehicle.pricePerDay}"
        holder.cityTextView.text = "Location : ${ vehicle.city }"
        Glide.with(context).load(vehicle.imagePath).into(holder.image)

        // Show or hide edit and delete buttons based on user role
        if (userRole == "admin") {
            holder.editButton.visibility = View.VISIBLE
            holder.deleteButton.visibility = View.VISIBLE

            holder.editButton.setOnClickListener {
                val intent = Intent(context, EditVehicleActivity::class.java).apply {
                    putExtra("id", vehicle.id)
                    putExtra("name", vehicle.name)
                    putExtra("type", vehicle.type)
                    putExtra("description", vehicle.description)
                    putExtra("pricePerDay", vehicle.pricePerDay)
                    putExtra("image", vehicle.imagePath)
                    putExtra("city", vehicle.city)
                }
                context.startActivity(intent)
            }

            holder.deleteButton.setOnClickListener {
                showDeleteConfirmationDialog(vehicle.id)
            }
        } else {
            holder.editButton.visibility = View.GONE
            holder.deleteButton.visibility = View.GONE
        }
        holder.locationBtn.setOnClickListener {
            val intent = Intent(context, MapViewer::class.java).apply {
                putExtra("city", vehicle.city)
            }
            context.startActivity(intent)
        }
    }

    private fun showDeleteConfirmationDialog(vehicleId: Int) {
        AlertDialog.Builder(context).apply {
            setTitle("Delete Vehicle")
            setMessage("Are you sure you want to delete this vehicle?")
            setPositiveButton("Yes") { _, _ -> onDelete(vehicleId) }
            setNegativeButton("No", null)
        }.create().show()
    }

    override fun getItemCount(): Int {
        return vehicleList.size
    }
}

