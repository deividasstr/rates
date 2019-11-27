package com.deividasstr.revoratelut.di

import android.content.Context
import androidx.room.Room
import com.deividasstr.revoratelut.BuildConfig
import com.deividasstr.revoratelut.data.network.CurrencyRateNetworkSource
import com.deividasstr.revoratelut.data.network.CurrencyRateNetworkSourceImpl
import com.deividasstr.revoratelut.data.network.CurrencyRatesClient
import com.deividasstr.revoratelut.data.repository.CurrencyRatesRepo
import com.deividasstr.revoratelut.data.repository.CurrencyRatesRepoImpl
import com.deividasstr.revoratelut.data.storage.CurrencyRatesStorage
import com.deividasstr.revoratelut.data.storage.CurrencyRatesStorageImpl
import com.deividasstr.revoratelut.data.storage.db.AppDb
import com.deividasstr.revoratelut.data.storage.db.CurrencyRateDao
import com.deividasstr.revoratelut.data.storage.sharedprefs.SharedPrefs
import com.deividasstr.revoratelut.data.storage.sharedprefs.SharedPrefsImpl
import com.deividasstr.revoratelut.ui.ratelist.CurrencyRatesViewModel
import com.deividasstr.revoratelut.ui.utils.currency.CurrencyHelper
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object KoinModules {

    fun get(baseUrl: String): List<Module> {
        val networkModule = module {
            factory { okHttp() }
            single(named("baseUrl")) { baseUrl }
            factory { retrofit(get(), get(named("baseUrl"))) }
            single { currencyRatesClient(get()) }
            single<CurrencyRateNetworkSource> { CurrencyRateNetworkSourceImpl(get()) }
        }

        val dbModule = module {
            single { roomDb(get()) }
            single { currencyRatesDao(get()) }
            single<CurrencyRatesStorage> { CurrencyRatesStorageImpl(get()) }
        }

        val currenciesModule = module {
            single { CurrencyHelper() }
        }

        val repoModule = module {
            factory<CurrencyRatesRepo> { CurrencyRatesRepoImpl(get(), get(), get()) }
        }

        val sharedPrefsModule = module {
            single<SharedPrefs> { SharedPrefsImpl(get()) }
        }

        val ratesListModule = module {
            viewModel { CurrencyRatesViewModel(get(), get()) }
        }
        return listOf(
            networkModule,
            dbModule,
            ratesListModule,
            currenciesModule,
            repoModule,
            sharedPrefsModule)
    }

    private fun retrofit(okHttpClient: OkHttpClient, baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
    }

    private fun okHttp(): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            interceptors().forEach { interceptor ->
                clientBuilder.addInterceptor(interceptor)
            }
        }
        return clientBuilder.build()
    }

    private fun interceptors(): List<Interceptor> {
        val interceptors = arrayListOf<Interceptor>()

        with(HttpLoggingInterceptor()) {
            level = HttpLoggingInterceptor.Level.BASIC
            interceptors.add(this)
        }

        with(HttpLoggingInterceptor()) {
            level = HttpLoggingInterceptor.Level.BODY
            interceptors.add(this)
        }

        return interceptors
    }

    private fun currencyRatesClient(retrofit: Retrofit): CurrencyRatesClient {
        return retrofit.create(CurrencyRatesClient::class.java)
    }

    private fun roomDb(applicationContext: Context): AppDb {
        return Room.databaseBuilder(
            applicationContext,
            AppDb::class.java, "app-db"
        ).build()
    }

    private fun currencyRatesDao(db: AppDb): CurrencyRateDao {
        return db.currencyRateDao()
    }
}