package com.leoschulmann.almi.api

import com.leoschulmann.almi.domain.Binyan
import com.leoschulmann.almi.domain.BinyanDto
import com.leoschulmann.almi.domain.ReqBinyanDto
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.binyanApi() {
    routing {
        route("/api/binyan") {

            post({
                request { body<String>() }
                response {
                    code(HttpStatusCode.Created) { body<BinyanDto>() }
                    code(HttpStatusCode.BadRequest) {}
                }
            }) {
                val newBinyan = call.receiveText() // validate

                if (newBinyan.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid value")
                    return@post
                }

                val binyan = transaction {
                    Binyan.new { value = newBinyan }
                }

                call.respond(HttpStatusCode.Created, binyan.toDto())
            }

            put({
                request { body<ReqBinyanDto>() }
                response {
                    code(HttpStatusCode.OK) { body<BinyanDto>() }
                    code(HttpStatusCode.NotFound) {}
                    code(HttpStatusCode.BadRequest) {}
                }
            }) {

                val (id, newValue) = call.receive<ReqBinyanDto>()


                if (newValue.isBlank()) {
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

            get({
                response { code(HttpStatusCode.OK) { body<List<BinyanDto>>() } }
            }) {
                val all = transaction { Binyan.all().map { it.toDto() } }
                call.respond(HttpStatusCode.OK, all)
                return@get
            }

            get("{id}", {
                request { pathParameter<Int>("id", { required = true }) }
                response {
                    code(HttpStatusCode.OK) { body<BinyanDto>() }
                    code(HttpStatusCode.NotFound) {}
                    code(HttpStatusCode.BadRequest) {}
                }
            }) {
                val id = call.parameters["id"]?.toLongOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                    return@get
                }

                val binyan: Binyan? = transaction { Binyan.findById(id) }

                if (binyan == null) {
                    call.respond(HttpStatusCode.NotFound, "Binyan entity not found")
                    return@get
                }

                call.respond(HttpStatusCode.OK, binyan.toDto())
            }
        }
    }
}