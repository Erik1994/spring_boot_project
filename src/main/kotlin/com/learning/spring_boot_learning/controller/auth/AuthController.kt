package com.learning.spring_boot_learning.controller.auth

import com.learning.spring_boot_learning.controller.EndPoints
import com.learning.spring_boot_learning.controller.auth.request.AuthRequest
import com.learning.spring_boot_learning.controller.auth.request.RefreshRequest
import com.learning.spring_boot_learning.security.AuthService
import com.learning.spring_boot_learning.security.model.TokenPair
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(EndPoints.AUTH)
class AuthController(
    private val authService: AuthService
) {

    @PostMapping(EndPoints.REGISTER)
    fun register(@Valid @RequestBody body: AuthRequest) {
        authService.register(body.email, body.password)
    }

    @PostMapping(EndPoints.LOGIN)
    fun login(
        @RequestBody body: AuthRequest
    ): TokenPair {
        return authService.login(body.email, body.password)
    }

    @PostMapping(EndPoints.REFRESH)
    fun refresh(
        @RequestBody body: RefreshRequest
    ): TokenPair {
        return authService.refresh(body.refreshToken)
    }

}