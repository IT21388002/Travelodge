package com.example.mad_project

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var authProfile: FirebaseAuth

    private lateinit var editTextPasswordCurrent: EditText
    private lateinit var editTextPasswordNew: EditText
    private lateinit var editTextPasswordConfirmNew: EditText
    private lateinit var textViewAuthenticated: TextView
    private lateinit var buttonChangePassword: Button
    private lateinit var buttonAuthenticate: Button
    private lateinit var buttonReAuthenticate: Button
    private lateinit var progressBar: ProgressBar
    private var userPasswordCurrent: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        supportActionBar?.title = "Change Password"

        // Initialize views
        editTextPasswordCurrent = findViewById(R.id.editText_change_password_current)
        editTextPasswordNew = findViewById(R.id.editText_update_change_password_new)
        editTextPasswordConfirmNew = findViewById(R.id.editText_update_change_password_new_confirm)
        textViewAuthenticated = findViewById(R.id.textView_change_password_authenticated)
        buttonChangePassword = findViewById(R.id.button_update_change_password)
        buttonAuthenticate = findViewById(R.id.button_change_password_authenticate)
        progressBar = findViewById(R.id.progressBar)

        //Disable editText for New Password, confirm new password and Make change password button unClick till user authenticate
        editTextPasswordNew.isEnabled = false
        editTextPasswordConfirmNew.isEnabled = false
        buttonChangePassword.isEnabled = false

        val authProfile = FirebaseAuth.getInstance()
        val firebaseUser = authProfile.currentUser

        if(firebaseUser == null){
            Toast.makeText(this, "Something went wrong! User's details are not available.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            reAuthenticateUser(firebaseUser)
        }
    }

    //reAuthenticate User before changing password
    @SuppressLint("SetTextI18n")
    private fun reAuthenticateUser(firebaseUser: FirebaseUser) {
        buttonAuthenticate.setOnClickListener {
            val userPasswordCurrent = editTextPasswordCurrent.text.toString()

            if (userPasswordCurrent.isEmpty()) {
                Toast.makeText(this, "Password is needed", Toast.LENGTH_SHORT).show()
                editTextPasswordCurrent.error = "Please enter current password to authenticate"
                editTextPasswordCurrent.requestFocus()
            } else {
                progressBar.visibility = View.VISIBLE

                //reAuthenticate user now
                val credential = EmailAuthProvider.getCredential(firebaseUser.email!!, userPasswordCurrent)

                firebaseUser.reauthenticate(credential).addOnCompleteListener { task ->
                    progressBar.visibility = View.GONE

                    if (task.isSuccessful) {
                        //Disable edittext for current password. enable editText for new password and confirm new password
                        editTextPasswordCurrent.isEnabled = false
                        editTextPasswordNew.isEnabled = true
                        editTextPasswordConfirmNew.isEnabled = true

                        //enable change password button. disable authenticate button
                        buttonReAuthenticate.isEnabled = false
                        buttonChangePassword.isEnabled = true

                        //set textView to show user is authenticated/verified
                        textViewAuthenticated.text = "You are authenticated/verified. You can change password now"
                        Toast.makeText(this, "Password has been verified. Change password now", Toast.LENGTH_SHORT).show()

                        //update color of change password button
                        buttonChangePassword.backgroundTintList = ContextCompat.getColorStateList(this, R.color.dark_green)

                        buttonChangePassword.setOnClickListener {
                            changePassword(firebaseUser)
                        }
                    } else {
                        try {
                            throw task.exception!!
                        } catch (e: Exception) {
                            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun changePassword(firebaseUser: FirebaseUser) {

        val userPasswordNew = editTextPasswordNew.text.toString()
        val userPasswordConfirmNew = editTextPasswordConfirmNew.text.toString()

        if (TextUtils.isEmpty(userPasswordNew)) {
            Toast.makeText(this, "New password is needed", Toast.LENGTH_SHORT).show()
            editTextPasswordNew.error = "Please enter your new password"
            editTextPasswordNew.requestFocus()
        } else if (TextUtils.isEmpty(userPasswordConfirmNew)) {
            Toast.makeText(this, "Please confirm your new password", Toast.LENGTH_SHORT).show()
            editTextPasswordConfirmNew.error = "Please re-enter your new password"
            editTextPasswordConfirmNew.requestFocus()
        } else if (userPasswordNew != userPasswordConfirmNew) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            editTextPasswordConfirmNew.error = "Please re-enter your new password"
            editTextPasswordConfirmNew.requestFocus()
        } else if (userPasswordCurrent == userPasswordNew) {
            Toast.makeText(this, "New password cannot be the same as the old password", Toast.LENGTH_SHORT).show()
            editTextPasswordNew.error = "Please enter a new password"
            editTextPasswordNew.requestFocus()
        } else {
            progressBar.visibility = View.VISIBLE

            firebaseUser.updatePassword(userPasswordNew).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password has been changed", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, UserProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: Exception) {
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
                progressBar.visibility = View.GONE
            }
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

