package com.example.foodorderingapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.Fragment.HistoryFragment
import com.example.foodorderingapp.adapter.PayoutAdapter
import com.example.foodorderingapp.databinding.ActivityProcessingOrderBinding
import com.example.foodorderingapp.model.CartItems
import com.example.foodorderingapp.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

@SuppressLint("StaticFieldLeak")
private lateinit var binding: ActivityProcessingOrderBinding
class ProcessingOrderActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var orderKey: String
    private lateinit var orderRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProcessingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://admin-food-ordering-app-b3f0e-default-rtdb.asia-southeast1.firebasedatabase.app/")
        orderKey = intent.getStringExtra("orderKey") ?: ""
        orderRef = database.reference.child("OrderDetails").child(orderKey)


        setOrderDetail()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setOrderDetail() {
        orderRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val order = snapshot.getValue(OrderModel::class.java)
                val name = order?.userName
                val address = order?.address
                val phone = order?.phone
                val totalPrice = order?.totalPrice
                val time = order?.time
                val foodItems = order?.foodItems as ArrayList<CartItems>
                val status = order.status
                if (status == "Cancelled") {
                    binding.receiveOrderBtn.text = "Confirm"
                    binding.receiveOrderBtn.setOnClickListener {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                        builder
                            .setMessage("Đơn hàng của bạn đã được hủy bởi người bán")
                            .setTitle("Xác nhận hủy đơn hàng")
                            .setPositiveButton("Xác nhận") { _, _ ->
                                orderRef.removeValue()
                                val intent = Intent(this, HistoryFragment::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            }
                        val dialog: AlertDialog = builder.create()
                        dialog.setCancelable(false)
                        dialog.show()
                    }
                }
                else if (!order.orderAccepted) {
                    binding.receiveOrderBtn.text = "Wait for Acceptance"
                    binding.receiveOrderBtn.isClickable = false
                }
                else if(status == "Processing"){
                    binding.receiveOrderBtn.isClickable = false
                }
                else {
                    binding.receiveOrderBtn.setOnClickListener {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                        builder
                            .setMessage("Bạn đã nhận được món ăn và hoàn thành thanh toán")
                            .setTitle("Giao hàng thành công")
                            .setPositiveButton("Xác nhận") { _, _ ->
                                updateStatus()
                            }
                            .setNegativeButton("Hủy") { dialog, _ ->
                                dialog.dismiss()
                            }

                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    }
                }


                binding.Name.setText(name)
                binding.Address.setText(address)
                binding.Phone.setText(phone)
                binding.TotalAmount.setText(totalPrice.toString() + " vnd")
                binding.estimatedTime.text = "$time + 30 minutes"
                binding.orderDetalRecyclerView.adapter = PayoutAdapter(foodItems, this)
                binding.orderDetalRecyclerView.layoutManager = LinearLayoutManager(this)

            }
        }
    }

    private fun updateStatus() {
        orderRef.child("status").setValue("Received")
        val intent = Intent(this, HistoryFragment::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}