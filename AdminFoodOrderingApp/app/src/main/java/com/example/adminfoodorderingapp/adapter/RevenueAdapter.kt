package com.example.adminfoodorderingapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminfoodorderingapp.databinding.RevenueItemBinding
import com.example.adminfoodorderingapp.model.Revenue

class RevenueAdapter(private val revenueList: List<Revenue>): RecyclerView.Adapter<RevenueAdapter.RevenueViewHolder>() {
    inner class RevenueViewHolder(private val binding: RevenueItemBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            binding.apply {
                month.text = revenueList[position].year + " - " + revenueList[position].month
                revenue.text = revenueList[position].revenue.toString()
                numberOrders.text = revenueList[position].numberOrder.toString()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RevenueViewHolder {
        val binding = RevenueItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RevenueViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RevenueViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = revenueList.size
}