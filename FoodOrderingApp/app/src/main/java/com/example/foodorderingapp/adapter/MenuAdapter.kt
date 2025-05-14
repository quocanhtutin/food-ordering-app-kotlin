package com.example.foodorderingapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodorderingapp.DetailActivity
import com.example.foodorderingapp.databinding.MenuItemBinding
import com.example.foodorderingapp.model.CartItems
import com.example.foodorderingapp.model.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MenuAdapter(private val menuList: MutableList<MenuItem>, private val requireContext: Context) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private val auth = FirebaseAuth.getInstance()

    inner class MenuViewHolder(private val binding: MenuItemBinding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener{
                val position = adapterPosition
                if(position!=RecyclerView.NO_POSITION){
                    openDetailActivity(position)
                }
            }
            binding.addToCartPopular.setOnClickListener {
                val position = adapterPosition
                if(position!=RecyclerView.NO_POSITION){
                    addItemToCart(position)
                }
            }
        }

        private fun openDetailActivity(position: Int) {
            val menuItem = menuList[position]
            //intent to open detail activity
            val intent = Intent(requireContext, DetailActivity::class.java)
            intent.putExtra("MenuItemName", menuItem.foodName)
            intent.putExtra("MenuItemImage", menuItem.foodImage)
            intent.putExtra("MenuItemDescription", menuItem.shortDescription)
            intent.putExtra("MenuItemIngredients", menuItem.ingredients)
            intent.putExtra("MenuItemPrice", menuItem.foodPrice)

            //start activity
            requireContext.startActivity(intent)
        }

        private fun addItemToCart(position: Int) {
            val menuItem = menuList[position]
            val database = FirebaseDatabase.getInstance("https://admin-food-ordering-app-b3f0e-default-rtdb.asia-southeast1.firebasedatabase.app/")
            val userId = auth.currentUser?.uid?:""
            //Create a card object
            val cartItem = CartItems(menuItem.foodName, menuItem.foodPrice, menuItem.foodImage, menuItem.shortDescription, menuItem.ingredients, 1)

            database.reference.child("Users").child(userId).child("cart").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(item in snapshot.children){
                        val itemAdded = item.getValue(CartItems::class.java)
                        if(itemAdded?.foodName == menuItem.foodName){
                            Toast.makeText(requireContext, "Item already in cart", Toast.LENGTH_SHORT).show()
                            return
                        }
                    }
                    //save data to cart item to database
                    database.reference.child("Users").child(userId).child("cart").push().setValue(cartItem).addOnSuccessListener {
                        Toast.makeText(requireContext, "Items added into cart successfully", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(requireContext, "Failed to add items into cart", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })



        }

        //set data into recycler view items name, price, image
        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            binding.apply{
                foodNameMenu.text = menuList[position].foodName
                priceMenu.text = menuList[position].foodPrice +" vnd"
                Glide.with(requireContext).load(menuList[position].foodImage).into(menuImage)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }
}

