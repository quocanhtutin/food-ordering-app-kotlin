package com.example.adminfoodorderingapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminfoodorderingapp.adapter.OrderDetailAdapter
import com.example.adminfoodorderingapp.databinding.ActivityOrderDetailBinding
import com.example.adminfoodorderingapp.model.CartItems
import com.example.adminfoodorderingapp.model.OrderModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.Clock
import java.util.Date

@SuppressLint("StaticFieldLeak")
private lateinit var binding: ActivityOrderDetailBinding
@Suppress("DEPRECATION")
class OrderDetailActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var orderKey: String
    private lateinit var orderRef: DatabaseReference

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //initialize firebase
        database = FirebaseDatabase.getInstance()

        //get order key from intent
        orderKey = intent.getStringExtra("orderKey").toString()
        orderRef = database.reference.child("OrderDetails").child(orderKey)

        setOrderDetail()

        binding.cancelBtn.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder
                .setTitle("Hủy đơn hàng")
                .setPositiveButton("Xác nhận") { _, _ ->
                    orderRef.child("status").setValue("Cancelled")
                    val intent = Intent(this, PendingOrderActivity::class.java)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                .setNegativeButton("Hủy") { dialog, _ ->
                    dialog.dismiss()
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, PendingOrderActivity::class.java)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun setOrderDetail() {
        val orderRef = database.reference.child("OrderDetails").child(orderKey)
        orderRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val order = snapshot.getValue(OrderModel::class.java)
                val userId = order?.userId
                val name = order?.userName
                val address = order?.address
                val phone = order?.phone
                val totalPrice = order?.totalPrice
                val time = order?.time
                val foodItems = order?.foodItems as ArrayList<CartItems>

                binding.Name.setText(name)
                binding.Address.setText(address)
                binding.Phone.setText(phone)
                binding.TotalAmount.setText(totalPrice.toString()+ " vnd")
                binding.time.text = time
                binding.orderDetalRecyclerView.adapter = OrderDetailAdapter(foodItems, this)
                binding.orderDetalRecyclerView.layoutManager = LinearLayoutManager(this)

                if(order.orderAccepted && order.status == "Processing"){
                    binding.acceptBtn.text = "Dispatch"
                    binding.acceptBtn.setOnClickListener {
                        orderRef.child("status").setValue("Out for delivery")
                        val intent = Intent(this, PendingOrderActivity::class.java)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
                else if(order.orderAccepted && (order.status == "Out for delivery" || order.status == "Received") && !order.paymentReceived){
                    binding.acceptBtn.text = "Completed"
                    binding.acceptBtn.setOnClickListener {
                        val today = Date()
                        val month = today.month + 1
                        val year = today.year
                        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                        builder
                            .setMessage("Bạn đã hoàn tất giao hàng và thu tiền thành công")
                            .setTitle("Giao hàng thành công")
                            .setPositiveButton("Xác nhận") { _, _ ->
                                orderRef.child("paymentReceived").setValue(true)
                                database.reference.child("revenue").child(year.toString()).child(month.toString()).child(time!!).setValue(totalPrice)
                                val intent = Intent(this, OutFoodDeliveryActivity::class.java)
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }
                            .setNegativeButton("Hủy") { dialog, _ ->
                                dialog.dismiss()
                            }

                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    }
                }
                else if(order.orderAccepted && order.paymentReceived){
                    binding.acceptBtn.text = "Completed"
                    binding.acceptBtn.isClickable = false
                    binding.cancelBtn.isClickable = false
                }
                else{
                    binding.acceptBtn.text = "Accept"
                    binding.acceptBtn.setOnClickListener {
                        orderRef.child("orderAccepted").setValue(true)
                        database.reference.child("Users").child(userId!!).child("history").child(time!!).setValue(order.itemPushKey)
                        setOrderDetail()
                    }
                }
            }
        }
    }
}

