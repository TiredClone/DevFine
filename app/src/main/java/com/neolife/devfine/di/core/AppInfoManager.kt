package com.neolife.devfine.di.core

import android.content.pm.PackageManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class AppInfoManager : KoinComponent {
    private val packageManager: PackageManager by inject()

    fun getAppVersion(): String {
        val packageInfo = packageManager.getPackageInfo("com.neolife.devfine", 0)
        return packageInfo.versionName
    }
}