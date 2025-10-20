package com.kr.cloudnotes

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.kr.cloudnotes.R


class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()

        val loadingAnim = findViewById<LottieAnimationView>(R.id.lottieLoading)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            loadingAnim.visibility = View.VISIBLE
            loadingAnim.playAnimation()


            auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnSuccessListener {
                    loadingAnim.pauseAnimation()
                    loadingAnim.visibility = View.GONE

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }.addOnFailureListener {
                    loadingAnim.pauseAnimation()
                    loadingAnim.visibility = View.GONE

                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
        }
    }
}
