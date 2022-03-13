package com.example.mobilefinal.views

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mobilefinal.MyOutlineTextField
import com.example.mobilefinal.R
import com.example.mobilefinal.models.ChatViewModel
import com.example.mobilefinal.models.User
import com.example.mobilefinal.models.UserViewModel
import kotlinx.coroutines.launch
import java.util.*


@Composable
fun ChatView(navController: NavHostController) {
    val filteredPartners: List<User>
    val filteringText = remember { mutableStateOf("") }

    val userVM = viewModel<UserViewModel>(LocalContext.current as ViewModelStoreOwner)

    userVM.fetchPartners()

    filteredPartners =
        userVM.partners.filter {
            it.nickname.lowercase(Locale.getDefault())
                .contains(filteringText.value.lowercase(Locale.getDefault()))
        }

    SearchBox(filteringText)

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        UserListView(filteredPartners, filteringText, navController)
    }


}

@Composable
fun SearchBox(filteringText: MutableState<String>) {
//    OutlinedTextField(
//        value = filteringText.value,
//        onValueChange = { filteringText.value = it },
//        label = { Text(text = "Search for a nickname...") })
    MyOutlineTextField(text = filteringText, label = "Search for a nickname...", isPw = false)
}

@Composable
fun UserListView(
    partners: List<User>,
    filteringText: MutableState<String>,
    navController: NavHostController
) {
    val userVM = viewModel<UserViewModel>(LocalContext.current as ViewModelStoreOwner)
    val chatVM = viewModel<ChatViewModel>(LocalContext.current as ViewModelStoreOwner)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xff252552))
            .padding(horizontal = 40.dp, vertical = 10.dp)
            .verticalScroll(state = ScrollState(0), enabled = true),
        horizontalAlignment = CenterHorizontally,

        ) {
        Text("User List (tap one to chat)", fontWeight = FontWeight(700), color = Color(0xfff8e3d8))
        SearchBox(filteringText = filteringText)
        Spacer(modifier = Modifier.height(20.dp))

        if (partners.isNotEmpty() && partners[0].id.trim() != "") {
            partners.forEach {
                if (it.id != userVM.loggedInUser.value.id && it.id != BOT.id) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .height(50.dp),
                        elevation = 5.dp
                    ) {
                        TextButton(onClick = {
                            chatVM.currentPartner.value = User(it.id, it.nickname)
                            navController.navigate(CHAT_CONVERSATION_ROUTE)
                        }) {
                            Text(text = it.nickname)
                        }
                    }
                }

            }
        }
    }

}

@Composable
fun ConversationView() {
    val text = remember { mutableStateOf("") }
    val userVM = viewModel<UserViewModel>(LocalContext.current as ViewModelStoreOwner)
    val chatVM = viewModel<ChatViewModel>(LocalContext.current as ViewModelStoreOwner)
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    fun scrollToNewMessage() {
        coroutineScope.launch {
            scrollState.animateScrollBy(250f)
        }

    }

    chatVM.fetchMessages(userVM.loggedInUser.value.id)
    scrollToNewMessage()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xffdf4e6b))
            .padding(10.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .weight(1f, false)
        ) {
            chatVM.chats.forEach {
                val isMyMessage = it.senderId == userVM.loggedInUser.value.id

                if (chatVM.currentPartner.value.id in listOf(it.senderId, it.receiverId)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isMyMessage) Arrangement.End else Arrangement.Start
                    ) {
                        Column {
                            if (it.senderId != userVM.loggedInUser.value.id) {
                                Text(
                                    text = chatVM.currentPartner.value.nickname,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 20.dp)
                                )
                            }
                            Card(
                                elevation = 10.dp,
                                backgroundColor = if (isMyMessage) Color.Blue else Color.Gray,
                                modifier = Modifier
                                    .padding(horizontal = 20.dp, vertical = 5.dp)

                            ) {

                                Text(
                                    text = it.text,
                                    modifier = Modifier.padding(10.dp),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MyOutlineTextField(text = text, label = "Type a message...", isPw = false)
            Icon(
                tint = Color.Blue,
                painter = painterResource(id = R.drawable.ic_send),
                contentDescription = "send a message",
                modifier = Modifier
                    .width(50.dp)
                    .clickable {
                        handleSendButton(chatVM, userVM, text)
                    })
        }
    }
}

fun handleSendButton(chatVM: ChatViewModel, userVM: UserViewModel, text: MutableState<String>) {
    chatVM.sendMessage(
        userVM.loggedInUser.value.id,
        chatVM.currentPartner.value.id,
        text.value
    )
    text.value = ""
}