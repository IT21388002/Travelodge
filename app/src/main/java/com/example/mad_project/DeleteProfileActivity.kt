package com.example.mad_project

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DeleteProfileActivity : AppCompatActivity() {

    private lateinit var authProfile: FirebaseAuth

//    private FirebaseUser firebaseUser
//    private EditText editTextUserPassword
//    private TextView textViewAuthenticated
//    private ProgressBar progressBar
//    private String userPassword
//    private Button buttonDeleteUser, buttonReAuthenticate

    private lateinit var editTextUserPassword: EditText
    private lateinit var textViewAuthenticated: TextView
    private lateinit var progressBar: ProgressBar
    private var userPassword: String? = null
    private lateinit var buttonDeleteUser: Button
    private lateinit var buttonReAuthenticate: Button

    private companion object {
        private const val TAG = "DeleteProfileActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_profile)

        getSupportActionBar()?.setTitle("Delete Your Profile")

        progressBar = findViewById(R.id.progressBar)
        editTextUserPassword = findViewById(R.id.editText_delete_user_password)
        textViewAuthenticated = findViewById(R.id.textView_delete_user_authenticated)
        buttonDeleteUser = findViewById(R.id.button_delete_user)
        buttonReAuthenticate = findViewById(R.id.button_delete_user_authenticate)

        //Disable delete user button until user is authenticatedd
        buttonDeleteUser.setEnabled(false)

        authProfile = FirebaseAuth.getInstance()
//        firebaseUser = authProfile.getCurrentUser()
        var firebaseUser = authProfile.currentUser


//        if(firebaseUser.equals("")){
////            Toast.makeText(this, "Something wrong!" + "User details are not available at the moment", Toast.LENGTH_SHORT).show()
////            Intent intent = new Intent(this, UserProfileActivity.class)
////                    startActivity(intent)
////                finish()
////        }else{
////            reAuthenticateUser(firebaseUser)
////        }

        if (firebaseUser == null || firebaseUser.uid.isEmpty()) {
            Toast.makeText(this, "Something wrong! User details are not available at the moment", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            reAuthenticateUser(firebaseUser)
        }
    }

//    private fun reAuthenticateUser(firebaseUser: FirebaseUser) {
//
//        buttonReAuthenticate.setOnClickListener(new View.onClickListener(){
//
//            public void onClick(View v){
//                userPwd = editTextUserPassword.getText().toString()
//
//                if(TextUtils.isEmpty(userPwd)){
//                    Toast.makeText(this, "Password is needed", Toast.LENGTH_SHORT).show()
//                    editTextUserPassword.setError("Please enter your password to authenticate")
//                    editTextUserPassword.requestFocus()
//                }else{
//                    progressBar.setVisibility(View.VISIBLE)
//
//                    //ReAuthenticate user new
//                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwd)
//
//                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>(){
//                        public void onComplete(@NonNull Task<Void> task){
//                            if(task.isSuccessful()){
//                                progressBar.setVisibility(View.GONE)
//
//                                //Disable editText for password.
//                                editTextUserPassword.setEnabled(false)
//
//                                //enable delete user button. disable authenticate button
//                                buttonReAuthenticate.setEnabled(false)
//                                buttonDeleteUser.setEnabled(true)
//
//                                //Set textView to show user is authenticated/verified
//                                textViewAuthenticated.setText("You are authenticated/verified" + "You can delet your profile and related data now")
//                                Toast.makeText(this, "Password has been verified" + "You can delete your profile now. be careful, this action is arreversible", Toast.LENGTH_LONG).show()
//
//                                //update color of change password button
//                                buttonDeleteUser.setBackgroundTintList(ContextCompat.getColorStateList(this, DeleteProfileActivity.this, R.color.dark_green))
//
//                                buttonDeleteUser.setOnClickListener(new View.OnClickListener(){
//                                    public void onClick(View v){
//                                        showAlertDialog(firebaseUser)
//                                    }
//                                })
//                            }else{
//                                try {
//                                    throw task.getException()
//                                }catch (Exception e){
//                                    Toast.makeText(this, e.getMessage(), Toast
//                                        .LENGTH_SHORT).show()
//                                }
//                            }
//                            progressBar.setVisibility(View.GONE)
//                        }
//                    })
//                }
//            }
//        })
//    }

    private fun reAuthenticateUser(firebaseUser: FirebaseUser) {
        buttonReAuthenticate.setOnClickListener {
            val userPwd = editTextUserPassword.text.toString()

            if (userPwd.isEmpty()) {
                Toast.makeText(this, "Password is needed", Toast.LENGTH_SHORT).show()
                editTextUserPassword.setError("Please enter your password to authenticate")
                editTextUserPassword.requestFocus()
            } else {
                progressBar.visibility = View.VISIBLE

                // Re-authenticate user
                val credential = EmailAuthProvider.getCredential(firebaseUser.email!!, userPwd)

                firebaseUser.reauthenticate(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        progressBar.visibility = View.GONE

                        // Disable editText for password.
                        editTextUserPassword.isEnabled = false

                        // Enable delete user button. Disable authenticate button.
                        buttonReAuthenticate.isEnabled = false
                        buttonDeleteUser.isEnabled = true

                        // Set textView to show user is authenticated/verified
                        textViewAuthenticated.text = "You are authenticated/verified. You can delete your profile and related data now."
                        Toast.makeText(this, "Password has been verified. You can delete your profile now. Be careful, this action is irreversible.", Toast.LENGTH_LONG).show()

                        // Update color of delete user button.
                        buttonDeleteUser.backgroundTintList = ContextCompat.getColorStateList(this, R.color.dark_green)

                        buttonDeleteUser.setOnClickListener {
                            showAlertDialog(firebaseUser)
                        }
                    } else {
                        task.exception?.let { e ->
                            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    progressBar.visibility = View.GONE
                }
            }
        }
    }


//    private fun showAlertDialog(firebaseUser: FirebaseUser) {
//
//            val builder = AlertDialog.Builder(this)
//            builder.setTitle("Delete User and Related Data")
//            builder.setMessage("Do you really want to delete your profile and related data? This action irresible")
//
//            builder.setPositiveButton("Continue") { dialog, which ->
//                deleteUser(firebaseUser)
//            }
//
//            //Return to user Profile Activity if user presses cancel button
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
//            public void onClick(DialogInterface dialog, int which){
//                Intent intent = new Intent(this, UserProfileActivity.class)
//                        startActivity(intent)
//                    finish()
//            }
//        })
//
//        //Create the AlertDialog
//            val alertDialog = builder.create()
//
//        //change the button color of countinue
//        alertDialog.setOnShowListener(new DialogInterface.OnShowListener(){
//            public void onShow(DialogInterface dialog){
//                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red))
//
//            }
//        })
//
//
//        //show the alertDialog
//            alertDialog.show()
//    }

    private fun showAlertDialog(firebaseUser: FirebaseUser) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete User and Related Data")
        builder.setMessage("Do you really want to delete your profile and related data? This action is irreversible")

        builder.setPositiveButton("Continue") { dialog, which ->
            deleteUser(firebaseUser)
        }

        // Return to user Profile Activity if user presses cancel button
        builder.setNegativeButton("Cancel") { dialog, which ->
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Create the AlertDialog
        val alertDialog = builder.create()

        // Change the button color of continue
        alertDialog.setOnShowListener { dialog ->
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.red))
        }

        // Show the alertDialog
        alertDialog.show()
    }




//    private fun deleteUser(firebaseUser: FirebaseUser) {
//        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>(){
//
//            public void onComplete(@NonNull Task<Void> task){
//                if(task.isSuccessful()){
//                    authProfile.signOut()
//
//                    Toast.makeText(this, "User has been deleted!", Toast.LENGTH_LONG).show()
//
//                    Intent intent = new Intent(this, MainActivity.class)
//                            startActivity(intent)
//                        finish()
//                }else{
//                    try {
//                        throw task.getException()
//                    }catch (Exception e){
//                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
//                    }
//                }
//                progressBar.setVisibility(View.GONE)
//            }
//        })
//    }

    private fun deleteUser(firebaseUser: FirebaseUser) {
        firebaseUser.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                deleteUserData(firebaseUser)

                authProfile.signOut()

                Toast.makeText(this, "User has been deleted!", Toast.LENGTH_LONG).show()

                val intent = Intent(this, MainActivity::class.java)
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

//    //delete all the data of user
//    private fun deleteUserData() {
//
//        //delete Display picture
//        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance()
//        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(firebaseUser.getPhoneurl().toString())
//        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>(){
//            public void onSuccess(Void unused){
//                Log.d(TAG, "OnSuccess: Photo Deleted")
//            }
//        }).addOnFailureListener(new OnFailureListener(){
//            public void onFailure(@NonNull Exception e){
//                Log.d(TAG, e.getMessage())
//                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
//            }
//        })
//
//        //Delete data from realtime database
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users")
//        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>(){
//            public void onSuccess(Void unused){
//                Log.d(TAG, "OnSuccess: User Data Deleted")
//            }
//        }).addOnFailureListener(new OnFailureListener(){
//            public void onFailure(@NonNull Exception e){
//                Log.d(TAG, e.getMessage())
//                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
//            }
//        })
//    }

    // Delete all the data of the user
    private fun deleteUserData(firebaseUser: FirebaseUser) {

        // Delete Display picture
//        val firebaseStorage = FirebaseStorage.getInstance()
//        val storageReference = firebaseStorage.getReferenceFromUrl(firebaseUser.getPhonerl().toString())
//        storageReference.delete().addOnSuccessListener {
//            Log.d(TAG, "OnSuccess: Photo Deleted")
//        }.addOnFailureListener { e ->
//            e.message?.let { Log.d(TAG, it) }
//            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
//        }

        // Delete data from Realtime Database
        val databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users")
        databaseReference.child(firebaseUser.uid).removeValue().addOnSuccessListener {
            Log.d(TAG, "OnSuccess: User Data Deleted")
        }.addOnFailureListener { e ->
            e.message?.let { Log.d(TAG, it) }
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
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
            }
            R.id.menu_update_email -> {
                val intent = Intent(this, UpdateEmailActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_update_settings -> {
                Toast.makeText(this, "Menu Settings", Toast.LENGTH_SHORT).show()
            }
            R.id.menu_change_password -> {
                val intent = Intent(this, ChangePasswordActivity::class.java)
                startActivity(intent)
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


