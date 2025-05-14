package com.example.foodorderingapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.DetailActivity
import com.example.foodorderingapp.databinding.PopularItemBinding

data class PopularItem(
    val name: String,
    val price: String,
    val image: Int
)

class PopularAdapter(private val list: List<PopularItem>, private val requireContext: Context): RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {
    inner class PopularViewHolder(private val binding: PopularItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(name: String, price: String, image: Int) {
            binding.foodNamePopular.text = name
            binding.pricePopular.text = price
            binding.imageView6.setImageResource(image)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        return PopularViewHolder(PopularItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val name = list[position].name
        val price = list[position].price
        val image = list[position].image
        holder.bind(name, price, image)
        holder.itemView.setOnClickListener {
            val intent = Intent(requireContext, DetailActivity::class.java)
            intent.putExtra("MenuItemName", name)
            intent.putExtra("MenuItemImage", image)
            requireContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}