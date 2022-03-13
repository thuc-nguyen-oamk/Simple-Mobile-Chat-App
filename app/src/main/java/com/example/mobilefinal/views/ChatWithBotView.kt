package com.example.mobilefinal.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mobilefinal.models.ChatViewModel
import com.example.mobilefinal.models.User
import com.example.mobilefinal.models.UserViewModel

val BOT = User("slDYRpcZp8VxJ3GUyRrj89ZKtQp1", "Virtual Friend")

@Composable
fun ChatWithBotView(navController: NavHostController) {
    val chatVM = viewModel<ChatViewModel>(LocalContext.current as ViewModelStoreOwner)
    val userVM = viewModel<UserViewModel>(LocalContext.current as ViewModelStoreOwner)
    chatVM.currentPartner.value = BOT
    Column {
        OutlinedButton(onClick = {
            chatVM.deleteWithBotConversation(userVM.loggedInUser.value.id, navController)
        }) {
            Text(text = "Delete this conversation")
        }
        ConversationView()
    }
    if (chatVM.chats.size == 0){
        chatVM.sendMessage(BOT.id, userVM.loggedInUser.value.id, "Hi, let's start a chat!")
    }
}