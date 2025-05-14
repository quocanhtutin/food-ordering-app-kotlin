package com.example.adminfoodorderingapp.adapter

import android.adservices.ondevicepersonalization.RequestLogRecord
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.provider.CalendarContract.Colors
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.example.adminfoodorderingapp.OrderDetailActivity
import com.example.adminfoodorderingapp.R
import com.example.adminfoodorderingapp.databinding.PendingOrderItemBinding
import com.example.adminfoodorderingapp.model.OrderModel

class PendingOrderAdapter(private val list: MutableList<OrderModel>, private val context: Context, private val intent: Intent, private val launcher: ActivityResultLauncher<Intent>) :
    RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder>() {
    inner class PendingOrderViewHolder(private val binding: PendingOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply{
                time.text = list[position].time
                customerName.text = list[position].userName

                if(list[position].orderAccepted){
                    binding.layoutOrder.setBackgroundColor(Color.parseColor("#8EBDD1"))
                }

                root.setOnClickListener {
                    intent.putExtra("orderKey", list[position].itemPushKey)
                    launcher.launch(intent)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingOrderViewHolder {
        val binding =
            PendingOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PendingOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PendingOrderViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = list.size
}