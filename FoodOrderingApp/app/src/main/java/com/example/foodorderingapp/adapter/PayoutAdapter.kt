package com.example.foodorderingapp.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodorderingapp.databinding.PayoutItemCheckBinding
import com.example.foodorderingapp.model.CartItems

class PayoutAdapter(private val cartList: ArrayList<CartItems>, private val context: Context): RecyclerView.Adapter<PayoutAdapter.PayoutViewHolder>() {
    inner class PayoutViewHolder(binding: PayoutItemCheckBinding): RecyclerView.ViewHolder(binding.root){
        val foodName = binding.cartfoodname
        val foodPrice = binding.cartitemprice
        val foodImage = binding.CartImage
        val quantity = binding.quantity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayoutViewHolder {
        val binding = PayoutItemCheckBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PayoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PayoutViewHolder, position: Int) {
        holder.foodName.text = cartList[position].foodName
        holder.foodPrice.text = cartList[position].foodPrice
        Glide.with(context).load(Uri.parse(cartList[position].foodImage)).into(holder.foodImage)
        holder.quantity.text = cartList[position].foodQuantity.toString()
    }

    override fun getItemCount(): Int = cartList.size
}