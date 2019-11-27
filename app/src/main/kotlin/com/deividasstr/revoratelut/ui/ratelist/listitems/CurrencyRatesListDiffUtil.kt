package com.deividasstr.revoratelut.ui.ratelist.listitems

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class CurrencyRatesListDiffUtil<T : CurrencyRatesListItem> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}