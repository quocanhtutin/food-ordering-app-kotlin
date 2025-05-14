package com.example.adminfoodorderingapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminfoodorderingapp.databinding.ActivityCreateUseBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

@SuppressLint("StaticFieldLeak")
private lateinit var binding: ActivityCreateUseBinding
class CreateUseActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var userName: String
    private lateinit var database: DatabaseReference
    private lateinit var dtb : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateUseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        auth = Firebase.auth
        //initialization Firebase Database
        dtb = FirebaseDatabase.getInstance("https://admin-food-ordering-app-b3f0e-default-rtdb.asia-southeast1.firebasedatabase.app")
        database = dtb.reference


        binding.createUserBtn.setOnClickListener {
            //get text from edit text
            email = binding.editEmail.text.toString().trim() //trim the blank space
            userName = binding.editName.text.toString().trim()
            password = binding.editPass.text.toString().trim()

            if (userName.isEmpty() || email.isEmpty() || password.isEmpty()) {
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
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            else {
                Toast.makeText(this, "Error creating account", Toast.LENGTH_SHORT).show()
                Log.e("Create Activity", "Error creating account", task.exception)
            }
        }.addOnFailureListener { exception ->
            Log.e("AppCheck", "Failed to get App Check token", exception)
        }
    }
}