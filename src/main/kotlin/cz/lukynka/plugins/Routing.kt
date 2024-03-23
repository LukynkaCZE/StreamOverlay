package cz.lukynka.plugins

import cz.lukynka.HTML
import cz.lukynka.routing.spotifyRoute
import cz.lukynka.routing.taskRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import util.respondHTML

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
            cause.printStackTrace()
        }
    }
    routing {
        spotifyRoute()
        taskRoute()
        get("/") {
            call.respondHTML(HTML.YAHAHA)
        }
    }
}