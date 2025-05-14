package com.example.foodorderingapp.Fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.R
import com.example.foodorderingapp.adapter.BuyAgainAdapter
import com.example.foodorderingapp.adapter.ProcessingAdapter
import com.example.foodorderingapp.databinding.FragmentHistoryBinding
import com.example.foodorderingapp.model.CartItems
import com.example.foodorderingapp.model.MenuItem
import com.example.foodorderingapp.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@SuppressLint("StaticFieldLeak")
private lateinit var binding: FragmentHistoryBinding
class HistoryFragment : Fragment() {
    
    private lateinit var buyAgainAdapter: BuyAgainAdapter
    private lateinit var processingAdapter: ProcessingAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private var listOfOrderItem: MutableList<OrderModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        //retrieve and display the user order history
        retrieveOrderHistory()

        return binding.root
    }

    private fun retrieveOrderHistory() {
        userId = auth.currentUser!!.uid
        val buyItemReference = database.reference.child("Users").child(userId).child("history")

        val orderReference = database.reference.child("OrderDetails")
        orderReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children){
                    val order = orderSnapshot.getValue(OrderModel::class.java)
                    if(order?.userId == userId) {
                        order.let {
                            listOfOrderItem.add(it)
                        }
                    }
                }
                listOfOrderItem.reverse()
                if(listOfOrderItem.isNotEmpty()){
                    setDataProcessing()
                    setDataBuyAgain()
                }
                else{
                    Toast.makeText(requireContext(),"No order history found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

//        buyItemReference.addListenerForSingleValueEvent(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val orderReference = database.reference.child("OrderDetails")
//                val orderCount = snapshot.childrenCount
//                var ordersProcessed = 0
//                for(buySnapshot in snapshot.children){
//                    val orderId = buySnapshot.getValue() as? String // Safe cast to String
//                    orderId?.let { id ->
//                        orderReference.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
//                            override fun onDataChange(orderSnapshot: DataSnapshot) {
//                                val order = orderSnapshot.getValue(OrderModel::class.java)
//                                order?.let {
//                                    listOfOrderItem.add(it)
//                                }
//                                ordersProcessed++
//                                if(ordersProcessed == orderCount.toInt()){
//                                    listOfOrderItem.reverse()
//                                    if(listOfOrderItem.isNotEmpty()){
//                                        setDataProcessing()
//                                        setDataBuyAgain()
//                                    }
//                                    else{
//                                        Toast.makeText(requireContext(),"No order history found", Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                            }
//
//                            override fun onCancelled(error: DatabaseError) {
//                                // Handle error getting order details
//                                Log.e("HistoryFragment", "Error getting order details: ${error.message}")
//                            }
//                        })
//                        Log.d("orderId", "Order ID: $id")
//                    }
//                    Log.d("HistoryFragment", "Order ID: $orderId")
//                }
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
    }

    private fun setDataBuyAgain() {
        val buyAgainOrderItem = listOfOrderItem.filter { item -> item.status == "Received" }
        val buyAgainFood = mutableListOf<CartItems>()
        for(order in buyAgainOrderItem){
            for(food in order.foodItems!!){
                if(buyAgainFood.contains(CartItems(food.foodName, food.foodPrice, food.foodImage, food.shortDescription, food.ingredients, 1))){
                    continue
                }
                else{
                    buyAgainFood.add(CartItems(food.foodName, food.foodPrice, food.foodImage, food.shortDescription, food.ingredients, 1))
                }
            }
        }
        buyAgainAdapter = BuyAgainAdapter(buyAgainFood, requireContext())
        binding.buyAgainRecV.layoutManager = LinearLayoutManager(requireContext())
        binding.buyAgainRecV.adapter = buyAgainAdapter
    }

    private fun setDataProcessing() {
        val recentOrderItem = listOfOrderItem.filter { item -> item.status == "Processing" || item.status == "Wait for Acceptance"||item.status == "Out for delivery" || item.status == "Cancelled"}
        processingAdapter = ProcessingAdapter(recentOrderItem.toMutableList(), requireContext())
        binding.processingRec.layoutManager = LinearLayoutManager(requireContext())
        binding.processingRec.adapter = processingAdapter
    }




    companion object {
        
    }
}