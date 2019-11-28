package com.deividasstr.revoratelut.ui.ratelist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.SimpleItemAnimator
import com.deividasstr.revoratelut.R
import com.deividasstr.revoratelut.databinding.ActivityRateListBinding
import com.deividasstr.revoratelut.databinding.RateListItemBinding
import com.deividasstr.revoratelut.ui.ratelist.listitems.CurrencyRateModel
import com.deividasstr.revoratelut.ui.utils.delegating.ListItem
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
                //loadingDelegate(),
                currencyRatesModelDelegate()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRateListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRecyclerView()
        viewModel.currencyRatesLive().observe(this, ::renderState)
    }

    private fun setRecyclerView() {
        with(binding.ratesList) {
            // To prevent item flickering on refresh
            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
            setHasFixedSize(true)
            adapter = currencyAdapter
        }
    }

    private fun renderState(currencyRatesState: CurrencyRatesState) {
        if (currencyRatesState is CurrencyRatesState.Available) {
            currencyAdapter.items = currencyRatesState.rates
        }
    }

    private fun currencyRatesModelDelegate() =
        adapterDelegate<CurrencyRateModel, ListItem>(R.layout.rate_list_item) {
            val binder = RateListItemBinding.bind(itemView)

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
}