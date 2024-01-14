package cz.lukynka.spotify

import LogType
import log
import se.michaelthelin.spotify.SpotifyApi
import se.michaelthelin.spotify.SpotifyHttpManager
import se.michaelthelin.spotify.enums.AuthorizationScope
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest
import util.Environment

object SpotifyAuth {
    private lateinit var codeGrant: String
    var authenticated = false

    var spotifyApi: SpotifyApi = SpotifyApi.Builder()
        .setClientId(Environment.SPOTIFY_CLIENT_ID)
        .setClientSecret(Environment.SPOTIFY_CLIENT_SECRET)
        .setRedirectUri(SpotifyHttpManager.makeUri(Environment.SPOTIFY_REDIRECT_URL))
        .build()

    private var authorizationCodeUriRequest: AuthorizationCodeUriRequest = spotifyApi.authorizationCodePKCEUri(Environment.SPOTIFY_CODE_CHALLENGE)
        .scope(AuthorizationScope.USER_READ_PRIVATE, AuthorizationScope.USER_READ_CURRENTLY_PLAYING)
        .build()

    val authorizationURL: String
        get() {
            return authorizationCodeUriRequest.execute().toString()
        }

    fun setNewCodeGrant(code: String) {
        log("Successfully got code grant!", LogType.SUCCESS)
        codeGrant = code
        fetchTokenPair()
    }

    fun setNewTokenPair(accessToken: String, refreshToken: String, expiry: Int) {
        spotifyApi.accessToken = accessToken
        spotifyApi.refreshToken = refreshToken

        if(!authenticated) authenticated = true

        val displayAccessToken = if(Environment.IS_DEBUG) accessToken else "<hidden>"
        val displayRefreshToken = if(Environment.IS_DEBUG) refreshToken else "<hidden>"

        log("Successfully received new spotify token pair!", LogType.SUCCESS)
        log("   Access Token: $displayAccessToken", LogType.DEBUG)
        log("   Refresh Token: $displayRefreshToken", LogType.DEBUG)
        log("   Expires in: $expiry", LogType.DEBUG)

        SpotifyRefreshToken().startRefreshTokenTimer()
    }

    private fun fetchTokenPair() {
        log("Requesting spotify token pair..", LogType.NETWORK)
        val authorizationCodeRequest = spotifyApi.authorizationCodePKCE(codeGrant, Environment.SPOTIFY_CODE_VERIFIER).build()
        val authorizationCodeCodeUriRequest = authorizationCodeRequest.execute()

        val accessToken = authorizationCodeCodeUriRequest.accessToken
        val refreshToken = authorizationCodeCodeUriRequest.refreshToken
        val expiry = authorizationCodeCodeUriRequest.expiresIn
        setNewTokenPair(accessToken, refreshToken, expiry)
    }
}