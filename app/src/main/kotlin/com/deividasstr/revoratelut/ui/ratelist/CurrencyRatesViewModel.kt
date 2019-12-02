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
import com.deividasstr.revoratelut.domain.Calculator
import com.deividasstr.revoratelut.domain.Currency
import com.deividasstr.revoratelut.domain.CurrencyWithRate
import com.deividasstr.revoratelut.domain.NumberFormatter
import com.deividasstr.revoratelut.ui.ratelist.listitems.CurrencyRateModel
import com.deividasstr.revoratelut.ui.ratelist.listitems.CurrencyRatesListHint
import com.deividasstr.revoratelut.ui.utils.currency.CurrencyHelper
import com.deividasstr.revoratelut.ui.utils.toArgedText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicReference

@ExperimentalCoroutinesApi
class CurrencyRatesViewModel(
    private val currencyRatesRepo: CurrencyRatesRepo,
    private val currencyHelper: CurrencyHelper,
    private val sharedPrefs: SharedPrefs,
    private val numberFormatter: NumberFormatter,
    private val calculator: Calculator
) : ViewModel() {

    private val selectedCurrencyChannel by lazy {
        ConflatedBroadcastChannel(sharedPrefs.baseCurrency())
    }

    private val selectedCurrencyValueTicklerChannel by lazy {
        ConflatedBroadcastChannel(Unit)
    }

    private val selectedCurrencyValue by lazy {
        AtomicReference<BigDecimal>(sharedPrefs.baseCurrencyValue())
    }

    fun currencyRatesLive(): LiveData<CurrencyRatesState> {
        val selectedCurrencyTicklerFlow = selectedCurrencyValueTicklerChannel.asFlow()
        return selectedCurrencyChannel.asFlow()
            .flatMapLatest { currency ->
                currencyRatesRepo.currencyRatesResultFlow(currency)
                    .map { it to currency }
            }
            .distinctUntilChanged()
            .map(::moveBaseCurrencyTop)
            .combine(selectedCurrencyTicklerFlow, ::recalculateRates)
            .map(::generateState)
            .onStart { emit(CurrencyRatesState.Loading) }
            .asLiveData(Dispatchers.IO)
    }

    private suspend fun moveBaseCurrencyTop(ratesToCurrency: Pair<CurrencyRatesResult, Currency>
    ): Pair<CurrencyRatesResult, Currency> {
        val result = ratesToCurrency.first
        if (result.currencyRates.isNullOrEmpty()) return ratesToCurrency

        val selectedCurrency = ratesToCurrency.second
        val currencies = result.currencyRates!!.toMutableList()
        val baseCurrencyIndex = currencies.indexOfFirst { it.currency == selectedCurrency }
        val baseCurrencyWithRate = currencies.removeAt(baseCurrencyIndex)
        currencies.add(0, baseCurrencyWithRate)

        return result.rewrapNewRates(currencies) to selectedCurrency
    }

    private suspend fun recalculateRates(
        resultToCurrency: Pair<CurrencyRatesResult, Currency>,
        tickler: Unit
    ): CurrencyRatesResult {
        if (resultToCurrency.first.currencyRates.isNullOrEmpty()) return resultToCurrency.first

        val currencyRates = resultToCurrency.first.currencyRates!!
        val currency = resultToCurrency.second
        val baseCurrencyRate = getBaseCurrencyRate(currencyRates, currency)
        val multiplier = selectedCurrencyValue.get()!!

        val adjustedRates = currencyRates.adjustRates(baseCurrencyRate, multiplier)

        return resultToCurrency.first.rewrapNewRates(adjustedRates)
    }

    private fun List<CurrencyWithRate>.adjustRates(
        baseCurrencyRate: BigDecimal,
        multiplier: BigDecimal
    ): List<CurrencyWithRate> {
        return map {
            val adjustedRate = calculator.divide(it.rate, baseCurrencyRate)
            val multipliedRate = calculator.multiply(adjustedRate, multiplier)
            it.copy(rate = multipliedRate)
        }
    }

    private suspend fun generateState(currencyRatesResult: CurrencyRatesResult): CurrencyRatesState {
        val error = errorFromResult(currencyRatesResult)?.toErrorRes()?.toArgedText()
        val consequence = consequenceFromResult(currencyRatesResult)?.toArgedText()
        val hint = if (error != null && consequence != null) {
            CurrencyRatesListHint(
                error,
                consequence,
                R.drawable.ic_error_outline_white_48dp)
        } else null

        val currencyRates = currencyRatesResult.currencyRates?.toModel()

        return CurrencyRatesState.Loaded(currencyRates, hint)
    }

    private fun errorFromResult(currencyRatesResult: CurrencyRatesResult): RemoteFailure? {
        return when (currencyRatesResult) {
            is CurrencyRatesResult.NoResults -> currencyRatesResult.networkFailure
            is CurrencyRatesResult.StaleResult -> currencyRatesResult.networkFailure
            else -> null
        }
    }

    private fun consequenceFromResult(currencyRatesResult: CurrencyRatesResult): Int? {
        return when (currencyRatesResult) {
            is CurrencyRatesResult.NoResults -> R.string.no_currency_rates
            is CurrencyRatesResult.StaleResult -> R.string.stale_currency_rates
            else -> null
        }
    }

    private fun getBaseCurrencyRate(
        currenciesWithRates: List<CurrencyWithRate>,
        baseCurrency: Currency
    ): BigDecimal {
        return currenciesWithRates.find { it.currency == baseCurrency }!!.rate
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

    fun changeBaseCurrency(
        currency: Currency,
        rate: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val parsedRate = numberFormatter.parseOrZero(rate)
            sharedPrefs.setBaseCurrencyValue(parsedRate)
            sharedPrefs.setBaseCurrency(currency)

            selectedCurrencyValue.set(parsedRate)
            selectedCurrencyChannel.send(currency)
        }
    }

    fun changeBaseCurrencyRate(newBaseRate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val parsedRate = numberFormatter.parseOrZero(newBaseRate)
            sharedPrefs.setBaseCurrencyValue(parsedRate)
            selectedCurrencyValue.set(parsedRate)
            selectedCurrencyValueTicklerChannel.send(Unit)
        }
    }
}
