package com.example.mobilefinal.models

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import com.example.mobilefinal.ChatBotAPI
import com.example.mobilefinal.views.BOT
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.*

class ChatViewModel : ViewModel() {
    var currentPartner = mutableStateOf(User())
    var chats = mutableStateListOf<Chat>()
    var isBotTurn = false

    private val fStore = Firebase.firestore

    fun sendMessage(senderId: String, receiverId: String, text: String) {
        if (text.trim().isEmpty()) {
            Log.d("----------------", "[sendMessage]: text is empty. Exited.")
            return
        }

        fStore
            .collection("chats")
            .add(
                hashMapOf(
                    "senderId" to senderId,
                    "receiverId" to receiverId,
                    "text" to text.trim(),
                    "timestamp" to Calendar.getInstance().timeInMillis
                )
            )
            .addOnSuccessListener {
                Log.d("----------------", "success send msg")
            }
            .addOnFailureListener {
                Log.d("----------------", "fail send msg")
                Log.d("----------------", it.message.toString())
            }

    }

    fun clearMessages() {
        chats = mutableStateListOf<Chat>()
    }

    fun fetchMessages(loggedInUserId: String) {
        fun queryByFieldName(fieldName: String) {
            fStore
                .collection("chats")
                .whereEqualTo(fieldName, loggedInUserId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    querySnapshot.forEach { queryDocumentSnapshot ->
                        val id = queryDocumentSnapshot.id
                        val senderId = queryDocumentSnapshot.get("senderId").toString()
                        val receiverId = queryDocumentSnapshot.get("receiverId").toString()
                        val text = queryDocumentSnapshot.get("text").toString()
                        val timestamp = queryDocumentSnapshot.get("timestamp") as Long

                        val searchResult = chats.find {
                            it.id == id
                        }
                        if (searchResult == null) {
                            chats.add(Chat(id, senderId, receiverId, text, timestamp))
                        }


                    }
                    Log.d("-------------", "success get all docs")
                }
                .addOnFailureListener {
                    Log.d("-------------", "fail get all docs")
                }
        }

        fStore
            .collection("chats")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("----------------", error.message.toString())
                } else if (snapshot != null && !snapshot.isEmpty) {
                    queryByFieldName("senderId")
                    queryByFieldName("receiverId")
                    chats.sortBy { it.timestamp }

                    // for ChatWithBotView

                    if (chats.size > 0) {
                        val lastMessage = chats[chats.size - 1]
                        var botReply = ""
                        if (lastMessage.senderId == BOT.id || lastMessage.receiverId == BOT.id) {
                            if (lastMessage.senderId != BOT.id) {
                                isBotTurn = true
                                val api by lazy {
                                    Retrofit
                                        .Builder()
                                        .baseUrl("https://kukiapi.xyz/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build()
                                        .create<ChatBotAPI>()
                                }
                                viewModelScope.launch {
                                    val res = api.getReply(lastMessage.text).awaitResponse()
                                    if (res.isSuccessful) {
                                        val data: JsonObject? = res.body()
                                        botReply = data!!.get("reply").asString
                                        Log.d("----------------", botReply)
                                        if (botReply.isNotEmpty()) {
                                            sendMessage(BOT.id, loggedInUserId, botReply)
                                        } else {
                                            Log.d("----------------", "bot reply is empty")
                                        }
                                    } else {
                                        Log.d("----------------", "is not successful")
                                    }
                                }

                            }
                        }

                    }

                }
            }


    }

    fun deleteWithBotConversation(loggedInUserId: String) {
        fun deleteByFieldName(fieldName: String) {
            fStore
                .collection("chats")
                .whereEqualTo(fieldName, loggedInUserId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    querySnapshot.forEach { queryDocumentSnapshot ->
                        val id = queryDocumentSnapshot.id
                        val senderId = queryDocumentSnapshot.get("senderId").toString()
                        val anotherId =
                            queryDocumentSnapshot.get(if (fieldName == "senderId") "receiverId" else "senderId")
                                .toString()
                        val text = queryDocumentSnapshot.get("text").toString()
                        val timestamp = queryDocumentSnapshot.get("timestamp") as Long

                        if (anotherId == BOT.id) {
                            fStore.collection("chats").document(id).delete()
                        }


                    }
                    Log.d("-------------", "[delete bot conv] success get all docs")
                }
                .addOnFailureListener {
                    Log.d("-------------", "[delete bot conv] fail get all docs")
                }
        }
        deleteByFieldName("senderId")
        deleteByFieldName("receiverId")
        clearMessages()
        super.onCleared()
    }
}