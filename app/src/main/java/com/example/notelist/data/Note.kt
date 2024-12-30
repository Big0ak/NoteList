package com.example.notelist.data

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val timestamp: String,
    var finished: Boolean = false
)
