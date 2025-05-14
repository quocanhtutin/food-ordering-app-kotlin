package com.example.foodorderingapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.foodorderingapp.databinding.ActivityDetailBinding
import com.example.foodorderingapp.model.CartItems
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase

@SuppressLint("StaticFieldLeak")
private lateinit var binding: ActivityDetailBinding
class DetailActivity : AppCompatActivity() {

    private var foodName: String? = null
    private var foodImage: String? = null
    private var foodPrice: String? = null
    private var shortDescription: String? = null
    private var ingredients: String? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //initialize firebase auth
        auth = Firebase.auth

        foodName = intent.getStringExtra("MenuItemName")
        foodImage = intent.getStringExtra("MenuItemImage")
        foodPrice = intent.getStringExtra("MenuItemPrice")
        shortDescription = intent.getStringExtra("MenuItemDescription")
        ingredients = intent.getStringExtra("MenuItemIngredients")

        binding.foodName.text = foodName
        binding.shortdescDetail.text = shortDescription
        binding.ingredientsDetail.text = ingredients
        Glide.with(this@DetailActivity).load(foodImage).into(binding.foodImageDetail)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.addItemButton.setOnClickListener {
            addItemToCart()
        }

    }

    private fun addItemToCart() {
        val database = FirebaseDatabase.getInstance("https://admin-food-ordering-app-b3f0e-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val userId = auth.currentUser?.uid?:""
        //Create a card object
        val cartItem = CartItems(foodName, foodPrice, foodImage, shortDescription,ingredients, 1)

        //save data to cart item to database
        database.reference.child("Users").child(userId).child("cart").push().setValue(cartItem).addOnSuccessListener {
            Toast.makeText(this, "Items added into cart successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to add items into cart", Toast.LENGTH_SHORT).show()
        }
    }
}