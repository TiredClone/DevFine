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
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.dcastalia.localappupdate.DownloadApk
import com.neolife.devfine.core.network.RequestHandler
import com.neolife.devfine.ui.pages.MainScreen
import com.neolife.devfine.ui.theme.DevfineTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            val url = RequestHandler.getUpdateInfoIfAvailable()
            if(url != "null") {
                val dialogBuilder = AlertDialog.Builder(this@MainActivity)
                dialogBuilder.setTitle("Update Available")
                dialogBuilder.setMessage("A new version of the app is available. Would you like to update now?")
                dialogBuilder.setPositiveButton("Update Now") { _, _ ->
                    val downloadApk = DownloadApk(this@MainActivity)

                    downloadApk.startDownloadingApk(url)
                    downloadApk.startDownloadingApk(url, "Update")
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

