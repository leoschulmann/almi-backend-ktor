package com.leoschulmann.almi.api

import com.leoschulmann.almi.domain.Preposition
import com.leoschulmann.almi.domain.PrepositionDto
import com.leoschulmann.almi.domain.UpdatePrepositionDto
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.prepositionApi() {
    routing {
        route("/api/preposition") {
            post({
                request { body<String>() }
                response {
                    code(HttpStatusCode.Created) { body<PrepositionDto>() }
                    code(HttpStatusCode.BadRequest) {}
                }
            }) {
                val newPreposition = call.receiveText() // validate

                if (newPreposition.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid value")
                    return@post
                }

                val preposition = transaction {
                    Preposition.new { value = newPreposition }
                }

                call.respond(HttpStatusCode.Created, preposition.toDto())
            }

            put({
                request {
                    body<UpdatePrepositionDto>()
                }
                response {
                    code(HttpStatusCode.OK) { body<PrepositionDto>() }
                    code(HttpStatusCode.NotFound) {}
                    code(HttpStatusCode.BadRequest) {}
                }
            }) {
                val (id, newValue) = call.receive<UpdatePrepositionDto>()

                if (newValue.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID or value")
                    return@put
                }

                val preposition: Preposition? = transaction { Preposition.findById(id) }

                if (preposition == null) {
                    call.respond(HttpStatusCode.NotFound, "Preposition entity not found")
                    return@put
                }

                val updatedPreposition = transaction {
                    preposition.apply {
                        value = newValue
                        version += 1
                    }
                }

                call.respond(HttpStatusCode.OK, updatedPreposition.toDto())
            }

            get({
                response {
                    code(HttpStatusCode.OK) { body<List<PrepositionDto>>() }
                }
            }) {
                val id = call.parameters["id"]?.toLongOrNull()
                if (id == null) {
                    val dtos = transaction {
                        Preposition.all().map { it.toDto() }
                    }
                    call.respond(HttpStatusCode.OK, dtos)
                    return@get
                }
            }

            get("{id}", {
                request { pathParameter<Int>("id", { required = true }) }
                response {
                    code(HttpStatusCode.OK) { body<PrepositionDto>() }
                    code(HttpStatusCode.NotFound) {}
                    code(HttpStatusCode.BadRequest) {}
                }
            }) {
                val id = call.parameters["id"]?.toLongOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                    return@get
                }
                val preposition = transaction { Preposition.findById(id) }
                if (preposition == null) {
                    call.respond(HttpStatusCode.NotFound, "Preposition entity not found")
                    return@get
                } else {
                    call.respond(HttpStatusCode.OK, preposition.toDto())
                }

            }
        }
    }
}