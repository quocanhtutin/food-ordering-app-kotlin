package com.example.adminfoodorderingapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminfoodorderingapp.databinding.ActivitySignUpBinding
import com.example.adminfoodorderingapp.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

@SuppressLint("StaticFieldLeak")
private lateinit var binding: ActivitySignUpBinding
class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var userName: String
    private lateinit var nameOfRestaurent: String
    private lateinit var database: DatabaseReference
    private lateinit var dtb : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //initialization Firebase Auth
        auth = Firebase.auth
        //initialization Firebase Database
        dtb = FirebaseDatabase.getInstance("https://admin-food-ordering-app-b3f0e-default-rtdb.asia-southeast1.firebasedatabase.app")
        database = dtb.reference

        var locationList = mutableListOf("Dong Anh", "Hoan Kiem", "Dong Da", " Hai Ba Trung", "Ba Dinh")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, locationList)
        binding.listLocation.setAdapter(adapter)

        binding.logintxt.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.createBtn.setOnClickListener {
            //get text from edit text
            email = binding.editEmail.text.toString().trim() //trim the blank space
            userName = binding.editNameOwner.text.toString().trim()
            nameOfRestaurent = binding.editNameRes.text.toString().trim()
            password = binding.editPass.text.toString().trim()

            if (userName.isEmpty() || nameOfRestaurent.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
            }
            else {
                createAccount(email, password)
            }

        }

    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password). addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Account created Successfully", Toast.LENGTH_SHORT).show()
//                val user = auth.currentUser
//                val userId = user?.uid
                saveUserData()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                Toast.makeText(this, "Error creating account", Toast.LENGTH_SHORT).show()
                Log.e("SignUpActivity", "Error creating account", task.exception)
            }
        }.addOnFailureListener { exception ->
            Log.e("AppCheck", "Failed to get App Check token", exception)
        }
    }

    //save Data into Database
    private fun saveUserData(){
        email = binding.editEmail.text.toString().trim() //trim the blank space
        userName = binding.editNameOwner.text.toString().trim()
        nameOfRestaurent = binding.editNameRes.text.toString().trim()
        password = binding.editPass.text.toString().trim()

//        val user = UserModel(userName, nameOfRestaurent, email, password)
        val userId = auth.currentUser!!.uid
        //save user data to database
//        database.child("admins").child(userId).setValue(user)
        Log.v("SignUpActivity", "User data saved to database")
    }
}