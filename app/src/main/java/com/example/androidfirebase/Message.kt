package com.example.androidfirebase

data class Message(
    val id: String = "", // Firestore 문서 ID
    val text: String = "",
    val senderId: String = "", // 보낸 사용자 ID
    val senderFirstName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isSentByCurrentUser: Boolean = false
)
