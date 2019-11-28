package com.deividasstr.revoratelut.ui.utils.delegating

interface ListItem {

    val id: String

    fun <T : ListItem> calculatePayload(oldItem: T): Any? = null
}