package com.example.foodorderingapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodorderingapp.databinding.CartItemBinding
import com.example.foodorderingapp.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CartAdapter(
    private val cartList: MutableList<CartItems>,
    private val context: Context
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    //initialize firebase auth
    private val auth = FirebaseAuth.getInstance()

    init{
        val database = FirebaseDatabase.getInstance("https://admin-food-ordering-app-b3f0e-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val userId = auth.currentUser?.uid?:""

        cartItemsReference = database.reference.child("Users").child(userId).child("cart")
    }

    companion object{
        private lateinit var cartItemsReference: DatabaseReference
    }


    inner class CartViewHolder(private val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            binding.apply {
                cartfoodname.text = cartList[position].foodName
                cartitemprice.text = cartList[position].foodPrice + " vnd"
                //load image using Glide
                Glide.with(context).load(Uri.parse(cartList[position].foodImage)).into(CartImage)
                quantity.text = cartList[position].foodQuantity.toString()

                minusbtn.setOnClickListener{
                    decreaseQuantity(position)
                }
                plusbtn.setOnClickListener {
                    increaseQuantity(position)
                }
                deletebtn.setOnClickListener {
                    val itemPosition = adapterPosition
                    if(itemPosition != RecyclerView.NO_POSITION){
                        deleteItem(itemPosition)
                    }
                }
            }
        }
        private fun decreaseQuantity(position: Int){
            if(cartList[position].foodQuantity!!>1){
                cartList[position].foodQuantity = cartList[position].foodQuantity!! - 1
                binding.quantity.text = cartList[position].foodQuantity.toString()
                getUniqueKeyAtPosition(position) { uniqueKey ->
                    if (uniqueKey != null) {
                        cartItemsReference.child(uniqueKey).child("foodQuantity").setValue(cartList[position].foodQuantity)
                    }
                }
            }
        }

        private fun increaseQuantity(position: Int){
            if(cartList[position].foodQuantity!!<10){
                cartList[position].foodQuantity = cartList[position].foodQuantity!! + 1
                binding.quantity.text = cartList[position].foodQuantity.toString()
                getUniqueKeyAtPosition(position) { uniqueKey ->
                    if (uniqueKey != null) {
                        cartItemsReference.child(uniqueKey).child("foodQuantity").setValue(cartList[position].foodQuantity)
                    }
                }
            }
        }

        private fun deleteItem(position: Int){
            getUniqueKeyAtPosition(position) { uniqueKey ->
                if (uniqueKey != null) {
                    removeItem(position, uniqueKey)
                }
            }
        }

    }

    private fun removeItem(position: Int, uniqueKey: String) {
        cartItemsReference.child(uniqueKey).removeValue().addOnSuccessListener {
            cartList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, cartList.size)
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to Delete", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getUniqueKeyAtPosition(positionRetrieve: Int, onComplete: (String?) -> Unit) {
        cartItemsReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var uniqueKey:String? = null
                snapshot.children.forEachIndexed { index, dataSnapshot ->
                    if(index == positionRetrieve){
                        uniqueKey = dataSnapshot.key
                        return@forEachIndexed
                    }
                }
                onComplete(uniqueKey)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return cartList.size
    }
}