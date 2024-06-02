package com.neolife.devfine.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.neolife.devfine.core.network.RequestHandler
import com.neolife.devfine.ui.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun CreatePostScreen(navController: NavController, viewModel: CreatePostViewModel) {
    Column(
        modifier = Modifier
            .padding(top = 40.dp)
            .fillMaxSize()
    ) {
        OutlinedTextField(label = {
            Text(text = "Заголовок")
        }, value = viewModel.title.value,
            onValueChange = { viewModel.title.value = it },
            modifier = Modifier.fillMaxWidth())

        OutlinedTextField(label = {
            Text(text = "Содержание")
        }, value = viewModel.content.value, onValueChange = { viewModel.content.value = it },
            modifier = Modifier.fillMaxWidth())

        Button(onClick = { viewModel.onRegisterClicked(navController) }) {
            Text(
                text = "Опубликовать",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
        }
    }
}

class CreatePostViewModel : ViewModel() {
    val title = mutableStateOf(TextFieldValue())
    val content = mutableStateOf(TextFieldValue())
    val showFailedDialog = mutableStateOf(false)
    val dialogTitle = mutableStateOf("Error")
    val dialogCaption = mutableStateOf("Error")
    val isLoading = mutableStateOf(false)

    fun onRegisterClicked(navController: NavController) {
        if (title.value.text == "" || content.value.text == "") {
            isLoading.value = false
            dialogTitle.value = "Ошибка"
            dialogCaption.value = "Пожалуйста введите заголовок и содержание"
            showFailedDialog.value = true
            return
        }
        viewModelScope.launch {
            isLoading.value = true
            val req = RequestHandler.createPost(title.value.text, content.value.text)
            if (req == null) {
                isLoading.value = false
                dialogTitle.value = "Ошибка"
                dialogCaption.value = "Что-то пошло не так"
                showFailedDialog.value = true
                return@launch
            }
            isLoading.value = false
            navController.navigate(Screen.HomePage.route)
        }
    }
}