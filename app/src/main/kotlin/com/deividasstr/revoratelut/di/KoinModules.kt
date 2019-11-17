package com.deividasstr.revoratelut.di

import com.deividasstr.revoratelut.BuildConfig
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object KoinModules {

        fun get(): List<Module> {
            val networkModule = module {
                single { okHttp() }
                single { retrofit(get()) }
            }
            return listOf(networkModule)
        }

        private fun retrofit(okHttpClient: OkHttpClient): Retrofit {
            return Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl(BuildConfig.baseUrl)
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
    }
}