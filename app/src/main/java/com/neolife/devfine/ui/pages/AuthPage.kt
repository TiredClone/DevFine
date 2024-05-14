package com.neolife.devfine.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.neolife.devfine.R
import com.neolife.devfine.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(navController: NavController) {
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = "")
        }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(30.dp),
                    tint = Color.White
                )
            }
        })
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val login = remember {
                mutableStateOf("")
            }

            val password = remember {
                mutableStateOf("")
            }

            Image(
                painter = painterResource(R.drawable.play_store_512),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Text(
                text = "Вход в аккаунт",
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 30.dp)
            )
            
            Spacer(modifier = Modifier.padding(20.dp))
            
            OutlinedTextField(label = {
                Text(text = "Логин")
            }, 
                value = login.value,
                onValueChange = { login.value = it })

            Spacer(modifier = Modifier.padding(10.dp))

            OutlinedTextField(label = {
                Text(text = "Пароль")
            },
                value = password.value,
                onValueChange = { password.value = it })

            Spacer(modifier = Modifier.padding(20.dp))

            Button(onClick = { /*TODO*/ }) {
                    Text(text = "Войти",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .height(40.dp)
                            .width(190.dp),
                        textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.padding(15.dp))

           Row(
               verticalAlignment = Alignment.CenterVertically) {
               Text(text = "Нет аккаунта?", fontSize = 15.sp)
               TextButton(onClick = { navController.navigate(Screen.RegisterPage.route) }) {
                  Text(text = "Регистрация", fontSize = 15.sp)
               }
           }
        }
    }
}