package com.neolife.devfine

import android.app.Application
import com.neolife.devfine.di.core.AppInfoModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DevFine: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidLogger()
            androidContext(this@DevFine)
            modules(AppInfoModel.prefModule)
        }
    }
}