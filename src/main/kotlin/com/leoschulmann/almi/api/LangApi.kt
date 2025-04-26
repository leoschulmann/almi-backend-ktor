package com.leoschulmann.almi.api

import com.leoschulmann.almi.enums.Lang
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.langApi() {
    routing {
        route("/api/lang") {
            get {
                call.respond(HttpStatusCode.OK, Lang.entries.associate { lang -> lang.name to lang.value })
            }
        }
    }
}