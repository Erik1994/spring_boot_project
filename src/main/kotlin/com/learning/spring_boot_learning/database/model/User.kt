package com.learning.spring_boot_learning.database.model

import com.learning.spring_boot_learning.database.Documents
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(Documents.USERS)
data class User(
    val email: String,
    val hashedPassword: String,
    @Id val id: ObjectId = ObjectId()
)
