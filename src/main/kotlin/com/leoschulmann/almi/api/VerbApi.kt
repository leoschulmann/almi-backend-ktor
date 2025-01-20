package com.leoschulmann.almi.api

import com.leoschulmann.almi.dto.CreateVerbDto
import com.leoschulmann.almi.dto.CreateVerbTranslationDto
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
                    ?: return@post call.respond(HttpStatusCode.NotFound, "Root entity not found")

                val binyan = transaction { Binyan.findById(createVerbDto.binyanId) }
                    ?: return@post call.respond(HttpStatusCode.NotFound, "Binyan entity not found")

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
                        val verb = Verb.findById(id)
                            ?.load(Verb::gizrahs, Verb::prepositions, Verb::root, Verb::binyan, Verb::translations)
                        verb?.toDto()
                    } ?: return@get call.respond(HttpStatusCode.NotFound, "Verb entity not found")
                    
                    call.respond(HttpStatusCode.OK, dto)
                    return@get
                } else {
                    val pagedResponse = transaction {
                        val dtos = Verb.all().limit(size).offset((page * size).toLong())
                            .map { it.load(Verb::gizrahs, Verb::prepositions, Verb::root, Verb::binyan) }
                            .map { it.toDto() }
                        val count = Verb.count()
                        PagedResponse(dtos, page, size, count)
                    }
                    call.respond(HttpStatusCode.OK, pagedResponse)
                }
            }
        }
        route("/api/verb/translate") {
            post {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid ID")

                val dto = call.receive(CreateVerbTranslationDto::class)

                val verb = transaction { Verb.findById(id)?.load(Verb::translations) }
                    ?: return@post call.respond(HttpStatusCode.NotFound, "Verb entity not found")

                val res = transaction {
                    verb.translations.plus(VerbTranslation.new {
                        this.verb = verb
                        this.version = 1
                        this.lang = dto.lang
                        this.value = dto.value
                    })
                    verb.toDto()
                }
                call.respond(HttpStatusCode.Created, res)
            }

            patch {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid translation ID")

                val dto = call.receive<CreateVerbTranslationDto>()

                val verbDto = transaction {
                    val verbTranslation = VerbTranslation.findById(id)
                        ?: return@transaction null

                    verbTranslation.apply {
                        lang = dto.lang
                        value = dto.value
                        version += 1
                    }.verb.toDto()
                } ?: return@patch call.respond(HttpStatusCode.NotFound, "Verb translation entity not found")

                call.respond(HttpStatusCode.OK, verbDto)
            }
        }
    }
}