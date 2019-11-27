import com.deividasstr.revoratelut.R
import com.deividasstr.revoratelut.data.network.CurrencyRatesResponse
import com.deividasstr.revoratelut.domain.Currency
import com.deividasstr.revoratelut.domain.CurrencyWithRate
import com.deividasstr.revoratelut.ui.ratelist.listitems.CurrencyRateModel
import com.deividasstr.revoratelut.ui.ratelist.CurrencyRatesState
import com.deividasstr.revoratelut.ui.utils.toArgedText

object TestData {

    val eur = "EUR"
    val usd = "USD"
    val gbp = "GBP"

    val eurCurrency = Currency(eur)
    val usdCurrency = Currency(usd)
    val gbpCurrency = Currency(gbp)

    const val eurRate: Double = 1.0
    const val usdRate: Double = 1.23
    const val usdRate2: Double = 1.2124
    const val gbpRate: Double = 0.89
    const val gbpRate2: Double = 0.88795

    val eurWithRate = CurrencyWithRate(eurCurrency, eurRate.toBigDecimal())
    val usdWithRate = CurrencyWithRate(usdCurrency, usdRate.toBigDecimal())
    val gbpWithRate = CurrencyWithRate(gbpCurrency, gbpRate.toBigDecimal())

    val currenciesToRates = mapOf(
        eur to eurRate,
        usd to usdRate,
        gbp to gbpRate
    )

    val currenciesToRates2 = mapOf(
        eur to eurRate,
        usd to usdRate2,
        gbp to gbpRate2
    )

    val response = CurrencyRatesResponse("", "", currenciesToRates)
    val response2 = CurrencyRatesResponse("", "", currenciesToRates2)

    val rates = listOf(
        eurWithRate,
        usdWithRate,
        gbpWithRate
    )

    val rates2 = listOf(
        CurrencyWithRate(eurCurrency, eurRate.toBigDecimal()),
        CurrencyWithRate(usdCurrency, usdRate2.toBigDecimal()),
        CurrencyWithRate(gbpCurrency, gbpRate2.toBigDecimal())
    )

    private val responseRatesMap = mapOf(
        gbp to gbpRate,
        usd to usdRate
    )

    val currencyRatesResponse = CurrencyRatesResponse(
        "EUR",
        "2018-09-06",
        responseRatesMap
    )

    // Fragile test data - currency lib specific
    val eurCurrencyRateModel =
        CurrencyRateModel(
            eurCurrency,
            eurRate.toBigDecimal(),
            "Euro",
            R.drawable.flag_eur
        )

    val gbpCurrencyRateModel =
        CurrencyRateModel(
            gbpCurrency,
            gbpRate.toBigDecimal(),
            "British Pound",
            R.drawable.flag_gbp
        )

    val usdCurrencyRateModel =
        CurrencyRateModel(
            usdCurrency,
            usdRate.toBigDecimal(),
            "United States Dollar",
            R.drawable.flag_usd)

    val currencyRatesModel = listOf(
        eurCurrencyRateModel,
        usdCurrencyRateModel,
        gbpCurrencyRateModel
    )

    val currencyRatesAvailableFresh = CurrencyRatesState.Available(currencyRatesModel, false)

    val currencyRatesAvailableStale = CurrencyRatesState.Available(
        currencyRatesModel,
        true,
        R.string.issue_network.toArgedText()
    )
}