package com.leoschulmann.almi.api

import com.leoschulmann.almi.domain.Binyan
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.binyanApi() {
    routing {
        route("/api/binyan") {
            post {
                val newBinyan = call.receiveText() // validate

                val binyan = transaction {
                    Binyan.new { value = newBinyan }
                }

                call.respond(HttpStatusCode.OK, binyan.id.value)
            }

            put {
                val id = call.parameters["id"]?.toLongOrNull()
                val newValue = call.receiveText()

                if (id == null || newValue.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID or value")
                    return@put
                }

                val binyan: Binyan? = transaction { Binyan.findById(id) }

                if (binyan == null) {
                    call.respond(HttpStatusCode.NotFound, "Binyan entity not found")
                    return@put
                }

                val updatedBinyan = transaction {
                    binyan.apply {
                        value = newValue
                        version += 1
                    }
                }

                call.respond(HttpStatusCode.OK, updatedBinyan.id.value)
            }

            get {
                val id = call.parameters["id"]?.toLongOrNull()
                if (id == null) {
                    val all = transaction { Binyan.all().map { it.toBinyanDto() } }
                    call.respond(HttpStatusCode.OK, all)
                    return@get
                }

                val binyan: Binyan? = transaction { Binyan.findById(id) }

                if (binyan == null) {
                    call.respond(HttpStatusCode.NotFound, "Binyan entity not found")
                    return@get
                }

                call.respond(HttpStatusCode.OK, binyan.toBinyanDto())
            }
        }
    }
}