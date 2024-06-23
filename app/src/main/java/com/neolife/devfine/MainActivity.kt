package com.neolife.devfine

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.dcastalia.localappupdate.DownloadApk
import com.neolife.devfine.core.network.RequestHandler
import com.neolife.devfine.di.core.SharedPrefManager
import com.neolife.devfine.ui.pages.MainScreen
import com.neolife.devfine.ui.theme.DevfineTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        lifecycleScope.launch {
            if (SharedPrefManager().containsRefreshToken())
                RequestHandler.refreshAccessToken(SharedPrefManager().getRefreshToken()!!)
            val url = RequestHandler.getUpdateInfoIfAvailable()
            if(url != null) {
                val dialogBuilder = AlertDialog.Builder(this@MainActivity)
                dialogBuilder.setTitle("Доступно обновление")
                dialogBuilder.setMessage("Доступна новая версия приложения. Обновить прямо сейчас?")
                dialogBuilder.setPositiveButton("Обновить") { _, _ ->
                    val downloadApk = DownloadApk(this@MainActivity)

                    if (url != null) {
                        downloadApk.startDownloadingApk(url)
                    }
                }
                val alertDialog = dialogBuilder.create()
                alertDialog.setCancelable(false)
                alertDialog.setCanceledOnTouchOutside(false)
                alertDialog.show()
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            DevfineTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    MainScreen()
                }
            }
        }
    }
}

