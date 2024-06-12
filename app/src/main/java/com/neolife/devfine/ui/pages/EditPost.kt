package com.neolife.devfine.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostScreen(postId: Int, navController: NavController, viewModel: EditPostViewModel) {
    viewModel.loadPostInfo(postId)
    val scrollState = rememberScrollState()
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = "")
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            OutlinedTextField(
                label = {
                    Text(text = "Заголовок")
                },
                value = viewModel.title.value,
                onValueChange = { viewModel.title.value = it},
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                label = {
                    Text(text = "Содержание")
                }, value = viewModel.content.value,
                onValueChange = { viewModel.content.value = it },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = {
                    viewModel.onCreateClicked(navController)
            }) {
                Text(
                    text = "Сохранить",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        }
    }

class EditPostViewModel : ViewModel() {
    val id = mutableIntStateOf(0)
    val title = mutableStateOf(TextFieldValue())
    val content = mutableStateOf(TextFieldValue())
    val showFailedDialog = mutableStateOf(false)
    val dialogTitle = mutableStateOf("Error")
    val dialogCaption = mutableStateOf("Error")
    val isLoading = mutableStateOf(false)

    fun loadPostInfo(postId: Int) {
        viewModelScope.launch {
            val req = RequestHandler.getPostById(postId)
            title.value = TextFieldValue(req.post.title)
            content.value = TextFieldValue(req.post.content)
            id.intValue = req.post.id
            isLoading.value = false
        }
    }

    fun onCreateClicked(navController: NavController) {
        if (title.value.text.isBlank() || content.value.text.isBlank()) {
            isLoading.value = false
            dialogTitle.value = "Ошибка"
            dialogCaption.value = "Пожалуйста введите заголовок и содержание"
            showFailedDialog.value = true
            return
        }
        viewModelScope.launch {
            isLoading.value = true
            val req = RequestHandler.editPost(id.intValue, title.value.text, content.value.text)
            if (req == null) {
                isLoading.value = false
                dialogTitle.value = "Ошибка"
                dialogCaption.value = "Что-то пошло не так"
                showFailedDialog.value = true
                return@launch
            }
            isLoading.value = false
            navController.popBackStack()
        }
    }
}
