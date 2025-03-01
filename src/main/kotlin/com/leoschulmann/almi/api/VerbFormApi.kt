package com.leoschulmann.almi.api

import com.leoschulmann.almi.dto.CreateVerbFormDto
import com.leoschulmann.almi.entities.Verb
import com.leoschulmann.almi.entities.VerbForm
import com.leoschulmann.almi.entities.VerbFormTransliteration
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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
                    }
                }
                call.respond(HttpStatusCode.OK, verbForm.id.value)
            }
        }
    }
}