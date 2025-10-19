package com.leoschulmann.almi.api

import com.leoschulmann.almi.dbhelper.PagedResponse
import com.leoschulmann.almi.domain.*
import com.leoschulmann.almi.enums.GrammaticalGender
import com.leoschulmann.almi.enums.GrammaticalPerson
import com.leoschulmann.almi.enums.Plurality
import com.leoschulmann.almi.enums.Tense
import io.github.smiley4.ktoropenapi.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.emptySized
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.verbApi() {
    routing {
        route("/api/verb") {
            createVerb()

            updateVerb()

            fetchVerbs()

            getVerbById()

            getVerbsByRootId()

            deleteVerb()
        }
        route("/api/verb/translate") {

            createVerbTranslation()

            updateVerbTranslation()

            deleteVerbTranslation()
        }
    }
}

private fun Route.deleteVerbTranslation() {
    delete("{id}", {
        request { pathParameter<Long>("id") { required = true } }
        response {
            code(HttpStatusCode.NoContent) { }
            code(HttpStatusCode.NotFound) { }
            code(HttpStatusCode.BadRequest) { }
        }
    }) {
        val id = call.parameters["id"]?.toLongOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid translation ID")
            return@delete
        }

        val verbTranslation = transaction { VerbTranslation.findById(id) }

        if (verbTranslation == null) {
            call.respond(HttpStatusCode.NotFound, "Verb translation entity not found")
            return@delete
        }

        transaction { verbTranslation.delete() }
        call.respond(HttpStatusCode.NoContent)
    }
}

private fun Route.updateVerbTranslation() {
    patch("{id}", {
        request {
            pathParameter<Long>("id") { required = true }
            body<CreateVerbTranslationDto>()
        }
        response {
            code(HttpStatusCode.OK) { body<VerbFullDto>() }
            code(HttpStatusCode.NotFound) { }
            code(HttpStatusCode.BadRequest) { }
        }
    }) {
        val id = call.parameters["id"]?.toLongOrNull()

        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid translation ID")
            return@patch
        }

        val (newVal, lang) = call.receive<CreateVerbTranslationDto>()

        val verbTranslation: VerbTranslation? = transaction {
            VerbTranslation.findById(id)
        }

        if (verbTranslation == null) {
            call.respond(HttpStatusCode.NotFound, "Verb translation entity not found")
            return@patch
        }

        val dto = transaction {
            verbTranslation.apply {
                this.lang = lang
                this.value = newVal
                this.version += 1
            }.verb.toFullDto()
        }

        call.respond(HttpStatusCode.OK, dto)
    }
}

private fun Route.createVerbTranslation() {
    post("{id}", {
        request {
            pathParameter<Long>("id") { required = true }
            body<CreateVerbTranslationDto>()
        }
        response {
            code(HttpStatusCode.Created) { body<VerbFullDto>() }
            code(HttpStatusCode.BadRequest) { }
        }
    }) {
        val id = call.parameters["id"]?.toLongOrNull()

        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            return@post
        }

        val (value, lang) = call.receive(CreateVerbTranslationDto::class)

        val verb = transaction { Verb.findById(id)?.load(Verb::translations) }

        if (verb == null) {
            call.respond(HttpStatusCode.NotFound, "Verb entity not found")
            return@post
        }

        val res = transaction {
            verb.translations.plus(VerbTranslation.new {
                this.verb = verb
                this.version = 0
                this.lang = lang
                this.value = value
            })
            verb.toFullDto()
        }
        call.respond(HttpStatusCode.Created, res)
    }
}

private fun Route.fetchVerbs() {
    get({
        request {
            queryParameter<Int>("page") { required = true }
            queryParameter<Int>("size") { required = true }
        }
        response {
            code(HttpStatusCode.OK) { body<PagedResponse<VerbFullDto>>() }
            code(HttpStatusCode.BadRequest) { }
        }
    }) {
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
        val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10

        if (page < 0 || size <= 0) {
            call.respond(HttpStatusCode.BadRequest, "Invalid page or size parameters")
            return@get
        }

        val pagedResponse = transaction {
            val dtos = Verb.all().limit(size).offset((page * size).toLong())
                .map { it.load(Verb::gizrahs, Verb::prepositions, Verb::root, Verb::binyan) }.map { it.toFullDto() }
            val count = Verb.count()
            PagedResponse(dtos, page, size, count)
        }
        call.respond(HttpStatusCode.OK, pagedResponse)
    }
}

private fun Route.getVerbById() {
    get("{id}", {
        request { pathParameter<Long>("id") { required = true } }
        response {
            code(HttpStatusCode.OK) { body<VerbFullDto>() }
            code(HttpStatusCode.NotFound) { }
            code(HttpStatusCode.BadRequest) { }
        }
    }) {
        val id = call.parameters["id"]?.toLongOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            return@get
        }
        val dto = transaction {
            val verb =
                Verb.findById(id)?.load(Verb::gizrahs, Verb::prepositions, Verb::root, Verb::binyan, Verb::translations)
            verb?.toFullDto()
        } ?: return@get call.respond(HttpStatusCode.NotFound, "Verb entity not found")

        call.respond(HttpStatusCode.OK, dto)
        return@get
    }
}

private fun Route.getVerbsByRootId() {
    get("root/{rootId}", {
        request { pathParameter<Long>("rootId") { required = true } }
        response {
            code(HttpStatusCode.OK) { body<List<VerbShortDto>>() }
            code(HttpStatusCode.NotFound) { }
        }
    }) {
        val rootId = call.parameters["rootId"]?.toLongOrNull()

        if (rootId != null) {
            val dtos: List<VerbShortDto> = transaction {
                Verb.find { VerbTable.root eq rootId }.with(Verb::translations).map { it.toShortDto() }
            }

            return@get call.respond(HttpStatusCode.OK, dtos)
        }
    }
}

private fun Route.updateVerb() {
    put({
        request { body<UpdateVerbDto>() }
        response {
            code(HttpStatusCode.OK) { body<VerbFullDto>() }
            code(HttpStatusCode.NotFound) { }
            code(HttpStatusCode.BadRequest) { }
        }
    }) {
        val (id, newValue) = call.receive(UpdateVerbDto::class)

        val verb = transaction { Verb.findById(id) }
        if (verb == null) {
            call.respond(HttpStatusCode.NotFound, "Verb entity not found")
            return@put
        }

        val res = transaction {
            verb.value = newValue
            verb.version += 1

            verb.toShortDto()
        }
        call.respond(HttpStatusCode.OK, res)
    }
}

private fun Route.createVerb() {
    post({
        request { body<CreateVerbDto>() }
        response {
            code(HttpStatusCode.Created) { body<VerbFullDto>() }
            code(HttpStatusCode.BadRequest) { }
        }
    }) {
        val createVerbDto = call.receive(CreateVerbDto::class)

        val root = transaction { Root.findById(createVerbDto.rootId) } ?: return@post call.respond(
            HttpStatusCode.NotFound,
            "Root entity not found"
        )

        val binyan = transaction { Binyan.findById(createVerbDto.binyanId) } ?: return@post call.respond(
            HttpStatusCode.NotFound,
            "Binyan entity not found"
        )

        val verbDto = transaction {
            Verb.new {
                this.value = createVerbDto.value
                this.root = root
                this.binyan = binyan
            }.apply {

                this.prepositions = Preposition.find { PrepositionTable.id inList createVerbDto.prepositionId }
                this.gizrahs = Gizrah.find { GizrahTable.id inList createVerbDto.gizrahId }

                createVerbDto.translations.forEach { tr ->
                    translations.plus(
                        VerbTranslation.new {
                            this.lang = tr.lang
                            this.value = tr.value
                            this.verb = this@apply
                        })
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
}

private fun Route.deleteVerb() {
    delete("{id}", {
        request { pathParameter<Long>("id") { required = true } }
        response {
            code(HttpStatusCode.NoContent) { }
            code(HttpStatusCode.NotFound) { }
            code(HttpStatusCode.BadRequest) { }
        }
    }) {
        val id = call.parameters["id"]?.toLongOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            return@delete
        }
        val verb = transaction { Verb.findById(id) }
        if (verb == null) {
            call.respond(HttpStatusCode.NotFound, "Verb entity not found")
            return@delete
        }
        transaction {
            verb.prepositions = emptySized()
            verb.gizrahs = emptySized()
            verb.translations.forEach { it.delete() }
            verb.forms.forEach { it.delete() }
            verb.delete()
        }
        call.respond(HttpStatusCode.NoContent)
    }
}