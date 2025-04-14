package com.learning.spring_boot_learning.database.model

import com.learning.spring_boot_learning.database.Documents
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(Documents.NOTES)
data class Note(
    val title: String,
    val content: String,
    val color: Long,
    val createdAt: Instant,
    val ownerId: ObjectId,
    @Id val id: ObjectId = ObjectId.get(),
)
