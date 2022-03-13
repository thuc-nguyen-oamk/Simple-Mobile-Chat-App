package com.example.mobilefinal.views

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobilefinal.MyOutlineTextField
import com.example.mobilefinal.R
import com.example.mobilefinal.models.ChatViewModel
import com.example.mobilefinal.models.UserViewModel
import com.example.mobilefinal.showAlertDialog

@Composable
fun LoginRegistrationView() {
    viewModel<UserViewModel>(LocalContext.current as ViewModelStoreOwner)
    val shouldShowLoginView = remember { mutableStateOf(true) }
    if (shouldShowLoginView.value) LoginView(shouldShowLoginView) else RegistrationView(
        shouldShowLoginView
    )

}

@Composable
fun LoginView(shouldShowLoginView: MutableState<Boolean>) {
    val userVM = viewModel<UserViewModel>(LocalContext.current as ViewModelStoreOwner)
    val chatVM = viewModel<ChatViewModel>(LocalContext.current as ViewModelStoreOwner)
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff252552)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.width(200.dp)
        )
        MyOutlineTextField(text = email, label = "Email", isPw = false)
        MyOutlineTextField(text = password, label = "Password", isPw = true)
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = {
            handleLogin(context, userVM, chatVM, email.value, password.value)
        }) { Text(text = "LOGIN") }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            onClick = {
                shouldShowLoginView.value = false
            },
            modifier = Modifier.background(Color.Gray)
        ) {
            Text(text = "SWITCH TO REGISTER")
        }
    }


}


fun handleLogin(
    context: Context,
    userVM: UserViewModel,
    chatVM: ChatViewModel,
    email: String,
    password: String,
) {
    if (email.trim().isEmpty() || password.trim().isEmpty()) {
        showAlertDialog(context, "Please enter your email and password.")
        return
    }

    userVM.login(
        context,
        email.trim(),
        password.trim()
    )

    chatVM.clearMessages()
}

@Composable
fun RegistrationView(shouldShowLoginView: MutableState<Boolean>) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val nickname = remember { mutableStateOf("") }
    val userVM = viewModel<UserViewModel>(LocalContext.current as ViewModelStoreOwner)
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xffdf4e6b)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MyOutlineTextField(text = email, label = "Email", isPw = false)
        MyOutlineTextField(text = password, label = "Password (minimum 6 characters)", isPw = true)
//        MyOutlineTextField(text = passwordAgain, label = "Re-type password", isPw = true)
        MyOutlineTextField(text = nickname, label = "Nickname (4-16 characters)", isPw = false)
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            modifier = Modifier.background(Color(0xffe77f8f)),
            onClick = {
                handleRegister(context, userVM, email.value, password.value, nickname.value)
            }) { Text(text = "Register") }
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedButton(
            onClick = {
                shouldShowLoginView.value = true
            },
            modifier = Modifier.background(Color(R.color.secondary))
        ) {
            Text(text = "GO BACK")
        }


    }
}

fun handleRegister(
    context: Context,
    userVM: UserViewModel,
    email: String,
    password: String,
    nickname: String
) {
    if (email.trim().isEmpty() || password.trim().isEmpty()) {
        showAlertDialog(context, "Please enter your email and password.")
        return
    }

    userVM.register(
        context,
        email.trim(),
        password.trim(),
        nickname.trim()
    )
}