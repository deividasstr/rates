package com.deividasstr.revoratelut.ui.ratelist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.deividasstr.revoratelut.R
import com.deividasstr.revoratelut.databinding.ActivityRateListBinding
import com.deividasstr.revoratelut.databinding.HintDefaultBinding
import com.deividasstr.revoratelut.databinding.ItemRateListBinding
import com.deividasstr.revoratelut.ui.ratelist.listitems.CurrencyRateModel
import com.deividasstr.revoratelut.ui.ratelist.listitems.CurrencyRatesListHint
import com.deividasstr.revoratelut.ui.utils.delegating.ListItem
import com.deividasstr.revoratelut.ui.utils.delegating.LoaderModel
import com.deividasstr.revoratelut.ui.utils.delegating.StableIdsDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegate
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.RoundingMode
import java.text.NumberFormat

class RateListActivity : AppCompatActivity() {

    private val viewModel: CurrencyRatesViewModel by viewModel()
    private lateinit var binding: ActivityRateListBinding

    private val numberFormatter = NumberFormat.getNumberInstance().apply {
        roundingMode = RoundingMode.HALF_DOWN
        maximumIntegerDigits = 7
        maximumFractionDigits = 2
    }

    private val currencyAdapter by lazy {
        StableIdsDifferDelegationAdapter(
            AdapterDelegatesManager(
                loadingDelegate(),
                hintDelegate(),
                currencyRatesModelDelegate()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRateListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRecyclerView()
        scrollUpOnHint()
        viewModel.currencyRatesLive().observe(this, ::renderState)
    }

    private fun scrollUpOnHint() {
        currencyAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                if (positionStart == 0) binding.ratesList.smoothScrollToPosition(0)
            }
        })
    }

    private fun setRecyclerView() {
        with(binding.ratesList) {
            setHasFixedSize(true)
            adapter = currencyAdapter
        }
    }

    private fun renderState(currencyRatesState: CurrencyRatesState) {
        val itemsToShow = when (currencyRatesState) {
            is CurrencyRatesState.Loading -> listOf(LoaderModel)
            is CurrencyRatesState.Loaded -> loadedStateToItems(currencyRatesState)
            else -> throw IllegalStateException("Unknown currencyRatesState $currencyRatesState")
        }
        currencyAdapter.items = itemsToShow
    }

    private fun loadedStateToItems(
        currencyRatesState: CurrencyRatesState.Loaded
    ): List<ListItem> {
        return listOfNotNull(currencyRatesState.hint).plus(currencyRatesState.rates)
    }

    private fun currencyRatesModelDelegate() =
        adapterDelegate<CurrencyRateModel, ListItem>(R.layout.item_rate_list) {
            val binder = ItemRateListBinding.bind(itemView)

            bind { payload ->
                when {
                    // First render of item
                    payload.isEmpty() -> with(binder) {
                        currencyName.text = item.currencyName
                        currencyCode.text = item.currency.currencyCode
                        countryImage.setImageResource(item.currencyCountryPic)
                        currencyUnits.setText(numberFormatter.format(item.rate))
                    }
                    // Change of item - only rate changes
                    else -> binder.currencyUnits.setText(numberFormatter.format(item.rate))
                }
            }
        }

    private fun hintDelegate() =
        adapterDelegate<CurrencyRatesListHint, ListItem>(R.layout.hint_default) {
            val binder = HintDefaultBinding.bind(itemView)
            bind {
                with(binder) {
                    title.text = item.firstText.getString(resources)
                    body.text = item.secondText.getString(resources)
                    icon.setImageResource(item.icon)
                }
            }
        }

    private fun loadingDelegate() =
        adapterDelegate<LoaderModel, ListItem>(R.layout.loader_delegate) {}
}