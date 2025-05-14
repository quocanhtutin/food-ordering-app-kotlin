package com.example.adminfoodorderingapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminfoodorderingapp.adapter.AllItemAdapter
import com.example.adminfoodorderingapp.databinding.ActivityAllItemBinding
import com.example.adminfoodorderingapp.model.AllMenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@SuppressLint("StaticFieldLeak")
private lateinit var binding: ActivityAllItemBinding
class AllItemActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private var menuItems: MutableList<AllMenuItem> = mutableListOf()
    private var foodKind = "All Items"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAllItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = FirebaseDatabase.getInstance("https://admin-food-ordering-app-b3f0e-default-rtdb.asia-southeast1.firebasedatabase.app")
        databaseReference = database.getReference("menu")

        retrieveMenuItem()

        val kindList = arrayOf("All Items", "Hamburger", "Pizza", "Chicken", "Beef", "Potato", "Beverage")
        val adapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, kindList)
        binding.enterKindOfFood.setAdapter(adapter)
        binding.enterKindOfFood.setOnItemClickListener { parent, view, position, id ->
            foodKind = parent.getItemAtPosition(position).toString()
            retrieveMenuItem()
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }

    private fun retrieveMenuItem() {
        val foodRef = database.reference.child("menu")

        //fetch data from database
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.e("DatabaseError", error.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                //clear existing data
                menuItems.clear()

                //loop for through each data item
                for(foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(AllMenuItem::class.java)
                    menuItem?.let{
                        if(foodKind == "All Items" || it.foodKind == foodKind) {
                            menuItems.add(it)
                        }
                    }
                }
                setAdapter()
            }
        })
    }

    private fun setAdapter() {
        val adapter = AllItemAdapter(menuItems, this@AllItemActivity) {
            position -> deleteItem(position)
        }
        binding.MenuRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.MenuRecyclerView.adapter = adapter
    }

    private fun deleteItem(position: Any) {
        val menuItemToDelete = menuItems[position as Int]
        val menuItemKey = menuItemToDelete.key
        val menuRef = database.getReference("menu").child(menuItemKey!!)
        menuRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful){
                menuItems.removeAt(position)
                binding.MenuRecyclerView.adapter?.notifyItemRemoved(position)
                binding.MenuRecyclerView.adapter?.notifyItemRangeChanged(position, menuItems.size)
            }
            else{
                Toast.makeText(this, "Failed to delete item", Toast.LENGTH_SHORT).show()
            }
        }
    }


}