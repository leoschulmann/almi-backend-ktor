package com.leoschulmann.almi.api

import com.leoschulmann.almi.dto.CreateVerbFormDto
import com.leoschulmann.almi.dto.UpdateVerbFormDto
import com.leoschulmann.almi.dto.toDto
import com.leoschulmann.almi.entities.Verb
import com.leoschulmann.almi.entities.VerbForm
import com.leoschulmann.almi.entities.VerbFormTransliteration
import com.leoschulmann.almi.tables.VerbFormTranslitTable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.verbFormApi() {
    routing {
        route("/api/verb/form") {
            post {

                val dto = call.receive<CreateVerbFormDto>()

                val verb = transaction { Verb.findById(dto.verbId) }
                    ?: return@post call.respond(HttpStatusCode.NotFound, "Verb not found")

                val verbForm = transaction {
                    VerbForm.new {
                        value = dto.value
                        this.verb = verb
                        plurality = dto.pluralityGender.getPlurality()
                        gender = dto.pluralityGender.getGrammaticalGender()
                        person = dto.tenseAndPerson.getPerson()
                        tense = dto.tenseAndPerson.getTense()
                    }.apply {
                        dto.transliterations.forEach { (lang, value) ->
                            transliterations.plus(VerbFormTransliteration.new {
                                this.lang = lang
                                this.value = value
                                this.verbForm = this@apply
                            })
                        }
                    }.toDto()
                }
                call.respond(HttpStatusCode.OK, verbForm)
            }

            put {
                val dto = call.receive<UpdateVerbFormDto>()

                val entity = transaction {
                    VerbForm.findById(dto.id)
                } ?: return@put call.respond(HttpStatusCode.NotFound, "Verb form not found")

                if (dto.version != entity.version) {
                    return@put call.respond(HttpStatusCode.Conflict, "Version mismatch")
                }

                val transliterationVersionMap =
                    transaction {
                        VerbFormTransliteration.find { VerbFormTranslitTable.verbForm eq entity.id }
                            .associate { it.id.value to it.version }
                    }
                        

                if (!dto.updateTransliterations.all {
                        transliterationVersionMap.containsKey(it.id) && transliterationVersionMap[it.id] == it.version
                    }) {
                    return@put call.respond(HttpStatusCode.Conflict, "Transliterations version mismatch")
                }

                val verbFormDto = transaction {
                    if (dto.value != entity.value) {
                        entity.value = dto.value
                        entity.version += 1
                    }

                    dto.updateTransliterations.forEach { (id, version, value) ->
                        entity.transliterations.find { it.id.value == id }?.let {
                            if (it.value != value) {
                                it.value = value
                                it.version += 1
                            }
                        }
                    }

                    dto.createTransliterations.forEach { (lang, value) ->
                        entity.transliterations.plus(VerbFormTransliteration.new {
                            this.lang = lang
                            this.value = value
                            this.verbForm = entity
                        })
                    }

                    entity.toDto()
                }

                return@put call.respond(HttpStatusCode.OK, verbFormDto)
            }

            get {
                val verbId = call.parameters["verb_id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid verb ID")

                val forms = transaction {
                    Verb.findById(verbId)
                        ?.load(Verb::forms)
                        ?.forms
                        ?.with(VerbForm::transliterations)
                        ?.map { it.toDto() }
                        ?: emptyList()
                }

                call.respond(HttpStatusCode.OK, forms)
            }
        }
    }
}