package com.neolife.devfine.di.core

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object AppInfoModel {
    val prefModule = module {
        single {
            androidContext().packageManager
        }
    }
}