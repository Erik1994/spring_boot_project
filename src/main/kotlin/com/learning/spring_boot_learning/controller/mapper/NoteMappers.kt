package com.learning.spring_boot_learning.controller.mapper

import com.learning.spring_boot_learning.controller.request.NoteRequest
import com.learning.spring_boot_learning.controller.response.NoteResponse
import com.learning.spring_boot_learning.database.model.Note
import org.bson.types.ObjectId
import java.time.Instant

fun Note.toResponse(): NoteResponse = NoteResponse(
    id = id.toHexString(),
    title = title,
    content = content,
    color = color,
    createdAt = createdAt
)

fun NoteRequest.toNote(): Note = Note(
    id = id?.let { ObjectId(it) } ?: ObjectId.get(),
    title = title,
    content = content,
    color = color,
    createdAt = Instant.now(),
    ownerId = ObjectId()
)