package cz.lukynka.spotify

import LogType
import log
import util.Environment
import java.util.*

class SpotifyRefreshToken {

    private val timer = Timer("SpotifyRefreshTokenTimerThread")
    private val task: TimerTask = object : TimerTask() {
        override fun run() {
            log("Requesting new access & refresh token..", LogType.NETWORK)
            try {
                val authorizationCodeRefreshRequest = SpotifyAuth.spotifyApi.authorizationCodePKCERefresh()
                    .client_id(Environment.SPOTIFY_CLIENT_ID)
                    .grant_type("refresh_token")
                    .build()
                val authorizationCodeCredentials = authorizationCodeRefreshRequest.execute()

                val accessToken = authorizationCodeCredentials.accessToken
                val refreshToken = authorizationCodeCredentials.refreshToken
                val expiry = authorizationCodeCredentials.expiresIn

                SpotifyAuth.setNewTokenPair(accessToken, refreshToken, expiry)
                this.cancel()
            } catch (err: Exception) {
                log("Failed to refresh token, trying again..", LogType.ERROR)
                SpotifyRefreshToken().startRefreshTokenTimer()
                this.cancel()
            }
        }
    }

    fun startRefreshTokenTimer() {
        timer.purge()
        val period = Environment.SPOTIFY_REFRESH_TOKEN_RATE
        timer.schedule(task, period, 1)
        log("Started Spotify Refresh Token Timer for ${period / 1000}s", LogType.RUNTIME)
    }
}