package com.example.foodorderingapp.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.R
import com.example.foodorderingapp.adapter.MenuAdapter
import com.example.foodorderingapp.databinding.FragmentSearchBinding
import com.example.foodorderingapp.model.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@SuppressLint("StaticFieldLeak")
private lateinit var binding: FragmentSearchBinding
class SearchFragment : Fragment() {

    private val filterMenu = mutableListOf<MenuItem>()
    private lateinit var database: FirebaseDatabase
    private lateinit var menuList : MutableList<MenuItem>
    private lateinit var adapter: MenuAdapter
    private var foodKind = "All Items"
   
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        adapter = MenuAdapter(filterMenu, this.requireContext())

        binding.menuRecView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecView.adapter = adapter


        //set up for search view
        retrieveMenuItem()
        setupSearchView()

        val kindList = arrayOf("All Items", "Hamburger", "Pizza", "Chicken", "Beef", "Potato", "Beverage")
        val adapter = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, kindList)
        binding.enterKindOfFood.setAdapter(adapter)
        binding.enterKindOfFood.setOnItemClickListener { parent, view, position, id ->
            foodKind = parent.getItemAtPosition(position).toString()
            retrieveMenuItem()
        }

        //show all menu items
//        showAllMenu()

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showAllMenu() {
        filterMenu.clear()
        filterMenu += menuList //.addAll(menuList)

        adapter.notifyDataSetChanged()
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
                    menuItem?.let {
                        if(foodKind == "All Items" || it.foodKind == foodKind) {
                            menuList.add(it)
                        }
                    }
                }
                //once data receive, set to adapter
                showAllMenu()
            }



            override fun onCancelled(error: DatabaseError) {
                Log.e("DatabaseError", error.message)
            }
        })
    }


    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                filterMenu(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterMenu(newText)
                return true
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterMenu(query: String) {
        filterMenu.clear()

        menuList.forEachIndexed { _, menuItems ->
            if(menuItems.foodName!!.contains(query, ignoreCase = true)){
                filterMenu.add(menuItems)
            }
        }

        adapter.notifyDataSetChanged()
    }

    companion object {
       
    }
}