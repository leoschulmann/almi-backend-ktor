package com.leoschulmann.almi.api

import com.leoschulmann.almi.dto.RootDto
import com.leoschulmann.almi.dto.toDto
import com.leoschulmann.almi.entities.PagedResponse
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

                val rootDto: RootDto = transaction {
                    Root.new { value = newRoot }
                }.toDto()
                
                call.respond(HttpStatusCode.OK, rootDto)
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

            get {
                val id = call.parameters["id"]?.toLongOrNull();
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10

                if (page < 0 || size <= 0) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid page or size parameters")
                    return@get
                }

                if (id == null) {
                    val roots = transaction {
                        val roots = Root.all().limit(size).offset((page * size).toLong()).map { it.toDto() }
                        val count = Root.count()
                        PagedResponse(roots, page, size, count)
                    }
                    call.respond(HttpStatusCode.OK, roots)
                    return@get
                } else {
                    val root: Root? = transaction { Root.findById(id) }
                    if (root == null) {
                        call.respond(HttpStatusCode.NotFound, "Root entity not found")
                    } else {
                        call.respond(HttpStatusCode.OK, root.toDto())
                    }
                }
            }
        }
    }
}