package com.deividasstr.revoratelut.ui.utils.delegating

import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

/*
* Extension of {@link com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter}
* which supports stable ids - supposed to be faster.
*/
class StableIdsDifferDelegationAdapter<T : ListItem>(
    adapterDelegatesManager: AdapterDelegatesManager<List<T>>
) : AsyncListDifferDelegationAdapter<T>(ListItemDiffUtil(), adapterDelegatesManager) {

    override fun getItemId(position: Int): Long {
        return items[position].id.hashCode().toLong()
    }
}