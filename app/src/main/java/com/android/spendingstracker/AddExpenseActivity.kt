package com.android.spendingstracker

import android.app.Activity
import android.databinding.tool.util.StringUtils
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_expense.*
import java.time.Instant
import android.widget.DatePicker

import android.app.DatePickerDialog
import android.util.Log
import java.lang.Exception


class AddExpenseActivity : AppCompatActivity() {
    var inputExpenseDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)
        }

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleDate(v: View) {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        datePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
        datePicker.addOnPositiveButtonClickListener { data ->
            run {
                inputExpenseDate = Instant.ofEpochMilli(data).toString()
            }
        }
    }
    private fun createDialogWithoutDateField(): DatePickerDialog? {
        val dpd = DatePickerDialog(this, null, 2014, 1, 24)
        try {
            val datePickerDialogFields = dpd.javaClass.declaredFields
            for (datePickerDialogField in datePickerDialogFields) {
                if (datePickerDialogField.name == "mDatePicker") {
                    datePickerDialogField.isAccessible = true
                    val datePicker = datePickerDialogField[dpd] as DatePicker
                    val datePickerFields = datePickerDialogField.type.declaredFields
                    for (datePickerField in datePickerFields) {
                        if ("mDaySpinner" == datePickerField.name) {
                            datePickerField.isAccessible = true
                            val dayPicker = datePickerField[datePicker]
                            (dayPicker as View).visibility = View.GONE
                        }
                    }
                }
            }
        } catch (ex: Exception) {
        }
        return dpd
    }

    fun handleSubmit(v: View) {
        val user = FirebaseAuth.getInstance().currentUser
        val name = expenseName.text.toString()
        val value = if(StringUtils.isNotBlank(expenseValue.text.toString())) Integer.parseInt(expenseValue.text.toString()) else 0
        if(name.isEmpty() || inputExpenseDate == "") {
            Toast.makeText(this@AddExpenseActivity, "Please fill out all the fields!", Toast.LENGTH_SHORT).show()
            return;
        }
        if(user != null) {
            FirebaseFirestore.getInstance().collection("expenses").add(
                object {
                    val userId = user.uid
                    val value = value
                    val name = name
                    val date = inputExpenseDate
                }
            ).addOnSuccessListener {
                Toast.makeText(this@AddExpenseActivity, "Successfully added expense!", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }
}