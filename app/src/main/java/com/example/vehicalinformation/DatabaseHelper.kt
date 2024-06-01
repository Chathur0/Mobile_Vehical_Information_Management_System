package com.example.vehicalinformation

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.mindrot.jbcrypt.BCrypt

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "VehicleDB"
        private const val DATABASE_VERSION = 3 // Increment version for database upgrade
        private const val TABLE_VEHICLES = "Vehicles"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_PRICE_PER_DAY = "pricePerDay"
        private const val COLUMN_IMAGE_PATH = "selectedImagePath"
        private const val COLUMN_CITY = "city"
        // Add these at the beginning of DatabaseHelper class
        private val TABLE_USERS = "users"
        private val COLUMN_USER_ID = "user_id"
        private val COLUMN_USER_NAME = "user_name"
        private val COLUMN_USER_MOBILE = "user_mobile"
        private val COLUMN_USER_PASSWORD = "user_password"
        private val COLUMN_USER_ROLE = "user_role"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_VEHICLE_TABLE = ("CREATE TABLE $TABLE_VEHICLES ("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_TYPE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_PRICE_PER_DAY + " REAL,"
                + COLUMN_IMAGE_PATH + " TEXT,"
                + COLUMN_CITY + " TEXT" + ")")
        db?.execSQL(CREATE_VEHICLE_TABLE)

        val CREATE_USER_TABLE = ("CREATE TABLE $TABLE_USERS ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_NAME + " TEXT,"
                + COLUMN_USER_MOBILE + " TEXT UNIQUE,"
                + COLUMN_USER_PASSWORD + " TEXT,"
                + COLUMN_USER_ROLE + " TEXT)")
        db?.execSQL(CREATE_USER_TABLE)

        // Insert admin user
        val adminPasswordHash = BCrypt.hashpw("12345", BCrypt.gensalt())
        val insertAdmin = "INSERT INTO $TABLE_USERS ($COLUMN_USER_NAME, $COLUMN_USER_MOBILE, $COLUMN_USER_PASSWORD, $COLUMN_USER_ROLE) VALUES ('Chathuranga', '0712984804', '$adminPasswordHash', 'admin')"
        db?.execSQL(insertAdmin)
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int,) {
        if (oldVersion < 3) {
            db?.execSQL("ALTER TABLE $TABLE_VEHICLES ADD COLUMN $COLUMN_CITY TEXT")
        }
    }

    fun addVehicle(name: String, type: String, description: String, pricePerDay: Double,selectedImagePath: String,city: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_TYPE, type)
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_PRICE_PER_DAY, pricePerDay)
            put(COLUMN_IMAGE_PATH, selectedImagePath)
            put(COLUMN_CITY, city)
        }
        db.insert(TABLE_VEHICLES, null, values)
        db.close()
    }

    fun updateVehicle(id: Int, name: String, type: String, description: String, pricePerDay: Double,imagePath: String,city: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_TYPE, type)
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_PRICE_PER_DAY, pricePerDay)
            put(COLUMN_IMAGE_PATH, imagePath)
            put(COLUMN_CITY, city)
        }
        db.update(TABLE_VEHICLES, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    fun deleteVehicle(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_VEHICLES, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    fun getAllVehicles(): List<Vehicle> {
        val vehicleList = ArrayList<Vehicle>()
        val selectQuery = "SELECT * FROM $TABLE_VEHICLES"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val vehicle = Vehicle(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE_PER_DAY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY))
                )
                vehicleList.add(vehicle)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return vehicleList
    }
    fun insertUser(name: String, mobile: String, password: String, role: String): String {
        val db = this.writableDatabase

        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID),
            "$COLUMN_USER_MOBILE = ?",
            arrayOf(mobile),
            null, null, null
        )
        return if (cursor != null && cursor.moveToFirst()) {
            cursor.close()
            "Mobile number already registered"
        }else{
        val values = ContentValues()
        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
        values.put(COLUMN_USER_NAME, name)
        values.put(COLUMN_USER_MOBILE, mobile)
        values.put(COLUMN_USER_PASSWORD, hashedPassword)
        values.put(COLUMN_USER_ROLE, role)
        db.insert(TABLE_USERS, null, values)
        "User registered successfully"
        }
    }

    fun getUser(mobile: String, password: String): User? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID, COLUMN_USER_NAME, COLUMN_USER_MOBILE, COLUMN_USER_PASSWORD, COLUMN_USER_ROLE),
            "$COLUMN_USER_MOBILE = ? ",
            arrayOf(mobile),
            null, null, null
        )
        return if (cursor != null && cursor.moveToFirst()) {
            val storedHash = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD))
            if (BCrypt.checkpw(password, storedHash)) {
            val user = User(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_MOBILE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ROLE))
            )
            cursor.close()
            user
            }else{
                cursor.close()
                null
            }
        } else {
            cursor?.close()
            null
        }
    }

}
