import com.deividasstr.revoratelut.R
import com.deividasstr.revoratelut.data.network.CurrencyRatesResponse
import com.deividasstr.revoratelut.domain.Calculator
import com.deividasstr.revoratelut.domain.Currency
import com.deividasstr.revoratelut.domain.CurrencyWithRate
import com.deividasstr.revoratelut.domain.NumberFormatter
import com.deividasstr.revoratelut.ui.ratelist.CurrencyRatesState
import com.deividasstr.revoratelut.ui.ratelist.listitems.CurrencyRateModel
import com.deividasstr.revoratelut.ui.ratelist.listitems.CurrencyRatesListHint
import com.deividasstr.revoratelut.ui.utils.toArgedText

object TestData {

    private val formatter = NumberFormatter()
    private val calculator = Calculator()

    val eur = "EUR"
    val usd = "USD"
    val gbp = "GBP"

    val eurCurrency = Currency(eur)
    val usdCurrency = Currency(usd)
    val gbpCurrency = Currency(gbp)

    const val eurRate = "1.0"
    const val usdRate = "1.23125"
    const val gbpRate = "0.89245"
    val eurRate2 = (eurRate.toDouble() / gbpRate.toDouble()).toString()
    val usdRate2 = (usdRate.toDouble() / gbpRate.toDouble()).toString()
    const val gbpRate2= eurRate

    const val currencyInputValue = "10"

    val eurWithRate = CurrencyWithRate(eurCurrency, eurRate.toBigDecimal())
    val usdWithRate = CurrencyWithRate(usdCurrency, usdRate.toBigDecimal())
    val gbpWithRate = CurrencyWithRate(gbpCurrency, gbpRate.toBigDecimal())

    val eurWithRate2 = CurrencyWithRate(eurCurrency, eurRate2.toBigDecimal())
    val usdWithRate2 = CurrencyWithRate(usdCurrency, usdRate2.toBigDecimal())
    val gbpWithRate2 = CurrencyWithRate(gbpCurrency, gbpRate2.toBigDecimal())

    val currenciesToRates = mapOf(
        eur to eurRate,
        usd to usdRate,
        gbp to gbpRate
    )

    val currenciesToRates2 = mapOf(
        eur to eurRate2,
        usd to usdRate2,
        gbp to gbpRate2
    )

    val response = CurrencyRatesResponse("", "", currenciesToRates)
    val response2 = CurrencyRatesResponse("", "", currenciesToRates2)

    val ratesEurBase = listOf(
        eurWithRate,
        usdWithRate,
        gbpWithRate
    )

    val ratesGbpBase = listOf(
        eurWithRate2,
        usdWithRate2,
        gbpWithRate2
    )

    val ratesWOBaseEur = listOf(
        usdWithRate,
        gbpWithRate
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
    fun eurCurrencyRateModel(timesString: String = "1") =
        CurrencyRateModel(
            eurCurrency,
            formatter.format(formatter.parseOrZero(timesString) * eurRate.toBigDecimal()),
            "Euro",
            R.drawable.flag_eur
        )

    fun gbpCurrencyRateModel(timesString: String = "1") =
        CurrencyRateModel(
            gbpCurrency,
            formatter.format(formatter.parseOrZero(timesString) * gbpRate.toBigDecimal()),
            "British Pound",
            R.drawable.flag_gbp
        )

    fun usdCurrencyRateModel(timesString: String = "1") =
        CurrencyRateModel(
            usdCurrency,
            formatter.format(formatter.parseOrZero(timesString) * usdRate.toBigDecimal()),
            "United States Dollar",
            R.drawable.flag_usd)

    private val modifiedEurRateValue = calculator.multiply(
        eurRate2.toBigDecimal(),
        currencyInputValue.toBigDecimal()
    )

    val eurCurrencyRateModel2 =
        CurrencyRateModel(
            eurCurrency,
            formatter.format(modifiedEurRateValue),
            "Euro",
            R.drawable.flag_eur
        )

    private val modifiedGbpRateValue = calculator.multiply(
        gbpRate2.toBigDecimal(),
        currencyInputValue.toBigDecimal()
    )

    val gbpCurrencyRateModel2 =
        CurrencyRateModel(
            gbpCurrency,
            formatter.format(modifiedGbpRateValue),
            "British Pound",
            R.drawable.flag_gbp
        )

    private val modifiedUsdRateValue = calculator.multiply(
        usdRate2.toBigDecimal(),
        currencyInputValue.toBigDecimal()
    )

    val usdCurrencyRateModel2 =
        CurrencyRateModel(
            usdCurrency,
            formatter.format(modifiedUsdRateValue),
            "United States Dollar",
            R.drawable.flag_usd)

    val currencyRatesModel = listOf(
        eurCurrencyRateModel(),
        usdCurrencyRateModel(),
        gbpCurrencyRateModel()
    )

    fun currencyRatesModelAdjusted(timesString: String) = listOf(
        eurCurrencyRateModel(timesString),
        usdCurrencyRateModel(timesString),
        gbpCurrencyRateModel(timesString)
    )

    val currencyRatesModel2 = listOf(
        gbpCurrencyRateModel2,
        eurCurrencyRateModel2,
        usdCurrencyRateModel2
    )

    val currencyRatesAvailableFresh = CurrencyRatesState.Loaded(currencyRatesModel)

    fun currencyRatesAvailableFreshAdjusted(timesString: String) =
        CurrencyRatesState.Loaded(currencyRatesModelAdjusted(timesString))

    val currencyRatesAvailableFresh2 = CurrencyRatesState.Loaded(currencyRatesModel2)

    val currencyRatesAvailableStaleNetworkIssue = CurrencyRatesState.Loaded(
        currencyRatesModel,
        CurrencyRatesListHint(
            R.string.issue_network.toArgedText(),
            R.string.stale_currency_rates.toArgedText(),
            R.drawable.ic_error_outline_white_48dp)
    )

    val currencyRatesAvailableStaleNetworkIssue2 = CurrencyRatesState.Loaded(
        currencyRatesModel2,
        CurrencyRatesListHint(
            R.string.issue_network.toArgedText(),
            R.string.stale_currency_rates.toArgedText(),
            R.drawable.ic_error_outline_white_48dp)
    )

    fun currencyRatesAvailableStaleNetworkIssueAdjusted(timesString: String) =
        CurrencyRatesState.Loaded(
            currencyRatesModelAdjusted(timesString),
            CurrencyRatesListHint(
                R.string.issue_network.toArgedText(),
                R.string.stale_currency_rates.toArgedText(),
                R.drawable.ic_error_outline_white_48dp)
        )

    val currencyRatesNotAvailableGenericIssue = CurrencyRatesState.Loaded(
        hint =
        CurrencyRatesListHint(
            R.string.issue_generic.toArgedText(),
            R.string.no_currency_rates.toArgedText(),
            R.drawable.ic_error_outline_white_48dp)
    )

    val currencyRatesNotAvailableNetworkIssue = CurrencyRatesState.Loaded(
        hint =
        CurrencyRatesListHint(
            R.string.issue_network.toArgedText(),
            R.string.no_currency_rates.toArgedText(),
            R.drawable.ic_error_outline_white_48dp)
    )
}