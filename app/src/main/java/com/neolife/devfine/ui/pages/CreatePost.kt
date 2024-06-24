package com.neolife.devfine.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.neolife.devfine.core.network.RequestHandler
import com.neolife.devfine.ui.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(navController: NavController, viewModel: CreatePostViewModel) {
    val scrollState = rememberScrollState()
    val localUriHandler = LocalUriHandler.current
    val color = when {
        isSystemInDarkTheme() -> Color.White
        else -> Color.Black
    }
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = "Создание материала", fontWeight = FontWeight.Bold,  color = color)
        }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(30.dp)
                )
            }
        })
    }) { innerPadding ->
        Column {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                OutlinedTextField(
                    label = {
                        Text(text = "Заголовок")
                    },
                    value = viewModel.title.value,
                    onValueChange = { viewModel.title.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp)
                )
                OutlinedTextField(
                    label = {
                        Text(text = "Содержание в формате markdown")
                    }, value = viewModel.content.value,
                    onValueChange = { viewModel.content.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(10.dp)
                )

                Button(
                    onClick = {
                        viewModel.onCreateClicked(navController)
                    }, shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .width(250.dp)
                        .height(60.dp)
                        .padding(top = 15.dp)
                ) {
                    Text(
                        text = "Опубликовать",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                }
                if (viewModel.showFailedDialog.value)
                    AlertDialog(
                        onDismissRequest = { viewModel.showFailedDialog.value = false },
                        title = { Text(text = viewModel.dialogTitle.value) },
                        text = {
                            Text(text = viewModel.dialogCaption.value)
                        },
                        confirmButton = {
                            TextButton(onClick = { viewModel.showFailedDialog.value = false }) {
                                Text(text = "OK")
                            }
                        }
                    )
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(top = 25.dp, start = 15.dp)
            ) {
                Text(text = "Полезно знать", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(
                    text = "Документация по использованию Markdown",
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            localUriHandler.openUri("https://www.markdownguide.org/extended-syntax/")
                        },
                    textDecoration = TextDecoration.Underline
                )

                Text(
                    text = "Документация по Android",
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            localUriHandler.openUri("https://developer.android.com/")
                        },
                    textDecoration = TextDecoration.Underline
                )

                Text(
                    text = "Документация по Kotlin",
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ){
                            localUriHandler.openUri("https://kotlinlang.org/docs/home.html")
                        },
                    textDecoration = TextDecoration.Underline
                )

            }
        }

    }
}

class CreatePostViewModel : ViewModel() {
    val id = mutableIntStateOf(0)
    val title = mutableStateOf(TextFieldValue())
    val content = mutableStateOf(TextFieldValue())
    val showFailedDialog = mutableStateOf(false)
    val dialogTitle = mutableStateOf("Error")
    val dialogCaption = mutableStateOf("Error")
    val isEdit = mutableStateOf(false)
    val isLoading = mutableStateOf(false)

    fun onCreateClicked(navController: NavController) {
        if (title.value.text.isBlank() && content.value.text.isBlank()) {
            isLoading.value = false
            dialogTitle.value = "Ошибка"
            dialogCaption.value = "Пожалуйста, введите заголовок и содержание статьи"
            showFailedDialog.value = true
            return
        }

        if (title.value.text.isBlank()) {
            isLoading.value = false
            dialogTitle.value = "Ошибка"
            dialogCaption.value = "Пожалуйста, введите заголовок статьи"
            showFailedDialog.value = true
            return
        }
        if (content.value.text.isBlank()) {
            isLoading.value = false
            dialogTitle.value = "Ошибка"
            dialogCaption.value = "Пожалуйста, введите содержание статьи"
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
