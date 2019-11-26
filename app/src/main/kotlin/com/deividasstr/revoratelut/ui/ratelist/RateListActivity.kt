package com.deividasstr.revoratelut.ui.ratelist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.deividasstr.revoratelut.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class RateListActivity : AppCompatActivity() {

    private val viewModel : CurrencyRatesViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate_list)

    }
}
