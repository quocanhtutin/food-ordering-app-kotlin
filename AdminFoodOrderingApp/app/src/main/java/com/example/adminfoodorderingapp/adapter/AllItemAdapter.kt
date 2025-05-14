package com.example.adminfoodorderingapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminfoodorderingapp.databinding.ItemmenuBinding
import com.example.adminfoodorderingapp.model.AllMenuItem


class AllItemAdapter(val list: MutableList<AllMenuItem>, private val context: Context, private val onDeleteClickListener: (position: Int) -> Unit): RecyclerView.Adapter<AllItemAdapter.AllItemViewHolder>() {

    inner class AllItemViewHolder(private val binding: ItemmenuBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            binding.apply {
                foodName.text = list[position].foodName
                foodPrice.text = list[position].foodPrice
                //foodImage.setImageURI(Uri.parse(list[position].foodImage))
                Glide.with(context).load(Uri.parse(list[position].foodImage)).into(foodImage)
                btnDelete.setOnClickListener {
                    onDeleteClickListener(position)
                }
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllItemViewHolder {
        val binding = ItemmenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AllItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllItemViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = list.size

}