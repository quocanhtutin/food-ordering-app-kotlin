package com.example.adminfoodorderingapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.adminfoodorderingapp.databinding.ActivityAdminProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@SuppressLint("StaticFieldLeak")
private lateinit var binding: ActivityAdminProfileBinding
class AdminProfile : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var adminReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        database = FirebaseDatabase.getInstance()
        adminReference = database.reference.child("admins").child(userId!!)

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        var isEnabled = false

        binding.adminName.isEnabled = isEnabled
        binding.adminAddress.isEnabled = isEnabled
        binding.adminEmail.isEnabled = isEnabled
        binding.adminPhone.isEnabled = isEnabled
        binding.saveBtn.isVisible = isEnabled

        binding.editBtn.setOnClickListener {
            isEnabled = !isEnabled
            if(isEnabled) {
                binding.editBtn.setTextColor(Color.parseColor("#4BADD7"))
                binding.adminName.requestFocus()
            }
            else binding.editBtn.setTextColor(Color.parseColor("#6F4BADD7"))
            binding.adminName.isEnabled = isEnabled
            binding.adminAddress.isEnabled = isEnabled
            binding.adminEmail.isEnabled = isEnabled
            binding.adminPhone.isEnabled = isEnabled
            binding.saveBtn.isVisible = isEnabled
        }

        binding.saveBtn.setOnClickListener {
            val adminName = binding.adminName.text.toString().trim()
            val adminAddress = binding.adminAddress.text.toString().trim()
            val adminEmail = binding.adminEmail.text.toString().trim()
            val adminPhone = binding.adminPhone.text.toString().trim()

            if(adminName.isEmpty() || adminAddress.isEmpty() || adminEmail.isEmpty() || adminPhone.isEmpty()) {
                binding.adminName.error = "Please enter your name"
                binding.adminAddress.error = "Please enter your address"
                binding.adminEmail.error = "Please enter your email"
                binding.adminPhone.error = "Please enter your phone number"
            }
            else{
                val adminData = mapOf(
                    "name" to adminName,
                    "address" to adminAddress,
                    "email" to adminEmail,
                    "phone" to adminPhone
                )
                adminReference.updateChildren(adminData).addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }
            }
        }

        retrieveAdminData()
    }

    private fun retrieveAdminData() {
        adminReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val adminName = snapshot.child("name").value.toString()
                    val adminAddress = snapshot.child("address").value.toString()
                    val adminEmail = snapshot.child("email").value.toString()
                    val adminPhone = snapshot.child("phone").value.toString()

                    setAdminData(adminName, adminAddress, adminEmail, adminPhone)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setAdminData(
        adminName: String,
        adminAddress: String,
        adminEmail: String,
        adminPhone: String
    ) {
        binding.adminName.setText(adminName)
        binding.adminAddress.setText(adminAddress)
        binding.adminEmail.setText(adminEmail)
        binding.adminPhone.setText(adminPhone)
    }
}