package com.kr.cloudnotes.models

data class Note(
    val title: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
