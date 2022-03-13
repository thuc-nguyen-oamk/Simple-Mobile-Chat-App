package com.example.mobilefinal.models

import java.time.LocalDateTime
import java.util.*

data class Chat(val id:String, val senderId:String, val receiverId:String, val text:String, val timestamp: Long)