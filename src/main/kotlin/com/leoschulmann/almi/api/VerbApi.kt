package com.leoschulmann.almi.api

import com.leoschulmann.almi.dto.CreateVerbDto
import com.leoschulmann.almi.entities.*
import com.leoschulmann.almi.tables.GizrahTable
import com.leoschulmann.almi.tables.PrepositionTable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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
                        preposions = prepositions
                        gizrahs = gizrahList
                    }
                }

                call.respond(HttpStatusCode.Created, verb.id.value)
            }
        }
    }
}

