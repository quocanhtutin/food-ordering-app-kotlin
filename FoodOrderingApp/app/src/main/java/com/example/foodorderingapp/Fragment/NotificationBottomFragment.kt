package com.example.foodorderingapp.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.R
import com.example.foodorderingapp.adapter.NotificationAdapter
import com.example.foodorderingapp.adapter.NotificationItem
import com.example.foodorderingapp.databinding.FragmentNotificationBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

@SuppressLint("StaticFieldLeak")
private lateinit var binding: FragmentNotificationBottomBinding
class NotificationBottomFragment : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBottomBinding.inflate(layoutInflater, container, false)
        val notifications = mutableListOf<NotificationItem>(NotificationItem("Your order has been CANCELED SUCCESSFULLY!", R.drawable.sademoji), NotificationItem("Order is being DELIVERED...", R.drawable.van), NotificationItem("Congrats your oder PLACED!", R.drawable.illustration))
        val adapter = NotificationAdapter(notifications)
        binding.notificationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationRecyclerView.adapter = adapter
        binding.btnBack.setOnClickListener{
            dismiss()
        }
        return binding.root
    }

    companion object {

    }
}