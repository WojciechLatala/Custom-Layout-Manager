package com.example.dasada.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.dasada.R
import com.example.dasada.model.ListItemViewModel
import com.example.dasada.recyclerview.diffutil.AsyncListCallback

class ListItemAdapter : RecyclerView.Adapter<ListItemViewHolder>() {

    private val asyncListCallback = object : AsyncListCallback<ListItemViewModel>() {}
    private val asyncListDiffer: AsyncListDiffer<ListItemViewModel> = AsyncListDiffer(this, asyncListCallback)

    fun setItems(list: List<ListItemViewModel>, commitCallback: () -> Unit) {
        asyncListDiffer.submitList(list, commitCallback)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val textView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ListItemViewHolder(textView)
    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val item = asyncListDiffer.currentList[position]
        holder.setItem(item)
    }
}
