package com.example.adminfoodorderingapp.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminfoodorderingapp.databinding.OrderItemBinding
import com.example.adminfoodorderingapp.model.CartItems

class OrderDetailAdapter(private val cartList: ArrayList<CartItems>, private val context: Context): RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder>() {
    inner class OrderDetailViewHolder(binding: OrderItemBinding): RecyclerView.ViewHolder(binding.root){
        val foodName = binding.cartfoodname
        val foodImage = binding.CartImage
        val quantity = binding.quantity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        val binding = OrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        holder.foodName.text = cartList[position].foodName
        Glide.with(context).load(Uri.parse(cartList[position].foodImage)).into(holder.foodImage)
        holder.quantity.text = cartList[position].foodQuantity.toString()
    }

    override fun getItemCount(): Int = cartList.size
}