package com.android.spendingstracker

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_update_income.*

class UpdateIncomeActivity : AppCompatActivity() {
    val user = FirebaseAuth.getInstance().currentUser
    var userIncome: Int = 0
    var userFixedExpenses: Int = 0
    var userInfoId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_income)

        FirebaseFirestore.getInstance().collection("usersInfo").whereEqualTo("id", user?.uid).get().addOnSuccessListener { data -> run {
            userInfoId = data.documents[0].id

            userIncome = Integer.parseInt(data.documents[0].get("income").toString())
            incomeLayout.hint = "${updateIncome.hint} - currently $$userIncome"
            userFixedExpenses = Integer.parseInt(data.documents[0].get("fixedExpenses").toString())
            fixedExpensesLayout.hint = "${updateFixedExpenses.hint} - currently $$userFixedExpenses"
        } }
    }

    fun handleUpdate(v: View) {
        var updatedIncomeField = updateIncome.text.toString()
        var updatedFixedExpensesField = updateFixedExpenses.text.toString()
        val updatedIncome = if (updatedIncomeField == "") userIncome else Integer.parseInt(updatedIncomeField)
        val updatedFixedExpenses = if (updatedFixedExpensesField == "") userFixedExpenses else Integer.parseInt(updatedFixedExpensesField)
        FirebaseFirestore.getInstance().collection("usersInfo").document(userInfoId).update("income", updatedIncome, "fixedExpenses", updatedFixedExpenses).addOnSuccessListener { _ -> run {
            Toast.makeText(this@UpdateIncomeActivity, "Successfully updated user info!", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK, intent)
            finish()
        } }
    }
}