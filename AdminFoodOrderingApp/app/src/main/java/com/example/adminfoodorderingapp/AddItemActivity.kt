package com.example.adminfoodorderingapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminfoodorderingapp.databinding.ActivityAddItemBinding
import com.example.adminfoodorderingapp.model.AllMenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import okhttp3.*
import java.io.IOException
import java.io.File

@SuppressLint("StaticFieldLeak")
private lateinit var binding: ActivityAddItemBinding
class AddItemActivity : AppCompatActivity() {

    val clientId = "3ebf81a04e9f960"
    private var imageFile: File? = null

    //food item details
    private lateinit var foodName: String
    private lateinit var foodPrice: String
    private var foodImage: Uri? = null
    private lateinit var shortDescription: String
    private lateinit var ingredients: String
    private lateinit var foodKind: String

    //Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //initialize Firebase
        auth = FirebaseAuth.getInstance()
        //initialize database
        database = FirebaseDatabase.getInstance("https://admin-food-ordering-app-b3f0e-default-rtdb.asia-southeast1.firebasedatabase.app")

        val kindList = arrayOf("Hamburger", "Pizza", "Chicken", "Beef", "Potato", "Beverage")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, kindList)
        binding.enterKindOfFood.setAdapter(adapter)

        binding.btnAddItem.setOnClickListener {
            //get data from edit text
            foodName = binding.edtFoodName.text.toString().trim()
            foodPrice = binding.edtFoodPrice.text.toString().trim()
            shortDescription = binding.edtShortDescription.text.toString().trim()
            ingredients = binding.edtIngredients.text.toString().trim()
            foodKind = binding.enterKindOfFood.text.toString().trim()


            if(foodName.isEmpty()||foodPrice.isEmpty()||shortDescription.isEmpty()||ingredients.isEmpty()||foodKind.isEmpty()){
                binding.edtFoodName.error = "Please enter food name"
                binding.edtFoodPrice.error = "Please enter food price"
                binding.edtShortDescription.error = "Please enter short description"
                binding.edtIngredients.error = "Please enter ingredients"
                binding.enterKindOfFood.error = "Please enter kind of food"
            }
            else{
                uploadData()
            }
        }

        binding.selectImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun uploadData() {
        //get a reference to the menu node in the data base
        val menuRef = database.getReference("menu")
        //generate a unique key for the new item
        val newItemKey = menuRef.push().key

//        if(foodImage != null) {
//            val storageRef = FirebaseStorage.getInstance().reference
//            val imageRef = storageRef.child("menu_images/${newItemKey}.jpg")
//            val uploadTask = imageRef.putFile(foodImage!!)
//
//            uploadTask.addOnCompleteListener {
//                imageRef.downloadUrl.addOnCompleteListener { downloadUrl ->
//                    //Create new menu Item
//                    val newItem = AllMenuItem(
//                        foodName,
//                        foodPrice,
//                        downloadUrl.toString(),
//                        shortDescription,
//                        ingredients,
//                        foodKind,
//                        newItemKey
//                    )
//                    newItemKey?.let { key ->
//                        menuRef.child(key).setValue(newItem).addOnCompleteListener {
//                            Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT)
//                                .show()
//                            binding.edtFoodName.text?.clear()
//                            binding.edtFoodPrice.text?.clear()
//                            binding.edtShortDescription.text?.clear()
//                            binding.edtIngredients.text?.clear()
//                            binding.selectedImage.setImageURI(null)
//                            binding.enterKindOfFood.text?.clear()
//                        }
//                            .addOnFailureListener {
//                                Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT)
//                                    .show()
//                            }
//                    }
//                }.addOnFailureListener {
//                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }else{
//            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
//        }

        if(foodImage != null) {
            uploadImageToImgur(foodImage!!, clientId) { imageUrl ->
                if (imageUrl != null) {
                    val newItem = AllMenuItem(
                        foodName,
                        foodPrice,
                        imageUrl,
                        shortDescription,
                        ingredients,
                        foodKind,
                        newItemKey
                    )
                    newItemKey?.let { key ->
                        menuRef.child(key).setValue(newItem).addOnCompleteListener {
                            Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT)
                                .show()
                            binding.edtFoodName.text?.clear()
                            binding.edtFoodPrice.text?.clear()
                            binding.edtShortDescription.text?.clear()
                            binding.edtIngredients.text?.clear()
                            binding.selectedImage.setImageURI(null)
                            binding.enterKindOfFood.text?.clear()
                        }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }
        }
        else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()){uri ->
        if(uri!=null)
        {
            binding.selectedImage.setImageURI(uri)
            foodImage = uri
            imageFile = uri.path?.let { File(it) }!!
            Log.d("Image File", imageFile.toString())
        }
    }

    private fun uploadImageToImgur(imageUri: Uri, clientId: String, onResult: (String?) -> Unit) {
        val inputStream = contentResolver.openInputStream(imageUri)
        if (inputStream == null) {
            Toast.makeText(this, "Không thể mở ảnh", Toast.LENGTH_SHORT).show()
            return
        }

        val imageBytes = inputStream.readBytes()
        val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)

        val requestBody = FormBody.Builder()
            .add("image", base64Image)
            .build()

        val request = Request.Builder()
            .url("https://api.imgur.com/3/image")
            .header("Authorization", "Client-ID $clientId")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResult(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        onResult(null)
                        return
                    }
                    val json = response.body?.string()
                    // Extract URL from JSON (đơn giản, nên dùng org.json hoặc Moshi)
                    val link = Regex("\"link\":\"(.*?)\"").find(json ?: "")?.groupValues?.get(1)
                    val cleanedLink = link?.replace("\\/", "/") // Bỏ escape
                    onResult(cleanedLink)
                }
            }
        })
    }
}