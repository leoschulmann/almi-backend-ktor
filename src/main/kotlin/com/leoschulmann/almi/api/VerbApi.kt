package com.leoschulmann.almi.api

import com.leoschulmann.almi.dbhelper.PagedResponse
import com.leoschulmann.almi.domain.*
import com.leoschulmann.almi.enums.GrammaticalGender
import com.leoschulmann.almi.enums.GrammaticalPerson
import com.leoschulmann.almi.enums.Plurality
import com.leoschulmann.almi.enums.Tense
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.dao.with
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

                val verbDto = transaction {
                    Verb.new {
                        this.value = createVerbDto.value
                        this.root = root
                        this.binyan = binyan
                    }.apply {
                        this.prepositions = prepositions
                        gizrahs = gizrahList
                        createVerbDto.translations.forEach { tr ->
                            
                            if (tr.id == -1L) {
                                translations.plus(
                                    VerbTranslation.new {
                                        this.lang = tr.lang
                                        this.value = tr.value
                                        this.verb = this@apply
                                    }
                                )
                            } else {
                                translations.find { it.id.value == tr.id }?.apply {
                                    value = tr.value
                                    lang = tr.lang
                                    version += 1
                                }
                            }
                        }

                        VerbForm.new { // creates default verb form (infinitive) with the verb
                            this.value = createVerbDto.value
                            this.tense = Tense.INFINITIVE
                            person = GrammaticalPerson.NONE
                            plurality = Plurality.NONE
                            gender = GrammaticalGender.NONE
                            this.verb = this@apply
                        }
                        
                    }.toFullDto()
                }

                call.respond(HttpStatusCode.Created, verbDto)
            }

            put {
                val dto = call.receive(UpdateVerbDto::class)

                val verb = transaction { Verb.findById(dto.id) }
                if (verb == null) {
                    call.respond(HttpStatusCode.NotFound, "Verb entity not found")
                    return@put
                }
                if (dto.version != verb.version) {
                    call.respond(HttpStatusCode.Conflict, "Version mismatch")
                }

                val res = transaction {
                    if (dto.value != verb.value) {
                        verb.value = dto.value
                        verb.version += 1
                    }

                    verb.toShortDto()
                }
                call.respond(HttpStatusCode.OK, res)
            }

            get {
                val id = call.parameters["id"]?.toLongOrNull()
                val rootId = call.parameters["rootId"]?.toLongOrNull()
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
                        verb?.toFullDto()
                    } ?: return@get call.respond(HttpStatusCode.NotFound, "Verb entity not found")
                    
                    call.respond(HttpStatusCode.OK, dto)
                    return@get
                } else if (rootId != null) {
                    val dtos: List<VerbShortDto> = transaction {
                        Verb.find { VerbTable.root eq rootId }
                            .with(Verb::translations)
                            .map { it.toShortDto() }
                    }
                    
                    return@get call.respond(HttpStatusCode.OK, dtos)
                } else {
                    val pagedResponse = transaction {
                        val dtos = Verb.all().limit(size).offset((page * size).toLong())
                            .map { it.load(Verb::gizrahs, Verb::prepositions, Verb::root, Verb::binyan) }
                            .map { it.toFullDto() }
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
                    verb.toFullDto()
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
                    }.verb.toFullDto()
                } ?: return@patch call.respond(HttpStatusCode.NotFound, "Verb translation entity not found")

                call.respond(HttpStatusCode.OK, verbDto)
            }
        }
    }
}