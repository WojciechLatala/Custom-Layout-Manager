package com.example.dasada.recyclerview

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dasada.R
import com.example.dasada.model.ListItemViewModel

class ListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val itemLabel = itemView.findViewById<TextView>(R.id.item_label)

    fun setItem(listItem: ListItemViewModel) {
        itemLabel.text = listItem.itemName
    }
}
