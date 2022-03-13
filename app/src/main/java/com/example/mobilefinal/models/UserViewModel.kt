package com.example.mobilefinal.models

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mobilefinal.showAlertDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserViewModel : ViewModel() {
    var loggedInUser = mutableStateOf(User())
    var partners = mutableStateListOf<User>()

    private var fAuth = Firebase.auth
    private var fStore = Firebase.firestore

    fun login(context: Context, email: String, password: String) {
        Log.d("-------------", "login event")
        fAuth
            .signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                Log.d("-------------", "firebase auth success")
                Log.d("-------------", authResult.toString())
                if (authResult.user != null) {
                    fStore
                        .collection("users")
                        .document(authResult.user!!.uid)
                        .get()
                        .addOnSuccessListener {
                            Log.d("-------------", "success get document")
                            Log.d("-------------", it.get("nickname").toString())
                            loggedInUser.value =
                                User(authResult.user!!.uid, it.get("nickname").toString())
                        }
                }
            }
            .addOnFailureListener {

                showAlertDialog(context, "Wrong username or password.")
                Log.d("-------------", "firebase auth failed")
                Log.d("-------------", it.toString())
            }

    }

    fun logout() {
        fAuth.signOut()
        loggedInUser.value = User()
    }

    fun register(context: Context, email: String, password: String, nickname: String) {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()
        val trimmedNickname = nickname.trim()
        if (trimmedEmail.isEmpty() || trimmedPassword.isEmpty() || trimmedNickname.isEmpty()
            || trimmedNickname.length < 4 || trimmedNickname.length > 16
        ) {
            Log.d("-------------", "email/pw/nickname empty or nickname not from 4-16 chars")
            showAlertDialog(
                context,
                "Registration failed. Please check your email, password and nickname again."
            )
            return
        }

        fStore
            .collection("users")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val allNicknames = mutableListOf<String>()
                querySnapshot.forEach {
                    allNicknames.add(it.get("nickname").toString())
                }
                if (allNicknames.indexOf(trimmedNickname) > -1) {
                    Log.d("----------------", "nickname existed")
                    showAlertDialog(
                        context,
                        "This nickname is not available. Please choose another one."
                    )
                } else {
                    fAuth
                        .createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            Log.d("-------------", "success register")
                            Log.d("-------------", it.user?.uid.toString())
                            if (it.user != null) {
                                fStore.collection("users").document(it.user!!.uid).set(
                                    mapOf("nickname" to nickname)
                                ).addOnSuccessListener {
                                    Log.d("-------------", "success add document with uid")
                                }.addOnFailureListener {
                                    Log.d("-------------", "fail add document with uid")
                                }

                            } else {
                                Log.e("-------------", "it.user is null!!!")
                            }
                            showAlertDialog(context, "Registration succeeded. You can login now.")
                        }
                        .addOnFailureListener {
                            Log.d("-------------", "failure register")
                            Log.d("-------------", it.toString())
                            var errorMessage = ""
                            try {
                                errorMessage = it.toString().split(":")[1].trim()
                            } catch (e: Exception) {
                                Log.e("-------------", e.toString())
                            }
                            showAlertDialog(context, "Registration failed. $errorMessage")
                        }
                }
            }
            .addOnFailureListener {
                Log.d("----------------", "fail getting docs")
            }


    }

    fun fetchPartners() {
        fStore
            .collection("users")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val test = querySnapshot.map { it ->
                    User(it.id, it.get("nickname").toString())
                }

                querySnapshot.forEach { queryDocumentSnapshot ->
                    val id = queryDocumentSnapshot.id
                    val nickname = queryDocumentSnapshot.get("nickname").toString()

                    val searchResult = partners.find { p ->
                        p.id == id
                    }
                    if (searchResult == null) {
                        partners.add(User(id, nickname))

                    }


                }
                Log.d("-------------", "success get all docs")
            }
            .addOnFailureListener {
                Log.d("-------------", "fail get all docs")
            }
    }


}