package com.neolife.devfine.ui.pages


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.neolife.devfine.core.network.RequestHandler
import com.neolife.devfine.di.core.SharedPrefManager
import com.neolife.devfine.ui.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel, navController: NavController) {
    viewModel.loadingUserData(navController)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri:   Uri? ->
            if (uri != null) {
                val inputStream = context.contentResolver.openInputStream(uri)
                val imageByteArray = inputStream?.readBytes()

                scope.launch {
                    RequestHandler.changeAvatar(imageByteArray)
                    navController.navigate(Screen.ProfilePage.route) {
                        popUpTo(Screen.ProfilePage.route) {
                            saveState = true
                        }
                    }
                }
            }
        }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.SettingsPage.route) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            if (!viewModel.isLoading.value) {
                AsyncImage(
                    model = viewModel.avatar.value,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(180.dp)
                        .clickable {
                            launcher.launch("image/jpeg")
                        }
                        .border(2.dp, color = MaterialTheme.colorScheme.outline, CircleShape)
                )
                
                Spacer(modifier = Modifier.size(20.dp))

                Text(text = viewModel.username.value,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp)

                Spacer(modifier = Modifier.size(20.dp))
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

class ProfileViewModel : ViewModel() {
    val username = mutableStateOf("")
    val avatar = mutableStateOf("")
    val isLoading = mutableStateOf(false)

    fun loadingUserData(navController: NavController) {
        if (!SharedPrefManager().containsRefreshToken()) {
            navController.navigate(Screen.AuthPage.route)
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            val req =
                RequestHandler.refreshAccessToken(SharedPrefManager().getRefreshToken().toString())

            if (req == "" || req == null) {
                navController.navigate(Screen.AuthPage.route)
            }

            val infoReq =
                RequestHandler.getProfileByUsername(SharedPrefManager().getUsername().toString())

            if (infoReq == null) {
                navController.navigate(Screen.AuthPage.route)
            } else {
                username.value = infoReq.username
                avatar.value =
                    "https://devfine.tiredclone.me/api/users/images?filename=${infoReq.profilePicture}"
                isLoading.value = false
            }
        }
    }
}
