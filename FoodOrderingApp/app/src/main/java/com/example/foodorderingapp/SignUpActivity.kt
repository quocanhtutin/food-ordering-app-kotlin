package com.example.foodorderingapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.provider.CreateEntry
import com.example.foodorderingapp.databinding.ActivitySignUpBinding
import com.example.foodorderingapp.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

@SuppressLint("StaticFieldLeak")
private lateinit var binding: ActivitySignUpBinding
@Suppress("DEPRECATION")
class SignUpActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var username: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //initialize Firebase auth
        auth = Firebase.auth
        //initialize Firebase database
        database = FirebaseDatabase.getInstance("https://admin-food-ordering-app-b3f0e-default-rtdb.asia-southeast1.firebasedatabase.app/")
        databaseReference = database.reference
        //initialize Google
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOption)

        binding.btnCreateAccount.setOnClickListener {
            email = binding.editTextTextEmailAddress2.text.toString().trim()
            password = binding.editTextTextPassword2.text.toString().trim()
            username = binding.editTextText.text.toString().trim()

            if (email.isEmpty()|| password.isEmpty() || username.isEmpty()) {
                binding.editTextTextEmailAddress2.error = "Please enter email"
                binding.editTextTextPassword2.error = "Please enter password"
                binding.editTextText.error = "Please enter username"
            } else {
                CreateAccount(email, password)
            }

        }

        binding.txtLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnGoogle.setOnClickListener {
            val signInClient = googleSignInClient.signInIntent
            launcher.launch(signInClient)
        }
    }

    //launcher for google sign in
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if(task.isSuccessful){
                val account: GoogleSignInAccount? = task.result
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, "Sign in successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    else{
                        Toast.makeText(this, "Google Sign In Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }else{
            Toast.makeText(this, "Google Sign In Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun CreateAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password). addOnCompleteListener {
            task->
            if (task.isSuccessful) {
                Toast.makeText(this, "Create Account Successfully", Toast.LENGTH_SHORT).show()
                saveUserData()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            else{
                Toast.makeText(this, "Create Account Failed", Toast.LENGTH_SHORT).show()
                Log.e("TAG", "CreateAccount: ${task.exception}")
            }
        }
    }

    private fun saveUserData() {
        //retrieve data from input filled
        username = binding.editTextText.text.toString().trim()
        email = binding.editTextTextEmailAddress2.text.toString().trim()
        password = binding.editTextTextPassword2.text.toString().trim()
        //create user object
        val user = UserModel(username, email, password)
        val userId = auth.currentUser!!.uid
        //save data to Firebase Database
        databaseReference.child("Users").child(userId).setValue(user)

    }
}