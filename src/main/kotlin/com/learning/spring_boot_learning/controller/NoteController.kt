package com.learning.spring_boot_learning.controller

import com.learning.spring_boot_learning.controller.mapper.toNote
import com.learning.spring_boot_learning.controller.mapper.toResponse
import com.learning.spring_boot_learning.controller.request.NoteRequest
import com.learning.spring_boot_learning.controller.response.NoteResponse
import com.learning.spring_boot_learning.database.repository.NoteRepository
import org.bson.types.ObjectId
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


// http://localhost:8080/notes

@RestController
@RequestMapping("/notes")
class NoteController(
    private val repository: NoteRepository
) {

    @PostMapping
    fun save(@RequestBody body: NoteRequest): NoteResponse = repository.save(body.toNote()).toResponse()

    @GetMapping
    fun getByOwnerId(@RequestParam(required = true) ownerId: String): List<NoteResponse> =
        repository.findByOwnerId(ownerId = ObjectId(ownerId)).map { it.toResponse() }

    @DeleteMapping(path = ["/{id}"])
    fun deleteById(@PathVariable id: String) {
        repository.deleteById(ObjectId(id))
    }
}