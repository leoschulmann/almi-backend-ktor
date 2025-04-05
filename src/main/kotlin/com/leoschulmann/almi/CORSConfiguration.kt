package com.leoschulmann.almi

import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCORS() {
    install(CORS) {

        anyHost()
        allowNonSimpleContentTypes = true
//        allowHost("myotherdomain.com", schemes = listOf("https"))
//        allowMethod(HttpMethod.Get)
//        allowMethod(HttpMethod.Post)
//        allowMethod(HttpMethod.Put)
//        allowMethod(HttpMethod.Delete)
//        allowHeader(HttpHeaders.Authorization)
//        allowHeader(HttpHeaders.ContentType)

    }
}