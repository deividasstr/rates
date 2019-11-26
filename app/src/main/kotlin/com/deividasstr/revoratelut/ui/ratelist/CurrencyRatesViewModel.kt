package com.deividasstr.revoratelut.ui.ratelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.deividasstr.revoratelut.R
import com.deividasstr.revoratelut.data.repository.CurrencyRatesRepo
import com.deividasstr.revoratelut.data.repository.CurrencyRatesResult
import com.deividasstr.revoratelut.data.repository.RemoteFailure
import com.deividasstr.revoratelut.domain.CurrencyWithRate
import com.deividasstr.revoratelut.ui.utils.currency.CurrencyHelper
import com.deividasstr.revoratelut.ui.utils.toArgedText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class CurrencyRatesViewModel(
    private val currencyRatesRepo: CurrencyRatesRepo,
    private val currencyHelper: CurrencyHelper
) : ViewModel() {

    fun currencyRatesLive(): LiveData<CurrencyRatesState> = currencyRatesRepo
        .currencyRatesResultFlow()
        .distinctUntilChanged()
        .map(this::resultToState)
        .asLiveData(Dispatchers.IO)

    private suspend fun resultToState(currencyRatesResult: CurrencyRatesResult): CurrencyRatesState {
        return when (currencyRatesResult) {
            is CurrencyRatesResult.NoResults -> {
                val error = currencyRatesResult.networkFailure.toErrorRes()
                CurrencyRatesState.Unavailable(error.toArgedText())
            }

            is CurrencyRatesResult.StaleResult -> with(currencyRatesResult) {
                val error = networkFailure.toErrorRes()
                CurrencyRatesState.Available(
                    currencyRates.toModel(),
                    true,
                    error.toArgedText())
            }

            is CurrencyRatesResult.FreshResult -> CurrencyRatesState.Available(
                currencyRatesResult.currencyRates.toModel(),
                false
            )
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
            rate,
            currencyDetails.currencyName,
            currencyDetails.currencyFlag
        )
    }
}
