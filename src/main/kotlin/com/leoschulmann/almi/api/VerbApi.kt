package com.leoschulmann.almi.api

import com.leoschulmann.almi.dto.CreateVerbDto
import com.leoschulmann.almi.dto.toDto
import com.leoschulmann.almi.entities.*
import com.leoschulmann.almi.tables.GizrahTable
import com.leoschulmann.almi.tables.PrepositionTable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.verbApi() {
    routing {
        route("/api/verb") {
            post {
                val createVerbDto = call.receive(CreateVerbDto::class)

                val root = transaction { Root.findById(createVerbDto.rootId) }

                if (root == null) {
                    call.respond(HttpStatusCode.NotFound, "Root entity not found")
                    return@post
                }

                val binyan = transaction { Binyan.findById(createVerbDto.binyanId) }
                if (binyan == null) {
                    call.respond(HttpStatusCode.NotFound, "Binyan entity not found")
                    return@post
                }

                val gizrahList = transaction { Gizrah.find { GizrahTable.id inList createVerbDto.gizrahId } }
                val prepositions =
                    transaction { Preposition.find { PrepositionTable.id inList createVerbDto.prepositionId } }

                val verb: Verb = transaction {
                    Verb.new {
                        this.value = createVerbDto.value
                        this.root = root
                        this.binyan = binyan
                    }.apply {
                        this.prepositions = prepositions
                        gizrahs = gizrahList
                    }
                }

                call.respond(HttpStatusCode.Created, verb.id.value)
            }
            get {
                val id = call.parameters["id"]?.toLongOrNull()
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10

                if (page < 0 || size <= 0) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid page or size parameters")
                    return@get
                }

                if (id != null) {
                    val dto = transaction {
                        val verb = Verb.findById(id)?.load(Verb::gizrahs, Verb::prepositions, Verb::root, Verb::binyan)
                        verb?.toDto()
                    }
                    if (dto != null) {
                        call.respond(HttpStatusCode.OK, dto)
                        return@get
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Verb entity not found")
                        return@get
                    }
                } else {
                    val pagedResponse = transaction {
                        val dtos = Verb.all().limit(size).offset((page * size).toLong())
                            .map { it.load(Verb::gizrahs, Verb::prepositions, Verb::root, Verb::binyan) }
                            .map { it.toDto() }
                        val count = Verb.count()
                        PagedResponse(dtos, page, size, count)
                    }
                    call.respond(HttpStatusCode.OK, pagedResponse)
                    return@get
                }
            }
        }
    }
}