package com.example.foodorderingapp.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.foodorderingapp.LoginActivity
import com.example.foodorderingapp.R
import com.example.foodorderingapp.databinding.FragmentProfileBinding
import com.example.foodorderingapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@SuppressLint("StaticFieldLeak")
private lateinit var binding: FragmentProfileBinding
class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userId: String
    private lateinit var userRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid?:""
        userRef = database.getReference("Users").child(userId)
        setUserData()

        binding.saveBtn.setOnClickListener {
            val name = binding.name.text.toString().trim()
            val address = binding.address.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val phone = binding.phone.text.toString().trim()

            if(name.isEmpty()||address.isEmpty()||email.isEmpty()||phone.isEmpty()){
                binding.name.error = "Name is required"
                binding.address.error = "Address is required"
                binding.email.error = "Email is required"
                binding.phone.error = "Phone is required"
            }else{
                val user = mapOf(
                    "name" to name,
                    "address" to address,
                    "email" to email,
                    "phone" to phone
                )
                userRef.updateChildren(user).addOnSuccessListener {
                    Toast.makeText(requireContext(), "Update Successfully", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Update Failed", Toast.LENGTH_SHORT).show()
                }
                // set value will delete the cart and password!!!!
            }

        }

        binding.LogoutBtn.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder
                .setTitle("Đăng xuất")
                .setPositiveButton("Xác nhận") { _, _ ->
                    auth = FirebaseAuth.getInstance()
                    auth.signOut()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .setNegativeButton("Hủy") { dialog, _ ->
                    dialog.dismiss()
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        return binding.root
    }

    private fun setUserData() {
            userRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val userPfo = snapshot.getValue(UserModel::class.java)
                        if(userPfo != null){
                            binding.name.setText(userPfo.name)
                            binding.address.setText(userPfo.address)
                            binding.email.setText(userPfo.email)
                            binding.phone.setText(userPfo.phone)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

}