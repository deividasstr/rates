package com.deividasstr.revoratelut.ui.ratelist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.SimpleItemAnimator
import com.deividasstr.revoratelut.R
import com.deividasstr.revoratelut.databinding.ActivityRateListBinding
import com.deividasstr.revoratelut.databinding.RateListItemBinding
import com.deividasstr.revoratelut.ui.ratelist.listitems.CurrencyRateModel
import com.deividasstr.revoratelut.ui.ratelist.listitems.CurrencyRatesListDiffUtil
import com.deividasstr.revoratelut.ui.ratelist.listitems.CurrencyRatesListItem
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
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
        AsyncListDifferDelegationAdapter(
            CurrencyRatesListDiffUtil(),
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

        // To prevent item flickering on refresh
        with(binding.ratesList) {
            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
            setHasFixedSize(true)
            adapter = currencyAdapter
            setItemViewCacheSize(20);
        }

        viewModel.currencyRatesLive().observe(this, ::renderState)
    }

    private fun renderState(currencyRatesState: CurrencyRatesState) {
        if (currencyRatesState is CurrencyRatesState.Available) {
            currencyAdapter.items = currencyRatesState.rates
        }
    }

    private fun currencyRatesModelDelegate() =
        adapterDelegate<CurrencyRateModel, CurrencyRatesListItem>(R.layout.rate_list_item) {
            val binder = RateListItemBinding.bind(itemView)

            bind {
                with(binder) {
                    currencyName.text = item.currencyName
                    currencyCode.text = item.currency.currencyCode
                    countryImage.setImageResource(item.currencyCountryPic)
                    currencyUnits.setText(numberFormatter.format(item.rate))
                }
            }
        }
}