package com.deividasstr.revoratelut

import android.app.Application
import com.deividasstr.revoratelut.di.KoinModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(KoinModules.get(BuildConfig.baseUrl))
        }
    }
}