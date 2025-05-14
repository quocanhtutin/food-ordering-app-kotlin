package com.example.adminfoodorderingapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminfoodorderingapp.databinding.ActivityMainBinding
import com.example.adminfoodorderingapp.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@SuppressLint("StaticFieldLeak")
private lateinit var binding: ActivityMainBinding
class MainActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var orderList: MutableList<OrderModel>
    private lateinit var completedOrdersRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = FirebaseDatabase.getInstance()

        binding.cardAddMenu.setOnClickListener{
            val intent = Intent(this, AddItemActivity::class.java)
            launcher.launch(intent)
        }

        binding.cardAllMenu.setOnClickListener {
            val intent =Intent(this, AllItemActivity::class.java)
            launcher.launch(intent)
        }

        binding.cardOrderDisPatch.setOnClickListener {
            val intent =Intent(this, OutFoodDeliveryActivity::class.java)
            launcher.launch(intent)
        }

        binding.cardProfile.setOnClickListener {
            val intent =Intent(this, AdminProfile::class.java)
            launcher.launch(intent)
        }

        binding.cardCreateUser.setOnClickListener {
            val intent =Intent(this, CreateUseActivity::class.java)
            startActivity(intent)
        }

        binding.pendingOrderTxt.setOnClickListener {
            val intent = Intent(this, PendingOrderActivity::class.java)
            launcher.launch(intent)
        }

        binding.cardLogout.setOnClickListener {

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder
                .setTitle("Đăng xuất")
                .setPositiveButton("Xác nhận") { _, _ ->
                    auth = FirebaseAuth.getInstance()
                    auth.signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Hủy") { dialog, _ ->
                    dialog.dismiss()
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        orderList = mutableListOf()
        retrieveOrders()
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if(result.resultCode == Activity.RESULT_OK){
            orderList.clear()
            retrieveOrders()
        }
    }

    private fun retrieveOrders() {
        val orderRef = database.getReference("OrderDetails")
        orderRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(OrderModel::class.java)
                    orderList.add(order!!)
                }
                setPendingOrderCount()
                setCompletedOrderCount()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setCompletedOrderCount() {
        val completedOrders = orderList.filter { it.paymentReceived }
        binding.completeOrdersCount.text = completedOrders.size.toString()
        var totalEarning = 0
        for(order in completedOrders){
            totalEarning += order.totalPrice!!.toInt()
        }
        binding.totalEarning.text = totalEarning.toString()
    }

    @SuppressLint("SetTextI18n")
    private fun setPendingOrderCount() {
        val pendingOrders = orderList.filter { it.status == "Processing" }
        binding.pendingCount.text = pendingOrders.size.toString()
    }

}