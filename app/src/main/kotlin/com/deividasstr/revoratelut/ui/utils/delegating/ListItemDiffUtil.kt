package com.deividasstr.revoratelut.ui.utils.delegating

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class ListItemDiffUtil<T : ListItem> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: T, newItem: T): Any? {
        return newItem.calculatePayload(oldItem)
    }
}