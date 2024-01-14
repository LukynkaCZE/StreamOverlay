package util

import LogType
import io.github.cdimascio.dotenv.dotenv
import log

object Environment {
    private val dotenv = dotenv()

    val PORT: Int = dotenv["PORT"].toInt()
    val HOST: String = dotenv["HOST"] as String
    val SPOTIFY_CLIENT_ID: String = dotenv["SPOTIFY_CLIENT_ID"] as String
    val SPOTIFY_CLIENT_SECRET: String = dotenv["SPOTIFY_CLIENT_SECRET"] as String
    val SPOTIFY_REFRESH_TOKEN_RATE: Long = dotenv["SPOTIFY_REFRESH_TOKEN_RATE"].toLong()
    val SPOTIFY_REDIRECT_URL: String = dotenv["SPOTIFY_REDIRECT_URL"] as String
    val SPOTIFY_CODE_CHALLENGE: String = dotenv["SPOTIFY_CODE_CHALLENGE"] as String
    val SPOTIFY_CODE_VERIFIER: String = dotenv["SPOTIFY_CODE_VERIFIER"] as String
    val IS_DEBUG: Boolean = dotenv["IS_DEBUG"].toBoolean()

    init {
        log("Loaded environment variables!", LogType.DEBUG)
    }
}