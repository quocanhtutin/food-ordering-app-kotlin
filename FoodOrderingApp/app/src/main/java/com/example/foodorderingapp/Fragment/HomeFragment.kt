package com.example.foodorderingapp.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.foodorderingapp.R
import com.example.foodorderingapp.adapter.MenuAdapter
import com.example.foodorderingapp.databinding.FragmentHomeBinding
import com.example.foodorderingapp.model.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@SuppressLint("StaticFieldLeak")
private lateinit var binding: FragmentHomeBinding
class HomeFragment : Fragment() {

    private lateinit var database: FirebaseDatabase
    private lateinit var menuItem: MutableList<MenuItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.viewMenu.setOnClickListener {
            val bottomSheetDialog = MenuBottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager, "Test")
        }

        //retrieve and display popular item
        retrieveAndDisplayPopularItem()
        return binding.root
    }

    private fun retrieveAndDisplayPopularItem() {
        //get reference to the database
        database = FirebaseDatabase.getInstance("https://admin-food-ordering-app-b3f0e-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val menuRef = database.getReference("menu")
        menuItem = mutableListOf()

        //retrieve menu item from database
        menuRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(foodSnapshot in snapshot.children){
                    val food = foodSnapshot.getValue(MenuItem::class.java)
                    menuItem.add(food!!)
                }
                //display a random popular item
                randomPopularItem()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun randomPopularItem() {
        //create as shuffled of menu items
        val index = menuItem.indices.toList().shuffled()
        val numItemsToShow = 6
        val subsetMenuItems = index.take(numItemsToShow).map{
            menuItem[it]
        }

        setPopularItemAdapter(subsetMenuItems as MutableList<MenuItem>)
    }

    private fun setPopularItemAdapter(subsetMenuItems: MutableList<MenuItem>) {
        val adapter = MenuAdapter(subsetMenuItems, requireContext())
        binding.popularReV.layoutManager = LinearLayoutManager(requireContext())
        binding.popularReV.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banhdacuahaiphong, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banhcanhvit, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banhcanhghe, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.comgahoian, ScaleTypes.FIT))

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList, ScaleTypes.FIT)
        imageSlider.setItemClickListener(object :ItemClickListener{
            override fun doubleClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(position: Int) {
                val itemPosition = imageList[position]
                val itemMessage = "Selected Image $itemPosition"
                Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
    }
}