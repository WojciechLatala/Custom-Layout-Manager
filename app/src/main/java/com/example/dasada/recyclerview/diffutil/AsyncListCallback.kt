package com.example.dasada.recyclerview.diffutil

import androidx.recyclerview.widget.DiffUtil

abstract class AsyncListCallback<T : DiffUtilListItem> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem.isTheSameItemAs(newItem)
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem.hasSameContentAs(newItem)
}
