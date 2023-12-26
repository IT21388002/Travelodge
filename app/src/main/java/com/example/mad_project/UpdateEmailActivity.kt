package com.example.mad_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UpdateEmailActivity : AppCompatActivity() {

    private lateinit var authProfile: FirebaseAuth

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var progressBar: ProgressBar
    private lateinit var textViewAuthenticated: TextView
    private lateinit var userOldEmail: String
    private lateinit var userNewEmail: String
    private lateinit var userPassword: String
    private lateinit var buttonUpdateEmail: Button
    private lateinit var editTextNewEmail: EditText
    private lateinit var editTextPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_email)

        supportActionBar?.title = "Update Email"

        progressBar = findViewById(R.id.progressBar)
        editTextPassword = findViewById(R.id.editText_update_email_verify_password)
        editTextNewEmail = findViewById(R.id.editText_update_email_new)
        textViewAuthenticated = findViewById(R.id.textView_update_email_authenticated)
        buttonUpdateEmail = findViewById(R.id.button_update_email)

        buttonUpdateEmail.isEnabled = false
        editTextNewEmail.isEnabled = false

        authProfile = FirebaseAuth.getInstance()
        firebaseUser = authProfile.currentUser!!

        // set old email ID on text
        val userOldEmail = firebaseUser?.email
        val textViewOldEmail: TextView = findViewById(R.id.textView_update_email_old)
        textViewOldEmail.text = userOldEmail

        if (firebaseUser == null) {
            Toast.makeText(this, "Something went wrong! User's details not available", Toast.LENGTH_LONG).show()
        } else {
            reAuthenticate(firebaseUser)
        }
    }

    //reAthenticate/verify user before updating email

    private fun reAuthenticate(firebaseUser: FirebaseUser) {
        val buttonVerifyUser = findViewById<Button>(R.id.button_authenticate_user)
        buttonVerifyUser.setOnClickListener {
            // Obtain password for authentication
            val userPassword = editTextPassword.text.toString()

            if(userPassword.isEmpty()) {
                Toast.makeText(this, "Password is needed to continue", Toast.LENGTH_SHORT).show()
                editTextPassword.setError("Please enter your password for authentication")
                editTextPassword.requestFocus()
            } else {
                progressBar.visibility = View.VISIBLE

                val credential = EmailAuthProvider.getCredential(firebaseUser.email!!, userPassword)

                firebaseUser.reauthenticate(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        progressBar.visibility = View.GONE

                        Toast.makeText(this, "Password has been verified. You can update your email now", Toast.LENGTH_LONG).show()

                        // Set textView to show that user is authenticated
                        textViewAuthenticated.text = "You are authenticated. You can update your email now."

                        // Disable editText for password, button to verify user and enable EditText for new Email button
                        editTextNewEmail.isEnabled = true
                        editTextPassword.isEnabled = false
                        buttonVerifyUser.isEnabled = false
                        buttonUpdateEmail.isEnabled = true

                        // Change color of update email button
                        buttonUpdateEmail.backgroundTintList = ContextCompat.getColorStateList(this, R.color.dark_green)

                        buttonUpdateEmail.setOnClickListener {
                            val userNewEmail = editTextNewEmail.text.toString()

                            if(userNewEmail.isEmpty()) {
                                Toast.makeText(this, "New email is required", Toast.LENGTH_SHORT).show()
                                editTextNewEmail.setError("Please enter new email")
                                editTextNewEmail.requestFocus()
                            } else if(!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()) {
                                Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show()
                                editTextNewEmail.setError("Please provide valid email")
                                editTextNewEmail.requestFocus()
                            } else if(firebaseUser.email == userNewEmail) {
                                Toast.makeText(this, "New email cannot be same as old email", Toast.LENGTH_SHORT).show()
                                editTextNewEmail.setError("Please enter new email")
                                editTextNewEmail.requestFocus()
                            } else {
                                progressBar.visibility = View.VISIBLE
                                updateEmail(firebaseUser, userNewEmail)
                            }
                        }
                    } else {
                        try {
                            throw task.exception!!
                        } catch(e: Exception) {
                            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun updateEmail(firebaseUser: FirebaseUser, userNewEmail: String) {
        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener { task ->
            if(task.isSuccessful) {

                // Verify Email
                firebaseUser.sendEmailVerification()

                Toast.makeText(this, "Email has been updated. Please verify your new email", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, UserProfileActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                try {
                    throw task.exception!!
                } catch(e: Exception) {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
            }
            progressBar.visibility = View.GONE
        }
    }

    //Creating Action bar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //Inflate menu item
        menuInflater.inflate(R.menu.common_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //When any menu item is selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> {
                //Refresh Activity
                startActivity(intent)
                finish()
                overridePendingTransition(0, 0)
            }
            R.id.menu_update_profile -> {
                val intent = Intent(this, UpdateProfileActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.menu_update_email -> {
                val intent = Intent(this, UpdateEmailActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.menu_update_settings -> {
                Toast.makeText(this, "Menu Settings", Toast.LENGTH_SHORT).show()
            }
            R.id.menu_change_password -> {
                val intent = Intent(this, ChangePasswordActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.menu_delete_profile -> {
                val intent = Intent(this, DeleteProfileActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.menu_logout -> {
                authProfile.signOut()
                Toast.makeText(this, "Logged Out", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)

                //Clear stack to prevent user coming back to UserProfileActivity on Pressing back button after Logging out
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()        //Close UserProfileActivity
            }
            else -> {
                Toast.makeText(this, "Something Wrong!", Toast.LENGTH_LONG).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

