package com.example.foodorderingapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.Fragment.CartFragment
import com.example.foodorderingapp.Fragment.CongratsBottomFragment
import com.example.foodorderingapp.adapter.PayoutAdapter
import com.example.foodorderingapp.databinding.ActivityPayOutBinding
import com.example.foodorderingapp.model.CartItems
import com.example.foodorderingapp.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("StaticFieldLeak")
private lateinit var binding: ActivityPayOutBinding
class PayOutActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var name: String
    private lateinit var address: String
    private lateinit var phone: String
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userId: String
    private lateinit var foodList: MutableList<CartItems>
    private var totalAmount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //initialize database
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid?:""
        databaseReference = database.getReference("Users").child(userId)

        setUserData()
        retrieveCartItem()

        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.placeOrderBtn.setOnClickListener {

            name= binding.Name.text.toString().trim()
            address = binding.Address.text.toString().trim()
            phone = binding.Phone.text.toString().trim()
            if(name.isEmpty() || address.isEmpty() || phone.isEmpty()){
                binding.Name.error = "Please enter your name"
                binding.Address.error = "Please enter your address"
                binding.Phone.error = "Please enter your phone number"
            }
            else{
                placeOrder()
            }
        }
    }

    private fun placeOrder() {
//        val time = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val current = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
//        val time = System.currentTimeMillis().toString()
        val itemPushKey = database.reference.child("OrderDetails").push().key
        val orderDetails = OrderModel(userId, name, foodList, address, totalAmount, phone, false, false, itemPushKey, current, "Processing")
        val orderReference = database.reference.child("OrderDetails").child(itemPushKey!!)
        orderReference.setValue(orderDetails).addOnSuccessListener {
            val bottomSheetDialog = CongratsBottomFragment()
            bottomSheetDialog.isCancelable = false
            bottomSheetDialog.show(supportFragmentManager, "Test")
            removeItemFromCart()

        }
            .addOnFailureListener {
                Toast.makeText(this, "Order Failed", Toast.LENGTH_SHORT).show()
                Log.e("Order Failed", it.message.toString())
            }
    }

    private fun addOrderToHistory(orderId: String, time: String) {
        database.reference.child("Users").child(userId).child("history").child(time).setValue(orderId)
    }


    private fun removeItemFromCart() {
        val cartReference = database.reference.child("Users").child(userId).child("cart")
        cartReference.removeValue()
    }

    private fun calculateTotalAmount() {
        for (eachItem in foodList){
            totalAmount += eachItem.foodPrice!!.toInt()*eachItem.foodQuantity!!
        }
        binding.TotalAmount.setText(totalAmount.toString()+" vnd")
    }

    private fun setUserData() {
        databaseReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    name = snapshot.child("name").getValue(String::class.java)?:""
                    address = snapshot.child("address").getValue(String::class.java)?:""
                    phone = snapshot.child("phone").getValue(String::class.java)?:""
                    binding.apply {
                        Name.setText(name)
                        Address.setText(address)
                        Phone.setText(phone)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun retrieveCartItem() {
        //database reference to the firebase
        val foodReference = database.getReference("Users").child(userId).child("cart")

        foodList = mutableListOf()

        //fetch data from firebase
        foodReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children){
                    val cartItem = foodSnapshot.getValue(CartItems::class.java)
                    cartItem?.let {
                        foodList.add(it)
                    }
                }
                calculateTotalAmount()
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PayOutActivity, "Data not fetch", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun setAdapter() {
        val adapter = PayoutAdapter(foodList as ArrayList<CartItems>, this)
        binding.payOutRecyclerView.adapter = adapter
        binding.payOutRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}