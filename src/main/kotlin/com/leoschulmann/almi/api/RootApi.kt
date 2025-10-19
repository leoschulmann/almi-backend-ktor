package com.leoschulmann.almi.api

import com.leoschulmann.almi.dbhelper.PagedResponse
import com.leoschulmann.almi.domain.Root
import com.leoschulmann.almi.domain.RootDto
import com.leoschulmann.almi.domain.UpdateRootDto
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.rootApi() {
    routing {
        route("/api/root") {
            createRoot()

            updateRoot()

            getRootList()

            getRootById()
        }
    }
}

private fun Route.getRootById() {
    get("{id}", {
        request { pathParameter<Long>("id", { required = true }) }
        response {
            code(HttpStatusCode.OK) { body<RootDto>() }
            code(HttpStatusCode.NotFound) { }
            code(HttpStatusCode.BadRequest) { }
        }
    }) {
        val id = call.parameters["id"]?.toLongOrNull();
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            return@get
        }

        val root: Root? = transaction { Root.findById(id) }
        if (root == null) {
            call.respond(HttpStatusCode.NotFound, "Root entity not found")
        } else {
            call.respond(HttpStatusCode.OK, root.toDto())
        }
    }
}

private fun Route.getRootList() {
    get({
        request {
            queryParameter<Int>("page", { required = true })
            queryParameter<Int>("size", { required = true })
        }
        response {
            code(HttpStatusCode.OK) { body<PagedResponse<RootDto>>() }
            code(HttpStatusCode.BadRequest) { }
        }
    }) {
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
        val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10

        if (page < 0 || size <= 0) {
            call.respond(HttpStatusCode.BadRequest, "Invalid page or size parameters")
            return@get
        }

        val roots = transaction {
            val roots = Root.all().limit(size).offset((page * size).toLong()).map { it.toDto() }
            val count = Root.count()
            PagedResponse(roots, page, size, count)
        }
        call.respond(HttpStatusCode.OK, roots)
        return@get
    }
}

private fun Route.updateRoot() {
    put({
        request { body<UpdateRootDto>() }
        response {
            code(HttpStatusCode.OK) { body<RootDto>() }
            code(HttpStatusCode.NotFound) { }
            code(HttpStatusCode.BadRequest) { }
        }
    }) {
        val (id, newValue) = call.receive<UpdateRootDto>()

        if (newValue.isBlank()) {
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

        call.respond(HttpStatusCode.OK, updatedRoot.toDto())
    }
}

private fun Route.createRoot() {
    post({
        request { body<String>() }
        response {
            code(HttpStatusCode.Created) { body<RootDto>() }
            code(HttpStatusCode.BadRequest) { }
        }
    }) {
        val newRoot = call.receiveText() // validate

        if (newRoot.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, "Invalid value")
            return@post
        }
        val rootDto: RootDto = transaction {
            Root.new { value = newRoot }
        }.toDto()

        call.respond(HttpStatusCode.Created, rootDto)
    }
}