package com.leoschulmann.almi.api

import com.leoschulmann.almi.dbhelper.PagedResponse
import com.leoschulmann.almi.dbhelper.VerbGizrahJointable
import com.leoschulmann.almi.domain.Gizrah
import com.leoschulmann.almi.domain.GizrahDto
import com.leoschulmann.almi.domain.ReqGizrahDto
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.gizrahApi() {
    routing {
        route("/api/gizrah") {
            post({
                request { body<String>() }
                response { code(HttpStatusCode.Created) { body<GizrahDto>() } }
            }) {
                val newGizrah = call.receiveText() // validate

                val gizrah = transaction {
                    Gizrah.new { value = newGizrah }
                }

                call.respond(HttpStatusCode.Created, gizrah.toDto())
            }

            put({
                request {
                    body<ReqGizrahDto>()
                }
                response {
                    code(HttpStatusCode.OK) { body<GizrahDto>() }
                    code(HttpStatusCode.NotFound) {}
                    code(HttpStatusCode.BadRequest) {}
                }
            }) {
                
                val (id, newval) = call.receive<ReqGizrahDto>()

                if (newval.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid value")
                    return@put
                }

                val gizrah: Gizrah? = transaction { Gizrah.findById(id) }

                if (gizrah == null) {
                    call.respond(HttpStatusCode.NotFound, "Gizrah entity not found")
                    return@put
                }

                val updatedGizrah = transaction {
                    gizrah.apply {
                        value = newval
                        version += 1
                    }
                }

                call.respond(HttpStatusCode.OK, updatedGizrah.toDto())
            }

            get({
                response {
                    code(HttpStatusCode.OK) {
                        body<List<GizrahDto>>()
                    }
                }
            }) {
                val gizrahs = transaction { Gizrah.all().map { gizrah: Gizrah -> gizrah.toDto() } }
                call.respond(HttpStatusCode.OK, gizrahs)
                return@get
            }

            get("{id}", {
                request {
                    pathParameter<Int>("id")
                }

                response {
                    code(HttpStatusCode.OK) {
                        body<GizrahDto>()
                    }
                    code(HttpStatusCode.NotFound) {}
                    code(HttpStatusCode.BadRequest) {}
                }
            }) {
                val id = call.parameters["id"]?.toLongOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID")
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
            
            route("/links") {
                get({
                    request {
                        queryParameter<Int>("page") { required = true }
                        queryParameter<Int>("size") { required = true }
                    }
                    response { code(HttpStatusCode.OK) { body<PagedResponse<VerbGizrahJointable>>() } }
                }) {

                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                    val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10

                    if (page < 0 || size <= 0) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid page or size parameters")
                        return@get
                    }

                    val links =
                        VerbGizrahJointable.getPaginatedGizrahLinks(size, (page * size).toLong())

                    val count = VerbGizrahJointable.countGizrahLinks()
                    val pagedResponse = PagedResponse(links, page, size, count)
                    call.respond(HttpStatusCode.OK, pagedResponse)
                }
            }
        }
    }
}