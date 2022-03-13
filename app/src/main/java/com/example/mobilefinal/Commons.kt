package com.example.mobilefinal

import android.content.Context
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
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
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xfff8e3d8),
            focusedLabelColor = Color(0xfff8e3d8),
            unfocusedLabelColor = Color(0xfff8e3d8),
            unfocusedIndicatorColor = Color(0xfff8e3d8)
        )
    )
}

fun showAlertDialog(context: Context, text: String) {
    val builder = android.app.AlertDialog.Builder(context)
    builder.setMessage(text)
    builder.show()
}