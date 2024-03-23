package cz.lukynka.routing

import LogType
import cz.lukynka.HTML
import cz.lukynka.Task
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import log
import util.Environment
import util.HTMLReplacer
import util.respondHTML

fun Route.taskRoute() {
    get("/task/set") {
        val task = call.request.queryParameters["task"].toString()
        if(task == "") throw Exception("task is empty")
        log("Set new task: $task", LogType.SUCCESS)
        Task.currentTask = task
        call.respond("done")
    }

    get("/task/clear") {
        Task.currentTask = ""
        log("Cleared Current Task!", LogType.WARNING)
        call.respond("done")
    }

    get("/task") {
        call.respond(Task.currentTask)
    }

    get("/task/currentTask") {
        val currentTaskPage = HTMLReplacer(HTML.CURRENT_TASK).replace(
            Pair("HOST", Environment.HOST),
            Pair("PORT", Environment.PORT.toString())
        )
        call.respondHTML(currentTaskPage)
    }
}