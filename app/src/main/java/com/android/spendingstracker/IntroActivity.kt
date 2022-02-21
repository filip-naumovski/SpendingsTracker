package com.android.spendingstracker

import android.content.Intent
import android.databinding.tool.util.StringUtils
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
    }

    fun handleIntroSubmit(v: View) {
        val user = FirebaseAuth.getInstance().currentUser
        val income = introIncome.text.toString()
        val fixedExpenses = introExpenses.text.toString()
        if(!StringUtils.isNotBlank(income) || !StringUtils.isNotBlank(fixedExpenses)) {
            return;
        }
        if (user != null) {
            FirebaseFirestore.getInstance().collection("usersInfo").add(
                object {
                    val id = user.uid
                    val income = income
                    val fixedExpenses = fixedExpenses
                }
            ).addOnSuccessListener { run {
                Toast.makeText(this@IntroActivity, "Successfully set up income!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@IntroActivity, HomeActivity::class.java))
                finish()
            } }
        }

    }
}