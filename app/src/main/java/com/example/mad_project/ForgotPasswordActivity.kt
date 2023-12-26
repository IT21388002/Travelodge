// package com.example.mad_project
//
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.text.TextUtils
//import android.util.Patterns
//import android.view.View
//import android.widget.Toast
//import androidx.annotation.NonNull
//import com.google.android.gms.tasks.Task
//import com.google.firebase.auth.FirebaseAuth
//
// class ForgotPasswordActivity : AppCompatActivity() {
//
//    private Button buttonPasswordReset
//    private EditText editTextPasswordResetEmail
//    private PrograssBar prograssBar
//    private FirebaseAuth authProfile
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_forgot_password)
//
//        getSupportActionBar().setTitle("Forgot Password")
//
//
//        editTextPasswordResetEmail = findViewById(R.id.editText_password_reset_email)
//        buttonPasswordReset = findViewById(R.id.button_password_reset)
//        prograssBar findViewById(R.id.progressBar)
//
//        buttonPasswordReset.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//                if (TextUtils.isEmpty(email)){
//                    Toast.makeText(this, "Please enter your registered email", Toast.LENGTH_SHORT).show()
//                    editTextPasswordResetEmail.setError("Email is Required")
//                    editTextPasswordResetEmail.requestFocus()
//                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
//                    Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show()
//                    editTextPasswordResetEmail.setError("Valid Email is Required")
//                    editTextPasswordResetEmail.requestFocus()
//                }else{
//                    progressBar.setVisibility(View.VISIBLE)
//                    resetPassword()
//                }
//            }
//        })
//
//    }
//
//     private fun resetPassword(String email): Any {
//         authProfile = FirebaseAuth.getInstance()
//         authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<void>(){
//
//             public void onComplete(@NonNull Task<void> task){
//                 if(task.isSuccessful()){
//                     Toast.makeText(this, "Please check yur for password reset link", Toast.LENGTH_SHORT).show()
//
//                     val intent = Intent(this, MainActivity::class.java)
//
//                     //Clear stack to prevent user coming back to ForgotPasswordActivity
//                     intent.flags =
//                         Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                     startActivity(intent)
//                     finish()        //Close UserProfileActivity
//                 }else{
//                     Toast.makeText(this, "Something Wrong!", Toast.LENGTH_SHORT).show()
//                 }
//                 progressBar.setVisibility(View.GONE)
//             }
//         })
//
//     }
// }


package com.example.mad_project

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var buttonPasswordReset: Button
    private lateinit var editTextPasswordResetEmail: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var authProfile: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        supportActionBar?.title = "Forgot Password"

        authProfile = FirebaseAuth.getInstance()

        buttonPasswordReset = findViewById(R.id.button_password_reset)
        editTextPasswordResetEmail = findViewById(R.id.editText_password_reset_email)
        progressBar = findViewById(R.id.progressBar)

        buttonPasswordReset.setOnClickListener {
            val email = editTextPasswordResetEmail.text.toString().trim()
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter your registered email", Toast.LENGTH_SHORT).show()
                editTextPasswordResetEmail.setError("Email is Required")
                editTextPasswordResetEmail.requestFocus()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show()
                editTextPasswordResetEmail.setError("Valid Email is Required")
                editTextPasswordResetEmail.requestFocus()
            } else {
                progressBar.visibility = View.VISIBLE
                resetPassword(email)
            }
        }
    }

    private fun resetPassword(email: String) {
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(this, OnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Please check your email for password reset link", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)

                //Clear stack to prevent user coming back to ForgotPasswordActivity
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish() //Close ForgotPasswordActivity
            }
            else {
                try {
                    throw task.exception!!
                } catch (e: FirebaseAuthInvalidUserException) {
                    editTextPasswordResetEmail.setError("User does not exists or is no longer valid. Please register again")
                } catch (e: Exception) {
                    Log.e(TAG, e.message!!)
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
            }

//            else {
//                try{
//                    throw task.getException()
//                }catch (FirebaseAuthInvalidUserException e){
//                    editTextPasswordResetEmail.setError("User does not exists or is no longer valid. Please register again")
//                }catch (Exception e){
//                    Log.e(TAG, e.getMessage())
//                    Toast.makeText(this, e.Message(), Toast.LENGTH_SHORT).show()
//                }



            progressBar.visibility = View.GONE
        })
    }
}




//package com.example.mad_project
//
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.text.TextUtils
//import android.util.Patterns
//import android.view.View
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ProgressBar
//import android.widget.Toast
//import com.google.android.gms.tasks.OnCompleteListener
//import com.google.firebase.auth.FirebaseAuth
//
//
//class ForgotPasswordActivity : AppCompatActivity() {
//
//    private lateinit var buttonPasswordReset: Button
//    private lateinit var editTextPasswordResetEmail: EditText
//    private lateinit var progressBar: ProgressBar
//    private lateinit var authProfile: FirebaseAuth
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_forgot_password)
//
//        supportActionBar?.title = "Forgot Password"
//
//        authProfile = FirebaseAuth.getInstance()
//
//        buttonPasswordReset.setOnClickListener {
//            val email = editTextPasswordResetEmail.text.toString().trim()
//            if (TextUtils.isEmpty(email)) {
//                Toast.makeText(this, "Please enter your registered email", Toast.LENGTH_SHORT).show()
//                editTextPasswordResetEmail.setError("Email is Required")
//                editTextPasswordResetEmail.requestFocus()
//            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show()
//                editTextPasswordResetEmail.setError("Valid Email is Required")
//                editTextPasswordResetEmail.requestFocus()
//            } else {
//                progressBar.visibility = View.VISIBLE
//                resetPassword(email)
//            }
//        }
//    }
//
//    private fun resetPassword(email: String) {
//        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(this, OnCompleteListener { task ->
//            if (task.isSuccessful) {
//                Toast.makeText(this, "Please check your email for password reset link", Toast.LENGTH_SHORT).show()
//
//                val intent = Intent(this, MainActivity::class.java)
//
//                //Clear stack to prevent user coming back to ForgotPasswordActivity
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                startActivity(intent)
//                finish() //Close ForgotPasswordActivity
//            } else {
//                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
//            }
//            progressBar.visibility = View.GONE
//        })
//    }
//}
