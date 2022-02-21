package com.android.spendingstracker.utility

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.spendingstracker.R
import com.android.spendingstracker.domain.ItemsViewModel

class ExpensesAdapter(private val mList: List<ItemsViewModel>) : RecyclerView.Adapter<ExpensesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = mList[position]

        // sets the image to the imageview from our itemHolder class
        holder.textView.text = item.name.toString()

        // sets the text to the textview from our itemHolder class
        holder.costView.text = item.price

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textView: TextView = itemView.findViewById(R.id.textView)
        val costView: TextView = itemView.findViewById(R.id.cost)
    }
}