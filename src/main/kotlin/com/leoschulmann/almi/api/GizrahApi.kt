package com.leoschulmann.almi.api

import com.leoschulmann.almi.dto.toDto
import com.leoschulmann.almi.entities.Gizrah
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.gizrahApi() {
    routing {
        route("/api/gizrah") {
            post {
                val newGizrah = call.receiveText() // validate

                val gizrah = transaction {
                    Gizrah.new { value = newGizrah }
                }

                call.respond(HttpStatusCode.OK, gizrah.id.value)
            }

            put {
                val id = call.parameters["id"]?.toLongOrNull()
                val newValue = call.receiveText()

                if (id == null || newValue.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID or value")
                    return@put
                }

                val gizrah: Gizrah? = transaction { Gizrah.findById(id) }

                if (gizrah == null) {
                    call.respond(HttpStatusCode.NotFound, "Gizrah entity not found")
                    return@put
                }

                val updatedGizrah = transaction {
                    gizrah.apply {
                        value = newValue
                        version += 1
                    }
                }

                call.respond(HttpStatusCode.OK, updatedGizrah.id.value)
            }

            get {
                val id = call.parameters["id"]?.toLongOrNull()
                if (id == null) {
                    val gizrahs = transaction { Gizrah.all().map { it.toDto() } }
                    call.respond(HttpStatusCode.OK, gizrahs)
                    return@get
                } else {
                    val gizrah: Gizrah? = transaction { Gizrah.findById(id) }
                    if (gizrah == null) {
                        call.respond(HttpStatusCode.NotFound, "Gizrah entity not found")
                        return@get
                    } else {
                        call.respond(HttpStatusCode.OK, gizrah.toDto())
                    }
                }
            }
        }
    }
}