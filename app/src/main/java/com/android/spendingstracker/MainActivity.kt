package com.android.spendingstracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun handleAuth(v: View) {
        val email = email.text.toString()
        val password = password.text.toString()
        if(email == "" || password == "") {
            Toast.makeText(this@MainActivity, "Please enter both your email and password!", Toast.LENGTH_SHORT).show()
            return
        }
        if((v as Button).text == "Log in") {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnSuccessListener { run {
                FirebaseFirestore.getInstance().collection("usersInfo").whereEqualTo("id", FirebaseAuth.getInstance().currentUser?.uid).get().addOnSuccessListener { data ->
                    run {
                        if (data.documents.isNotEmpty()) {
                            Toast.makeText(this@MainActivity, "Successfully logged in!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                        } else {
                            startActivity(Intent(this@MainActivity, IntroActivity::class.java))
                        }
                    }
                }
            } }
        } else {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnSuccessListener { run {
                startActivity(Intent(this@MainActivity, IntroActivity::class.java))
            }}
        }
    }
}