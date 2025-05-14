package com.example.adminfoodorderingapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminfoodorderingapp.adapter.PendingOrderAdapter
import com.example.adminfoodorderingapp.databinding.ActivityPendingOrderBinding
import com.example.adminfoodorderingapp.model.OrderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@SuppressLint("StaticFieldLeak")
private lateinit var binding: ActivityPendingOrderBinding
class PendingOrderActivity : AppCompatActivity() {

    private lateinit var orderList: MutableList<OrderModel>
    private lateinit var database: FirebaseDatabase
    private lateinit var adapter: PendingOrderAdapter
    private lateinit var orderRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //initialize database
        database = FirebaseDatabase.getInstance("https://admin-food-ordering-app-b3f0e-default-rtdb.asia-southeast1.firebasedatabase.app")
        orderRef = database.reference.child("OrderDetails")

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        orderList = mutableListOf()
        retrieveOrders()

    }

    private fun retrieveOrders() {
        orderRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(orderSnapshot in snapshot.children){
                    val order = orderSnapshot.getValue(OrderModel::class.java)
                    if(order?.status == "Processing") {
                        orderList.add(order)
                    }
                }
                orderList.reverse()
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DatabaseError", "Error retrieving orders: ${error.message}")
            }
        })
    }

    private fun setAdapter() {
        val intent = Intent(this, OrderDetailActivity::class.java)
        adapter = PendingOrderAdapter(orderList, this, intent, launcher)
        binding.pendingOrderRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.pendingOrderRecyclerView.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if(result.resultCode == Activity.RESULT_OK){
            orderList.clear()
            retrieveOrders()
        }
    }
}