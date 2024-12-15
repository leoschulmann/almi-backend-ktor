package com.leoschulmann.almi.api

import com.leoschulmann.almi.entities.Root
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.rootApi() {
    routing {
        route("/api/root") {
            post {
                val newRoot = call.receiveText() // validate

                val root = transaction {
                    Root.new { value = newRoot }
                }

                call.respond(HttpStatusCode.OK, root.id.value)
            }

            put {
                val id = call.parameters["id"]?.toLongOrNull()
                val newValue = call.receiveText()

                if (id == null || newValue.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID or value")
                    return@put
                }
                
                val root: Root? = transaction { Root.findById(id) }
                
                if (root == null) {
                    call.respond(HttpStatusCode.NotFound, "Root entity not found")
                    return@put
                }
                
                val updatedRoot = transaction {
                    root.apply {
                        value = newValue
                        version += 1
                    }
                }
                
                call.respond(HttpStatusCode.OK, updatedRoot.id.value)
            }
        }
    }
}
