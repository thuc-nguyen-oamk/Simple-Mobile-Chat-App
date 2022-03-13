package com.example.mobilefinal

import android.content.Context
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun MyOutlineTextField(
    text: MutableState<String>,
    label: String,
    isPw: Boolean,
) {
    OutlinedTextField(
        value = text.value,
        onValueChange = { text.value = it },
        label = { Text(label) },
        visualTransformation = if (isPw) PasswordVisualTransformation() else VisualTransformation.None,
    )
}

fun showAlertDialog(context: Context, text: String) {
    var builder = android.app.AlertDialog.Builder(context)
    builder.setMessage(text)
    builder.show()
}