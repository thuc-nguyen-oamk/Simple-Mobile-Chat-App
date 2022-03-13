package com.example.mobilefinal.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobilefinal.R
import com.example.mobilefinal.models.UserViewModel

const val APP_NAME = "1-1 CHAT"
const val CHAT_ROUTE = "chat"
const val CHAT_CONVERSATION_ROUTE = "conversation"
const val BOT_ROUTE = "bot"

@Composable
fun MainScaffoldView() {
    val userVM = viewModel<UserViewModel>(LocalContext.current as ViewModelStoreOwner)
    val navController = rememberNavController()

    Scaffold(
        topBar = { TopBar(userVM) },
        bottomBar = { BottomBar(navController) },
    ) {
        Box(modifier = Modifier.padding(it)) {
            MainContent(navController)
        }
    }
}

@Composable
fun TopBar(userVM: UserViewModel) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(R.color.primary))
            .padding(5.dp)

    ) {
        Text(text = userVM.loggedInUser.value.nickname, color = Color(0xfff8e3d8))
        Text(text = APP_NAME, fontFamily = FontFamily.Monospace, color = Color(0xfff8e3d8), fontWeight = FontWeight(700))
        OutlinedButton(onClick = { userVM.logout() }) {
            Text(text = "Logout")
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(R.color.primary_variant)
            )
            .padding(vertical = 10.dp)
    ) {
        Icon(
            tint = Color(0xfff8e3d8),
            painter = painterResource(id = R.drawable.ic_chat),
            contentDescription = "chat",
            modifier = Modifier.clickable {
                navController.navigate(
                    CHAT_ROUTE
                )
            })
        Icon(
            tint = Color(0xfff8e3d8),
            painter = painterResource(id = R.drawable.ic_bot),
            contentDescription = "bot",
            modifier = Modifier.clickable {
                navController.navigate(
                    BOT_ROUTE
                )
            })
    }
}

@Composable
fun MainContent(navController: NavHostController) {
    NavHost(navController = navController, startDestination = CHAT_ROUTE) {
        composable(route = CHAT_ROUTE) { ChatView(navController) }
        composable(route = BOT_ROUTE) { ChatWithBotView(navController) }
        composable(route = CHAT_CONVERSATION_ROUTE) { ConversationView() }
    }
}
