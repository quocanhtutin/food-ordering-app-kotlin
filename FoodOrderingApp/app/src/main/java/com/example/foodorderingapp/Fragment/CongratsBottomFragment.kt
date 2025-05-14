package com.example.foodorderingapp.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foodorderingapp.databinding.FragmentCongratsBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

@SuppressLint("StaticFieldLeak")
private lateinit var binding: FragmentCongratsBottomBinding
class CongratsBottomFragment : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCongratsBottomBinding.inflate(layoutInflater, container, false)
        binding.gohomebtn.setOnClickListener {
            val intent = Intent(requireContext(), HomeFragment::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            dismiss()
        }
        return binding.root
    }

    companion object {

    }
}