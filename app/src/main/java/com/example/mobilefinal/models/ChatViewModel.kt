package com.example.mobilefinal.models

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
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
    private var lastBotReplyTimestamp = 0L

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
//                    scrollToLatestMessageCallback


                    // for ChatWithBotView
                    if (chats.size > 0) {
                        val lastMessage = chats[chats.size - 1]
                        var botReply: String
                        if (lastMessage.receiverId == BOT.id) {
                            val currentTime = Calendar.getInstance().timeInMillis
                            Log.d("------time in seconds:", Calendar.getInstance().time.toString())
                            if (lastBotReplyTimestamp == 0L || currentTime - lastBotReplyTimestamp > 1000L){
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
                                            lastBotReplyTimestamp = Calendar.getInstance().timeInMillis
                                        } else {
                                            Log.d("----------------", "bot reply is empty")
                                        }
                                    } else {
                                        Log.e("----------------", "failed to call chatbot api")
                                    }
                                }
                            }


                        }

                    }

                }
            }

    }

    fun deleteWithBotConversation(loggedInUserId: String, navController: NavHostController) {
        fun deleteByFieldName(fieldName: String) {
            fStore
                .collection("chats")
                .whereEqualTo(fieldName, loggedInUserId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    querySnapshot.forEach { queryDocumentSnapshot ->
                        val id = queryDocumentSnapshot.id
                        val anotherId =
                            queryDocumentSnapshot.get(if (fieldName == "senderId") "receiverId" else "senderId")
                                .toString()

                        if (anotherId == BOT.id) {
                            fStore.collection("chats").document(id).delete()
                        }
                    }
                    navController.navigateUp()
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