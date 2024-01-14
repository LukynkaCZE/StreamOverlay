package cz.lukynka.routing

import LogType
import cz.lukynka.HTML
import cz.lukynka.spotify.SpotifyAuth
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import log
import se.michaelthelin.spotify.model_objects.specification.Track
import util.Environment
import util.HTMLReplacer
import util.respondHTML

fun Route.spotifyRoute() {
    get("/spotify/callback") {
        val code = call.request.queryParameters["code"].toString()
        SpotifyAuth.setNewCodeGrant(code)
        call.respondHTML(HTML.AUTO_CLOSE)
    }

    get("/api/spotify/currentSong") {

        if(!SpotifyAuth.authenticated) {
            call.respond(CurrentSongResponse("", "", "", isPaused = false, isEmpty = true, 0, 0))
            return@get
        }

        val name: String
        val artist: String
        val image: String
        val isPaused: Boolean
        val currentMs: Int
        val maxMs: Int
        try {
            val currentlyPlaying = SpotifyAuth.spotifyApi.usersCurrentlyPlayingTrack.build().execute()
            val track = currentlyPlaying.item as Track

            name = track.name
            artist = track.artists[0].name
            image = track.album.images[0].url
            isPaused = !currentlyPlaying.is_playing
            currentMs = currentlyPlaying.progress_ms
            maxMs = currentlyPlaying.item.durationMs
            call.respond(CurrentSongResponse(name, artist, image, isPaused, false, currentMs, maxMs))
        } catch (err: Exception) {
            if(err.message?.contains("because \"currentlyPlaying\" is null") == false) {
                log("Error occurred while fetching currently playing song: $err", LogType.ERROR)
            }
            call.respond(CurrentSongResponse("", "", "", isPaused = false, isEmpty = true, 0, 0))
        }
    }

    get("/spotify/currentSong") {
        val currentSongPage = HTMLReplacer(HTML.CURRENT_SONG).replace(
            Pair("HOST", Environment.HOST),
            Pair("PORT", Environment.PORT.toString())
        )
        call.respondHTML(currentSongPage)
    }
}

@Serializable
data class CurrentSongResponse(
    val name: String,
    val artist: String,
    val image: String,
    val isPaused: Boolean,
    val isEmpty: Boolean,
    val currentMs: Int,
    val maxMs: Int
)