package com.example.foodorderingapp.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.R
import com.example.foodorderingapp.adapter.MenuAdapter
import com.example.foodorderingapp.databinding.FragmentMenuBottomSheetBinding
import com.example.foodorderingapp.model.MenuItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@SuppressLint("StaticFieldLeak")
private lateinit var binding: FragmentMenuBottomSheetBinding
class MenuBottomSheetFragment : BottomSheetDialogFragment(){

    private lateinit var database: FirebaseDatabase
    private lateinit var menuList : MutableList<MenuItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBottomSheetBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener{
            dismiss()
        }

        //retrieve menu item
        retrieveMenuItem()

        return binding.root
    }

    private fun retrieveMenuItem() {

        database = FirebaseDatabase.getInstance("https://admin-food-ordering-app-b3f0e-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val menuRef = database.getReference("menu")
        menuList = mutableListOf()

        menuRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                menuList.clear()

                for(foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(MenuItem::class.java)
                    menuItem?.let { menuList.add(it) }
                }
                //once data receive, set to adapter
                setAdapter()
            }



            override fun onCancelled(error: DatabaseError) {
                Log.e("DatabaseError", error.message)
            }
        })
    }

    private fun setAdapter() {
        val adapter = MenuAdapter(menuList, requireContext())
        binding.menuRecView.adapter = adapter
        binding.menuRecView.layoutManager = LinearLayoutManager(requireContext())
    }

    companion object {

    }
}