package com.leoschulmann.almi.api

import com.leoschulmann.almi.domain.*
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.put
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
        route("/api/vform") {
            createVerbForm()

            updateVerbForm()

            fetchVerbForms()
        }
    }
}

private fun Route.fetchVerbForms() {
    get("{verbId}", {
        request { pathParameter<Long>("verbId") { required = true } }
        response {
            code(HttpStatusCode.OK) { body<List<VerbFormDto>>() }
            code(HttpStatusCode.BadRequest) { }
        }
    }) {
        val verbId = call.parameters["verbId"]?.toLongOrNull()
        if (verbId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid verb ID")
            return@get
        }

        val forms = transaction {
            Verb.findById(verbId)?.load(Verb::forms)?.forms?.with(VerbForm::transliterations)?.map { it.toDto() }
                ?: emptyList()
        }

        call.respond(HttpStatusCode.OK, forms)
    }
}

private fun Route.updateVerbForm() {
    put({
        request { body<UpdateVerbFormDto>() }
        response {
            code(HttpStatusCode.OK) { body<VerbFormDto>() }
            code(HttpStatusCode.NotFound) { }
            code(HttpStatusCode.Conflict) { }
        }
    }) {
        val dto = call.receive<UpdateVerbFormDto>()
        val toInsert = dto.upsertTransliterations.filter { it.id == null }
        val toUpdate = dto.upsertTransliterations.filter { it.id != null }

        val verbForm = transaction {
            VerbForm.findById(dto.id)?.load(VerbForm::transliterations)?.apply {}
        }

        if (verbForm == null) {
            call.respond(HttpStatusCode.NotFound, "Verb form not found")
            return@put
        }

        val updatedVerbFormDto = transaction {
            toInsert.forEach {
                verbForm.transliterations.plus(VerbFormTransliteration.new {
                    this.lang = it.lang
                    this.value = it.value
                    this.version = 0
                    this.verbForm = verbForm
                })
            }
            toUpdate.forEach { toupdatedto ->
                verbForm.transliterations.find { toupdatedto.id == it.id.value }?.let {
                    if (it.lang != toupdatedto.lang || it.value != toupdatedto.value) {
                        it.value = toupdatedto.value
                        it.lang = toupdatedto.lang
                        it.version += 1
                    }
                }
            }
            if (verbForm.value != dto.value) {
                verbForm.value = dto.value
                verbForm.version += 1
            }
            return@transaction verbForm.toDto()
        }
        call.respond(HttpStatusCode.OK, updatedVerbFormDto)
    }
}

private fun Route.createVerbForm() {
    post({
        request { body<CreateVerbFormDto>() }
        response {
            code(HttpStatusCode.Created) { body<VerbFormDto>() }
            code(HttpStatusCode.BadRequest) { }
            code(HttpStatusCode.NotFound) { }
        }
    }) {

        val dto = call.receive<CreateVerbFormDto>()

        val verb = transaction { Verb.findById(dto.verbId) } ?: return@post call.respond(
            HttpStatusCode.NotFound, "Verb not found"
        )

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
        call.respond(HttpStatusCode.Created, verbForm)
    }
}