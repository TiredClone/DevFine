package com.neolife.devfine.di.core

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object SharedPrefModel {
    val module = module {
        single {
            androidContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        }
    }
}