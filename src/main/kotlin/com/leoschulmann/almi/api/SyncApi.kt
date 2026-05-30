package com.leoschulmann.almi.api

import com.leoschulmann.almi.dbhelper.VerbGizrahJointable
import com.leoschulmann.almi.dbhelper.VerbGizrahJointable.gizrah
import com.leoschulmann.almi.dbhelper.VerbGizrahJointable.verb
import com.leoschulmann.almi.dbhelper.VerbGizrahLinkDto
import com.leoschulmann.almi.dbhelper.VerbPrepositionJointable
import com.leoschulmann.almi.dbhelper.VerbPrepositionJointable.preposition
import com.leoschulmann.almi.dbhelper.VerbPrepositionLinkDto
import com.leoschulmann.almi.domain.*
import io.github.smiley4.ktoropenapi.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.get
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.syncApi() {
    routing {
        route("/api/sync/simple") {
            get("/binyan") { call.respondRedirect("/api/binyan") }
            get("/prep") { call.respondRedirect("/api/preposition") }
            get("/gizrah") { call.respondRedirect("/api/gizrah") }

            getAllRoots()

            getAllVerbs()

            getAllVerbPrepositionLinks()

            getAllVerbGizrahLinks()

            getAllVerbFormsWithTranslits()
        
            getAllVerbFormExamples()
        }

    }
}

private fun Route.getAllVerbFormsWithTranslits() {
    get("/vform", {
        response { code(HttpStatusCode.OK) { body<List<VerbFormSyncDto>>() } }
    }) {
        val verbFormsWithTranslits = transaction {
            val verbForms = VerbFormTable.selectAll().map { it.mapRowToVFormSyncDto() }

            val map = VerbFormTranslitTable.selectAll().groupBy(
                keySelector = { it[VerbFormTranslitTable.verbForm].value },
                valueTransform = { it.mapRowToVFormTtanslitSyncDto() })

            verbForms.forEach { verbForm ->
                verbForm.transliterations.addAll(map[verbForm.id] ?: emptyList())
            }
            
            verbForms
        }

        call.respond(HttpStatusCode.OK, verbFormsWithTranslits)
    }
}

private fun Route.getAllRoots() {
    get(
        "/root", {
            response {
                code(HttpStatusCode.OK) { body<List<RootDto>>() }
                code(HttpStatusCode.BadRequest) { }
            }
        }) {

        call.respond(HttpStatusCode.OK, transaction {
            Root.all().map { it.toDto() }
        })
    }
}

private fun Route.getAllVerbs() {
    get(
        "/verb", {
            response {
                code(HttpStatusCode.OK) { body<VerbSyncDto>() }
                code(HttpStatusCode.BadRequest) { }
            }
        }) {

        call.respond(HttpStatusCode.OK, transaction { Verb.all().map { it.toSyncDto() } })
    }
}

private fun Route.getAllVerbPrepositionLinks() {
    get("/vb-pp", {
        response { code(HttpStatusCode.OK) { body<VerbPrepositionLinkDto>() } }
    }) {

        val links: List<VerbPrepositionLinkDto> = transaction {
            VerbPrepositionJointable.selectAll().map { it: ResultRow ->
                VerbPrepositionLinkDto(it[VerbPrepositionJointable.verb].value, it[preposition].value)
            }
        }
        call.respond(HttpStatusCode.OK, links)
    }
}

private fun Route.getAllVerbGizrahLinks() {
    get("/vb-gz", {
        response {
            code(HttpStatusCode.OK) {
                body<VerbPrepositionJointable>()
            }
        }
    }) {
        val dtos: List<VerbGizrahLinkDto> = transaction {
            VerbGizrahJointable.selectAll().map { VerbGizrahLinkDto(it[verb].value, it[gizrah].value) }
        }
        call.respond(HttpStatusCode.OK, dtos)
    }
}

private fun Route.getAllVerbFormExamples() {
    get("/vf-ex", {
        response { code(HttpStatusCode.OK) { body<List<VerbFormExampleSyncDto>>() } }
    }) {
        val examples = transaction {
            val examples = VerbFormExampleTable.selectAll().map { it.mapRowToExampleSyncDto() }

            val translationsByExampleId = VerbFormExampleTranslationTable.selectAll().groupBy(
                keySelector = { it[VerbFormExampleTranslationTable.example].value },
                valueTransform = { it.mapRowToExampleTr8nSyncDto() }
            )

            examples.forEach { example ->
                example.translations.addAll(translationsByExampleId[example.id] ?: emptyList())
            }

            examples
        }

        call.respond(HttpStatusCode.OK, examples)
    }
}