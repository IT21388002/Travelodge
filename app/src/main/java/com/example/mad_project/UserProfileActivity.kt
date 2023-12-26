//package com.example.mad_project
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//
//class UserProfileActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_user_profile)
//    }
//}


package com.example.mad_project

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NavUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class UserProfileActivity : AppCompatActivity() {

    private lateinit var textViewWelcome: TextView
    private lateinit var textViewFullName: TextView
    private lateinit var textViewEmail: TextView
    private lateinit var textViewDob: TextView
    private lateinit var textViewGender: TextView
    private lateinit var textViewMobile: TextView

    private lateinit var imageView: ImageView


    private lateinit var progressBar: ProgressBar

    private lateinit var authProfile: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        supportActionBar?.title = "Home"

        //        getSupportActionBar.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        textViewWelcome = findViewById(R.id.textView_show_welcome)
        textViewFullName = findViewById(R.id.textView_show_full_name)
        textViewEmail = findViewById(R.id.textView_show_email)
        textViewDob = findViewById(R.id.textView_show_dob)
        textViewGender = findViewById(R.id.textView_show_gender)
        textViewMobile = findViewById(R.id.textView_show_mobile)
        progressBar = findViewById(R.id.progressBar)

        //Set OnClickListener on ImageView to open UploadProfilePictureActivity
//        imageView = findViewById(R.id.imageView_profile_picture)
//        imageView.setOnClickListener(new View.OnClickListener()){
//            public void onClick(View v){
//                Intent intent = new Intent(this, UploadProfilePictureActivity.class)
//                        startActivity(intent)
//            }
//        }

//        imageView = findViewById(R.id.imageView_profile_picture)
//        imageView.setOnClickListener {
//            val intent = Intent(this, UploadProfilePictureActivity::class.java)
//            startActivity(intent)
//        }



        authProfile = FirebaseAuth.getInstance()
        val firebaseUser = authProfile.currentUser

        if (firebaseUser == null) {
            Toast.makeText(
                this,
                "Something went wrong! User's details are not available at the moment",
                Toast.LENGTH_LONG
            ).show()
        } else {
            checkIfEmailVerified(firebaseUser)
            progressBar.visibility = View.VISIBLE
            showUserProfile(firebaseUser)
        }
    }

    private fun checkIfEmailVerified(firebaseUser: FirebaseUser) {
        if (!firebaseUser.isEmailVerified()) {
            showAlertDialog()
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Email Not Verified")
        builder.setMessage("Please verify your email now. You cannot login without email verification next time")

        builder.setPositiveButton("Continue") { dialog, which ->
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_EMAIL)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun showUserProfile(firebaseUser: FirebaseUser) {
        val userID = firebaseUser.uid

        // Extracting user reference from database for "Registered Users"
        val referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users")

        referenceProfile.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                val readUserDetails = snapshot.getValue(ReadWriteUserDetails::class.java)

                readUserDetails?.let {
                    val fullName = firebaseUser.displayName
                    val email = firebaseUser.email
                    val doB = readUserDetails.doB
                    val gender = readUserDetails.gender
                    val mobile = readUserDetails.mobile

                    textViewWelcome.text = "Welcome $fullName!"
                    textViewFullName.text = fullName
                    textViewEmail.text = email
                    textViewDob.text = doB
                    textViewGender.text = gender
                    textViewMobile.text = mobile

                } ?: run {
                    Toast.makeText(
                        this@UserProfileActivity,
                        "Failed to read user details from database!",
                        Toast.LENGTH_LONG
                    ).show()
                }

                progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@UserProfileActivity,
                    "Failed to read user details from database!",
                    Toast.LENGTH_LONG
                ).show()
                progressBar.visibility = View.GONE
            }
        })
    }

//    //Creating Action bar
//    public boolean onCreateOptionsMenu(Menu menu){
//        //Inflate menu item
//        getMenuInflater().inflate(R.menu.common_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    //When any menu item is selected
//    public boolean onOptionsItemSelected(@NonNull MenuItem item){
//        int id = item.getItemId()
//
//        if(id == R.id.menu_refresh){
//            //Refresh Activity
//            startActivity(getIntent())
//            finish()
//            overridePendingTransition(0,0)
//        }else if(id== R.id.menu_update_profile){
//            Intent intent = new Intent(this, UpdateProfileActivity.class)
//                    startActivity(intent)
//        }else if(id == R.id.menu_update_email){
//            Intent intent = new Intent(this, UpdateEmailActivity.class)
//                    startActivity(intent)
//        }else if(id == R.id.menu_update_settings){
//            Toast.makeText(this, "Menu Settings", Toast.LENGTH_SHORT).show()
//        }else if(id == R.id.menu_change_password){
//            Intent intent = new Intent(this, ChangePasswordActivity.class)
//                    startActivity(intent)
//        }else if(id == R.id.menu_delete_profile){
//            Intent intent = new Intent(this, DeleteProfileActivity.class)
//                    startActivity(intent)
//        }else if(id == R.id.menu_logout){
//            authProfile.signOut()
//            Toast.makeText(this, "Logged Out", Toast.LENGTH_LONG).show()
//            Intent intent = new Intent(this, MainActivity.class)
//
//            //Clear stack to prevent user coming back to UserProfileActivity on Pressing back button after Logging out
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//            finish()        //Close UserProfileActivity
//        }else{
//            Toast.makeText(this, "Something Wrong!", Toast.LENGTH_LONG).show()
//        }
//
//        }
//        return super.onOptionsItemSelected(item)
//    }

    //Creating Action bar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //Inflate menu item
        menuInflater.inflate(R.menu.common_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //When any menu item is selected
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//
//            if (id == android.R.id.home) {
//                NavUtils.navigateUpFromSameTask(this)
//            } R.id.menu_refresh -> {
//                //Refresh Activity
//                startActivity(intent)
//                finish()
//                overridePendingTransition(0, 0)
//            }
//            R.id.menu_update_profile -> {
//                val intent = Intent(this, UpdateProfileActivity::class.java)
//                startActivity(intent)
//            }
//            R.id.menu_update_email -> {
//                val intent = Intent(this, UpdateEmailActivity::class.java)
//                startActivity(intent)
//            }
//            R.id.menu_update_settings -> {
//                Toast.makeText(this, "Menu Settings", Toast.LENGTH_SHORT).show()
//            }
//            R.id.menu_change_password -> {
//                val intent = Intent(this, ChangePasswordActivity::class.java)
//                startActivity(intent)
//            }
//            R.id.menu_delete_profile -> {
//                val intent = Intent(this, DeleteProfileActivity::class.java)
//                startActivity(intent)
//            }
//            R.id.menu_logout -> {
//                authProfile.signOut()
//                Toast.makeText(this, "Logged Out", Toast.LENGTH_LONG).show()
//                val intent = Intent(this, MainActivity::class.java)
//
//                //Clear stack to prevent user coming back to UserProfileActivity on Pressing back button after Logging out
//                intent.flags =
//                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                startActivity(intent)
//                finish()        //Close UserProfileActivity
//            }
//            else -> {
//                Toast.makeText(this, "Something Wrong!", Toast.LENGTH_LONG).show()
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
            R.id.menu_refresh -> {
                // Refresh activity
                finish()
                startActivity(intent)
                overridePendingTransition(0, 0)
                return true
            }
            R.id.menu_update_profile -> {
                val intent = Intent(this, UpdateProfileActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.menu_update_email -> {
                val intent = Intent(this, UpdateEmailActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.menu_update_settings -> {
                Toast.makeText(this, "Menu Settings", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.menu_change_password -> {
                val intent = Intent(this, ChangePasswordActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.menu_delete_profile -> {
                val intent = Intent(this, DeleteProfileActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.menu_logout -> {
                authProfile.signOut()
                Toast.makeText(this, "Logged Out", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)

                // Clear stack to prevent user from coming back to UserProfileActivity on pressing back button after logging out
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish() // Close UserProfileActivity
                return true
            }
            else -> {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()
                return super.onOptionsItemSelected(item)
            }
        }
    }


}

//    //Creating Action bar
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        //Inflate menu item
//        menuInflater.inflate(R.menu.common_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    //When any menu item is selected
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.menu_refresh -> {
//                //Refresh Activity
//                startActivity(intent)
//                finish()
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
//}



//class UserProfileActivity : AppCompatActivity() {
//
//    private lateinit var textViewWelcome: TextView
//    private lateinit var textViewFullName: TextView
//    private lateinit var textViewEmail: TextView
//    private lateinit var textViewDob: TextView
//    private lateinit var textViewGender: TextView
//    private lateinit var textViewMobile: TextView
//
//    private lateinit var progressBar: ProgressBar
//
//    private lateinit var fullName: String
//    private lateinit var email: String
//    private lateinit var dob: String
//    private lateinit var gender: String
//    private lateinit var mobile: String
//
//    private lateinit var imageView: ImageView
//    private lateinit var authProfile: FirebaseAuth
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_user_profile)
//
//        supportActionBar?.title = "Home"
//
//        textViewWelcome = findViewById(R.id.textView_show_welcome)
//        textViewFullName = findViewById(R.id.textView_show_full_name)
//        textViewEmail = findViewById(R.id.textView_show_email)
//        textViewDob = findViewById(R.id.textView_show_dob)
//        textViewGender = findViewById(R.id.textView_show_gender)
//        textViewMobile = findViewById(R.id.textView_show_mobile)
//        progressBar = findViewById(R.id.progressBar)
//
//        authProfile = FirebaseAuth.getInstance()
//        val firebaseUser = authProfile.currentUser
//
//        if (firebaseUser == null) {
//            Toast.makeText(this, "Something went wrong! User's details are not available at the moment", Toast.LENGTH_LONG).show()
//        } else {
//            checkIfEmailVerified(firebaseUser)
//            progressBar.visibility = View.VISIBLE
//            showUserProfile(firebaseUser)
//        }
//    }
//
//    private fun checkIfEmailVerified(firebaseUser: FirebaseUser) {
//        if (!firebaseUser.isEmailVerified()) {
//            showAlertDialog()
//        }
//    }
//
//    private fun showAlertDialog() {
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Email Not Verified")
//        builder.setMessage("Please verify your email now. You cannot login without email verification next time")
//
//        builder.setPositiveButton("Continue") { dialog, which ->
//            val intent = Intent(Intent.ACTION_MAIN).apply {
//                addCategory(Intent.CATEGORY_APP_EMAIL)
//                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            }
//            startActivity(intent)
//        }
//
//        val alertDialog = builder.create()
//        alertDialog.show()
//    }
//
//    private fun showUserProfile(firebaseUser: FirebaseUser) {
//        val userID = firebaseUser.uid
//
//        //Extracting user Reference from Database for "Registered Users"
//        val referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users")
//        referenceProfile.child(userID)
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                @SuppressLint("SetTextI18n")
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val readUserDetails = snapshot.getValue(ReadWriteUserDetails::class.java)
//
//                    if (readUserDetails != null) {
//                        val fullName = firebaseUser.displayName
//                        val email = firebaseUser.email
//                        val doB = readUserDetails.doB
//                        val gender = readUserDetails.gender
//                        val mobile = readUserDetails.mobile
//
//                        textViewWelcome.text = "Welcome $fullName!"
//                        textViewFullName.text = fullName
//                        textViewEmail.text = email
//                        textViewDob.text = doB
//                        textViewGender.text = gender
//                        textViewMobile.text = mobile
//                    }
//                    progressBar.visibility = View.GONE
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Toast.makeText(
//                        this@UserProfileActivity,
//                        "Failed to read user details from database!",
//                        Toast.LENGTH_LONG
//                    ).show()
//                    progressBar.visibility = View.GONE
//                }
//            })
//    }
//}
