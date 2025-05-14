package com.example.foodorderingapp.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.PayOutActivity
import com.example.foodorderingapp.adapter.CartAdapter
import com.example.foodorderingapp.databinding.FragmentCartBinding
import com.example.foodorderingapp.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@SuppressLint("StaticFieldLeak")
private lateinit var binding: FragmentCartBinding
class CartFragment : Fragment() {
    
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var foodList: MutableList<CartItems>
    private lateinit var quantity: MutableList<Int>
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater, container, false)

       auth = FirebaseAuth.getInstance()
       retrieveCartItem()

        binding.proceedbtn.setOnClickListener {
            //get order item
            getOrderItemsDetail()
            val intent = Intent(requireContext(), PayOutActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun getOrderItemsDetail() {
        val orderIdReference = database.getReference("Users").child(userId).child("cart")


    }

    private fun retrieveCartItem() {
        //database reference to the firebase
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid?:""
        val foodReference = database.getReference("Users").child(userId).child("cart")

        foodList = mutableListOf()
        quantity = mutableListOf()

        //fetch data from firebase
        foodReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children){
                    val cartItem = foodSnapshot.getValue(CartItems::class.java)
                    cartItem?.let {
                        foodList.add(it)
                    }
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Data not fetch", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun setAdapter() {
        val adapter = CartAdapter(foodList, requireContext())
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.cartRecyclerView.adapter = adapter
    }

}