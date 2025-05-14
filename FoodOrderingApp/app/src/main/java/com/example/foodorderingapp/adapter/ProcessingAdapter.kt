package com.example.foodorderingapp.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodorderingapp.ProcessingOrderActivity
import com.example.foodorderingapp.databinding.ProcessingOrderItemBinding
import com.example.foodorderingapp.model.OrderModel
import com.google.firebase.database.FirebaseDatabase

class ProcessingAdapter(val orderList: MutableList<OrderModel>, private val requireContext: Context): RecyclerView.Adapter<ProcessingAdapter.ProcessingViewHolder>() {

    private val database = FirebaseDatabase.getInstance("https://admin-food-ordering-app-b3f0e-default-rtdb.asia-southeast1.firebasedatabase.app/")

    inner class ProcessingViewHolder(private val binding: ProcessingOrderItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.nameOfFirstFood.text = orderList[position].foodItems?.get(0)?.foodName ?: ""
            Glide.with(requireContext).load(Uri.parse(orderList[position].foodItems?.get(0)?.foodImage)).into(binding.imageView7)
            if(orderList[position].status == "Cancelled"){
                binding.status.text = orderList[position].status
                binding.receivedBtn.text = "Confirm"
                binding.receivedBtn.alpha = 0.5F
                binding.receivedBtn.setOnClickListener {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext)
                    builder
                        .setMessage("Đơn hàng của bạn đã được hủy bởi người bán")
                        .setTitle("Xác nhận hủy đơn hàng")
                        .setPositiveButton("Xác nhận") { _, _ ->
                            val orderRef = database.reference.child("OrderDetails").child(orderList[position].itemPushKey!!)
                            orderRef.removeValue()
                            orderList.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, orderList.size)
                        }
                    val dialog: AlertDialog = builder.create()
                    dialog.setCancelable(false)
                    dialog.show()
                }
            }
            else if(!orderList[position].orderAccepted){
                binding.receivedBtn.isVisible = false
                binding.oderStatus.setCardBackgroundColor(Color.YELLOW)
                binding.status.text = "Wait for acceptance"
            }
            else if(orderList[position].status == "Processing"){
                binding.status.text = orderList[position].status
                binding.receivedBtn.alpha = 0.5F
                binding.receivedBtn.isClickable = false
            }
            else {

                binding.receivedBtn.setOnClickListener {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext)
                    builder
                        .setMessage("Bạn đã nhận được món ăn và hoàn thành thanh toán")
                        .setTitle("Giao hàng thành công")
                        .setPositiveButton("Xác nhận") { _, _ ->
                            updateStatus(position)
                        }
                        .setNegativeButton("Hủy") { dialog, _ ->
                            dialog.dismiss()
                        }

                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
            }

            binding.root.setOnClickListener {
                val intent = Intent(requireContext, ProcessingOrderActivity::class.java)
                intent.putExtra("orderKey", orderList[position].itemPushKey)
                requireContext.startActivity(intent)
            }
        }
    }


    private fun updateStatus(position: Int) {
        val orderRef = database.reference.child("OrderDetails").child(orderList[position].itemPushKey!!)
        orderRef.child("status").setValue("Received")

        orderList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, orderList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProcessingViewHolder {
        val binding = ProcessingOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProcessingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProcessingViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = orderList.size
}