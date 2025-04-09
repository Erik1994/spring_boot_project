package com.learning.spring_boot_learning.controller.request

data class NoteRequest(
    val id: String?,
    val title: String,
    val content: String,
    val color: Long
)
