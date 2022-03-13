package com.example.mobilefinal

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobilefinal.models.UserViewModel
import com.example.mobilefinal.ui.theme.MobileFinalTheme
import com.example.mobilefinal.views.LoginRegistrationView
import com.example.mobilefinal.views.MainScaffoldView

//import com.example.mobilefinal.views.LoginRegistrationView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileFinalTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainView()
                }
            }
        }
    }
}

@Composable
fun MainView() {
    val userVM = viewModel<UserViewModel>(LocalContext.current as ViewModelStoreOwner)
    Log.d("----------", userVM.loggedInUser.value.id)
    if (userVM.loggedInUser.value.id == "") LoginRegistrationView() else MainScaffoldView()
}