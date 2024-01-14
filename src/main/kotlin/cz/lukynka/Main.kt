package cz.lukynka

import LogType
import LoggerSettings
import ch.qos.logback.classic.LoggerContext
import util.Environment
import cz.lukynka.plugins.*
import cz.lukynka.spotify.SpotifyAuth
import cz.lukynka.spotify.SpotifyRefreshToken
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import log
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.util.logging.Logger

fun main() {
    LoggerSettings.saveToFile = false

    Environment

    log("Started Stream Overlay!", LogType.SUCCESS)
    log("Attempting to start ktor..", LogType.DEBUG)

    log("", LogType.NETWORK)
    log("Please login - ${SpotifyAuth.authorizationURL}", LogType.NETWORK)
    log("", LogType.NETWORK)

    val server = embeddedServer(Netty, port = Environment.PORT, host = Environment.HOST, module = Application::module)
    server.start(true)

}

fun Application.module() {

    val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).level = ch.qos.logback.classic.Level.ERROR

    configureTemplating()
    configureSerialization()
    configureHTTP()
    configureRouting()
}