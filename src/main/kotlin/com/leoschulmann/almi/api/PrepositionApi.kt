package com.leoschulmann.almi.api

import com.leoschulmann.almi.domain.Preposition
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.prepositionApi() {
    routing {
        route("/api/preposition") {
            post {
                val newPreposition = call.receiveText() // validate

                val preposition = transaction {
                    Preposition.new { value = newPreposition }
                }

                call.respond(HttpStatusCode.OK, preposition.id.value)
            }
            put {
                val id = call.parameters["id"]?.toLongOrNull()
                val newValue = call.receiveText()

                if (id == null || newValue.isBlank()) {
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

                call.respond(HttpStatusCode.OK, updatedPreposition.id.value)
            }

            get {
                val id = call.parameters["id"]?.toLongOrNull()
                if (id == null) {
                    val dtos = transaction {
                        Preposition.all().map { it.toDto() }
                    }
                    call.respond(HttpStatusCode.OK, dtos)
                    return@get
                } else {
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
}