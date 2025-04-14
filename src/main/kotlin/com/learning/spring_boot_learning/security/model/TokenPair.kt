package com.learning.spring_boot_learning.security.model

data class TokenPair(
    val accessToken: String,
    val refreshToken: String
)
