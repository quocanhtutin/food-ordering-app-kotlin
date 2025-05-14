package com.example.adminfoodorderingapp.adapter

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.example.adminfoodorderingapp.databinding.DeliveryItemBinding
import com.example.adminfoodorderingapp.model.OrderModel

class DeliveryAdapter(private val orderList:MutableList<OrderModel>, private val intent: Intent, private val launcher: ActivityResultLauncher<Intent>): RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>() {
    inner class DeliveryViewHolder(private val binding: DeliveryItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.apply {
                name.text = orderList[position].userName
                if(orderList[position].paymentReceived){
                    deliveryStatus.text = "Completed"
                    paymentStatus.text = "Received"
                    paymentStatus.setTextColor(Color.BLUE)
                    deliveryStatusColor.backgroundTintList = ColorStateList.valueOf(Color.BLUE)
                }
                else{
                    deliveryStatus.text = "Out for delivery"
                    paymentStatus.text = "Not Received"
                    paymentStatus.setTextColor(Color.RED)
                    deliveryStatusColor.backgroundTintList = ColorStateList.valueOf(Color.RED)
                }


                root.setOnClickListener {
                    intent.putExtra("orderKey", orderList[position].itemPushKey)
                    launcher.launch(intent)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val binding = DeliveryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeliveryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = orderList.size
}