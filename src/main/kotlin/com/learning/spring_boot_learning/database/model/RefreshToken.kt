package com.learning.spring_boot_learning.database.model

import com.learning.spring_boot_learning.database.Documents
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(Documents.REFRESH_TOKENS)
data class RefreshToken(
    val userId: ObjectId, // Here we omit id field for token, it will be generated automatically, and we don't need it to have explicitly.
    @Indexed(expireAfter = "0s") // This means that MogoDb will always check if token is expired or not,
    val expiresAt: Instant,  // and will delete from db expired tokens, and we will not need to do it manually.
    val hashedToken: String,
    val createdAt: Instant = Instant.now()
)
