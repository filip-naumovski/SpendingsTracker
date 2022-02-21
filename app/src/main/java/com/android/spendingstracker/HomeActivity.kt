package com.android.spendingstracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*
import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Color.green
import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.spendingstracker.domain.Expense
import com.android.spendingstracker.domain.ItemsViewModel
import com.android.spendingstracker.utility.ExpensesAdapter
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.DateTime
import java.lang.Exception
import java.time.Instant
import kotlin.math.exp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset.UTC
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.*
import kotlin.collections.ArrayList


class HomeActivity : AppCompatActivity() {
    private val user = FirebaseAuth.getInstance().currentUser
    var filteredDateString = "2022-02-01T00:00:00Z"
    lateinit var adapter: ExpensesAdapter
    val expensesData = ArrayList<ItemsViewModel>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator() //add this
        fadeIn.duration = 250

        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateInterpolator() //and this
        fadeOut.duration = 250

        topAppBar.setNavigationOnClickListener {
            when(homeMenu.visibility) {
                View.GONE -> {
                    homeMenu.visibility = View.VISIBLE
                    homeMenu.startAnimation(fadeIn)
                }
                View.VISIBLE -> {
                    homeMenu.startAnimation(fadeOut)
                    homeMenu.visibility = View.GONE
                }
                else -> {}
            }
        }

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@HomeActivity, MainActivity::class.java))
                    true;
                }
                else -> false
            }
        }

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerview.layoutManager = LinearLayoutManager(this)

        // This will pass the ArrayList to our Adapter
        adapter = ExpensesAdapter(expensesData)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

        updateRecyclerData()

        calculateLeftoverMoney()
    }

    fun calculateDateString(year: Int, month: Int) {
        // 2022-02-20T00:00:00Z
        val properMonth = if(month>=10) {
            (month + 1).toString()
        } else {
            "0${month + 1}"
        }
        filteredDateString =  "$year-$properMonth-01T00:00:00Z"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateRecyclerData() {
        expensesData.clear()
        FirebaseFirestore.getInstance().collection("expenses").whereEqualTo("userId", user?.uid).get().addOnSuccessListener { data -> run {
            data.documents.forEach { doc -> run {
                val firstOfMonth: LocalDate = LocalDate.parse(doc.get("date").toString().split("T")[0]).withDayOfMonth(1)
                val millis = firstOfMonth.atStartOfDay(UTC).toInstant().toEpochMilli()
                val currentDate = Instant.ofEpochMilli(millis)
                if (currentDate.toString() == filteredDateString) {
                    expensesData.add(ItemsViewModel(doc.get("name").toString(), "$${
                        doc.get("value").toString()
                    }"))
                }
                calculateLeftoverMoney()
                adapter.notifyDataSetChanged()
            } }
        } }
    }

    fun calculateLeftoverMoney() {
        var income: Int
        var fixedExpenses: Int
        var expenses = 0
        FirebaseFirestore.getInstance().collection("usersInfo").whereEqualTo("id", user?.uid).get().addOnSuccessListener { data -> run {
            income = Integer.parseInt(data.documents[0].get("income").toString())
            fixedExpenses = Integer.parseInt(data.documents[0].get("fixedExpenses").toString())
            expenses = if(expensesData.size > 0) expensesData.map { data -> Integer.parseInt(data.price.split("$")[1]) }.reduce { acc, next -> acc + next  } else 0
            val leftover = income - fixedExpenses - expenses
            homeIncomeLeft.text = "Leftover money: $$leftover"
            if(leftover > 0) homeIncomeLeft.setTextColor(resources.getColor(R.color.green))
            else homeIncomeLeft.setTextColor(resources.getColor(R.color.red))
        } }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleFilter(v: View) {
        val calendar = Calendar.getInstance()
        val yearSelected = calendar[Calendar.YEAR]
        val monthSelected = calendar[Calendar.MONTH]

        val dialogFragment = MonthYearPickerDialogFragment
            .getInstance(monthSelected, yearSelected)

        dialogFragment.show(supportFragmentManager, null)

        dialogFragment.setOnDateSetListener { year, monthOfYear -> run {
            calculateDateString(year, monthOfYear)
            updateRecyclerData()
        } }
    }

    fun handleExpense(v: View) {
        startActivityForResult(Intent(this@HomeActivity, AddExpenseActivity::class.java), 1)
    }

    fun handleIncome(v: View) {
        startActivityForResult(Intent(this@HomeActivity, UpdateIncomeActivity::class.java), 1)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data==null)return;
        if (requestCode == 1){
            when (resultCode) {
                RESULT_OK -> updateRecyclerData()
                else -> {}
            }
        }
    }
}