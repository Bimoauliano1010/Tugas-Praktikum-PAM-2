package com.example.tugaspraktikumpam2.model

data class News(
    val id: Int,
    val title: String,
    val category: String,
    val content: String,
    val timestamp: String,
    val isRead: Boolean = false
)

data class NewsDisplay(
    val id: Int,
    val formattedTitle: String,
    val category: String,
    val content: String,
    val formattedTime: String,
    val isRead: Boolean
)
