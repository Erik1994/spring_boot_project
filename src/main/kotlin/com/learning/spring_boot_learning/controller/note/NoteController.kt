package com.learning.spring_boot_learning.controller.note

import com.learning.spring_boot_learning.controller.EndPoints
import com.learning.spring_boot_learning.controller.note.mapper.toNote
import com.learning.spring_boot_learning.controller.note.mapper.toResponse
import com.learning.spring_boot_learning.controller.note.request.NoteRequest
import com.learning.spring_boot_learning.controller.note.response.NoteResponse
import com.learning.spring_boot_learning.database.repository.NoteRepository
import jakarta.validation.Valid
import org.bson.types.ObjectId
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.jvm.optionals.getOrElse

// http://localhost:8080/notes

@RestController
@RequestMapping(EndPoints.NOTES)
class NoteController(
    private val repository: NoteRepository
) {

    @PostMapping
    fun save(@Valid @RequestBody body: NoteRequest): NoteResponse {
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        return repository.save(body.toNote(ObjectId(ownerId))).toResponse()
    }

    @GetMapping
    fun getByOwnerId(): List<NoteResponse> {
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        return repository.findByOwnerId(ownerId = ObjectId(ownerId)).map { it.toResponse() }
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteById(@PathVariable id: String) {
        val note = repository.findById(ObjectId(id)).orElseThrow { IllegalArgumentException("Note with the provided id doesn't exist") }
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        if (note.ownerId.toHexString() == ownerId) {
            repository.deleteById(ObjectId(id))
        }
    }
}