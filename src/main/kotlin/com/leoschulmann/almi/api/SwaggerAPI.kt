package com.leoschulmann.almi.api

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.SchemaGenerator
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorswaggerui.config.OperationsSort
import io.github.smiley4.ktorswaggerui.config.SwaggerUISyntaxHighlight
import io.github.smiley4.ktorswaggerui.config.TagSort
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.swaggerAPI() {

    install(OpenApi) {
        tags {
            tagGenerator = { url -> listOf(url.getOrNull(1)) }
        }
        schemas {
            generator = SchemaGenerator.kotlinx()
        }
    }

    routing {
        route("/api.json") {
            openApi()
        }
        route("/swagger-ui.html") {
            swaggerUI("/api.json") {
                tryItOutEnabled = true
                operationsSorter = OperationsSort.HTTP_METHOD
                tagsSorter = TagSort.ALPHANUMERICALLY
                syntaxHighlight = SwaggerUISyntaxHighlight.IDEA
                displayRequestDuration = true
                displayOperationId = true
                filter = true
            }
        }
    }
}
