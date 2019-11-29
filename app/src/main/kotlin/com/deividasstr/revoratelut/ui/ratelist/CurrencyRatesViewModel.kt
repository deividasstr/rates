package com.deividasstr.revoratelut.ui.ratelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.deividasstr.revoratelut.R
import com.deividasstr.revoratelut.data.repository.CurrencyRatesRepo
import com.deividasstr.revoratelut.data.repository.CurrencyRatesResult
import com.deividasstr.revoratelut.data.repository.RemoteFailure
import com.deividasstr.revoratelut.data.storage.sharedprefs.SharedPrefs
import com.deividasstr.revoratelut.domain.Currency
import com.deividasstr.revoratelut.domain.CurrencyWithRate
import com.deividasstr.revoratelut.domain.NumberFormatter
import com.deividasstr.revoratelut.ui.ratelist.listitems.CurrencyRateModel
import com.deividasstr.revoratelut.ui.ratelist.listitems.CurrencyRatesListHint
import com.deividasstr.revoratelut.ui.utils.currency.CurrencyHelper
import com.deividasstr.revoratelut.ui.utils.toArgedText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class CurrencyRatesViewModel(
    private val currencyRatesRepo: CurrencyRatesRepo,
    private val currencyHelper: CurrencyHelper,
    private val sharedPrefs: SharedPrefs,
    private val numberFormatter: NumberFormatter
) : ViewModel() {

    private val baseCurrencyChannel by lazy {
        ConflatedBroadcastChannel(sharedPrefs.getLatestBaseCurrency())
    }

    fun currencyRatesLive(): LiveData<CurrencyRatesState> =
        baseCurrencyChannel.asFlow()
            .flatMapLatest { currencyRatesRepo.currencyRatesResultFlow(it) }
            .distinctUntilChanged()
            .map(::resultToState)
            .onStart { emit(CurrencyRatesState.Loading) }
            .asLiveData(Dispatchers.IO)

    private suspend fun resultToState(currencyRatesResult: CurrencyRatesResult): CurrencyRatesState {
        return when (currencyRatesResult) {
            is CurrencyRatesResult.NoResults -> {
                val error = currencyRatesResult.networkFailure.toErrorRes().toArgedText()
                val consequence = R.string.no_currency_rates.toArgedText()
                CurrencyRatesState.Loaded(
                    hint = CurrencyRatesListHint(
                        error,
                        consequence,
                        R.drawable.ic_error_outline_white_48dp)
                )
            }

            is CurrencyRatesResult.StaleResult -> with(currencyRatesResult) {
                val error = networkFailure.toErrorRes().toArgedText()
                val consequence = R.string.stale_currency_rates.toArgedText()

                CurrencyRatesState.Loaded(
                    currencyRates.toModel(),
                    CurrencyRatesListHint(
                        error,
                        consequence,
                        R.drawable.ic_error_outline_white_48dp
                    )
                )
            }

            is CurrencyRatesResult.FreshResult -> CurrencyRatesState.Loaded(
                currencyRatesResult.currencyRates.toModel())

            is CurrencyRatesResult.InitialResult -> CurrencyRatesState.Loaded(
                currencyRatesResult.currencyRates.toModel())
        }
    }

    private fun RemoteFailure.toErrorRes(): Int {
        return when (this) {
            RemoteFailure.NetworkFailure -> R.string.issue_network
            RemoteFailure.GenericFailure -> R.string.issue_generic
        }
    }

    private fun List<CurrencyWithRate>.toModel(): List<CurrencyRateModel> {
        return map { it.toModel() }
    }

    private fun CurrencyWithRate.toModel(): CurrencyRateModel {
        val currencyDetails = currencyHelper.getCurrencyDetails(currency.currencyCode)
        return CurrencyRateModel(
            currency,
            numberFormatter.format(rate),
            currencyDetails.currencyName,
            currencyDetails.currencyFlag
        )
    }

    fun focusedCurrency(currency: Currency) {
        sharedPrefs.setLatestBaseCurrency(currency)
        viewModelScope.launch(Dispatchers.IO) { baseCurrencyChannel.send(currency) }
    }

    override fun onCleared() {
        super.onCleared()
        baseCurrencyChannel.close()
    }
}