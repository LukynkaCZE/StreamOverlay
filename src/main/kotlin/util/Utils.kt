package util

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import log

fun getResourceAsText(path: String): String {
    return try {
        object {}.javaClass.getResource(path)!!.readText()
    } catch (err: Exception) {
        log(err)
        object {}.javaClass.getResource(path)!!.readText()
    }
}

suspend fun ApplicationCall.respondHTML(text: String) {
    this.respondText(text, ContentType.Text.Html)
}