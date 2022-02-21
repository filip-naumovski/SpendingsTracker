package com.android.spendingstracker.domain

class Expense {
    lateinit var userId: String
    var value: Int = 0
    lateinit var name: String
    lateinit var date: String
    constructor() {}
    constructor(userId: String, value: Int, name: String, date: String) {
        this.userId = userId
        this.value = value
        this.name = name
        this.date = date
    }

    override fun toString(): String {
        return "Expense(userId='$userId', value=$value, name='$name', date='$date')"
    }


}
