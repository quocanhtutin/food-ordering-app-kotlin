package com.example.foodorderingapp.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodorderingapp.DetailActivity
import com.example.foodorderingapp.databinding.BuyagainitemBinding
import com.example.foodorderingapp.model.CartItems
import com.example.foodorderingapp.model.MenuItem

class BuyAgainAdapter(private val buyAgainList : MutableList<CartItems>, private val requireContext: Context): RecyclerView.Adapter<BuyAgainAdapter.BuyAgainHolder>() {
    inner class BuyAgainHolder(val binding: BuyagainitemBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener{
                val position = adapterPosition
                if(position!=RecyclerView.NO_POSITION){
                    openDetailActivity(position)
                }
            }
            binding.btnBuyAgain.setOnClickListener{
                val position = adapterPosition
                if(position!=RecyclerView.NO_POSITION){
                    openDetailActivity(position)
                }
            }
        }

        private fun openDetailActivity(position: Int) {
            val menuItem = buyAgainList[position]
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

        fun bind(position: Int) {
            binding.apply {
                nameBA.text = buyAgainList[position].foodName
                priceBA.text = buyAgainList[position].foodPrice
                Glide.with(requireContext).load(Uri.parse(buyAgainList[position].foodImage)).into(imageBA)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyAgainHolder {
        val binding = BuyagainitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BuyAgainHolder(binding)
    }

    override fun onBindViewHolder(holder: BuyAgainHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return buyAgainList.size
    }
}