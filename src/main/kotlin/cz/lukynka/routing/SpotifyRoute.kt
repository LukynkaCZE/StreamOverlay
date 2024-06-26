package cz.lukynka.routing

import cz.lukynka.HTML
import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import cz.lukynka.spotify.SpotifyAuth
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import se.michaelthelin.spotify.model_objects.specification.Track
import util.Environment
import util.HTMLReplacer
import util.respondHTML

var isOtographic = false

fun Route.spotifyRoute() {
    get("/spotify/callback") {
        val code = call.request.queryParameters["code"].toString()
        SpotifyAuth.setNewCodeGrant(code)
        call.respondHTML(HTML.AUTO_CLOSE)
    }

    get("/spotify/unknownimage") {
        call.respondBytes(object {}.javaClass.getResource("/unknown.jpg")!!.readBytes())
    }

    get("/spotify/otographic") {
        val bool = (call.request.queryParameters["enabled"] ?: "false").toBoolean()
        isOtographic = bool
        call.response.status(HttpStatusCode.OK)
        call.respond("")
    }

    get("/miku") {
        call.respondBytes(object {}.javaClass.getResource("/miku.png")!!.readBytes())
    }

    get("/api/spotify/currentSong") {

        if(isOtographic) {
            call.respond(CurrentSongResponse("Otographic Arts", "Kenji Sekiguchi & Nhato", "https://thumbnailer.mixcloud.com/unsafe/390x390/extaudio/9/d/1/b/a849-e223-4c89-9697-b61b94b4dd3a", false,
                isEmpty = false,
                currentMs = 0,
                maxMs = 0
            ))
            return@get
        }

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

            val artistName = if(track.artists.isNotEmpty()) track.artists[0].name else ""
            val imageURL = if(track.album.images.isNotEmpty()) track.album.images[0].url else "http://${Environment.HOST}:${Environment.PORT}/spotify/unknownimage"

            name = track.name
            artist = artistName
            image = imageURL
            isPaused = !currentlyPlaying.is_playing
            currentMs = currentlyPlaying.progress_ms
            maxMs = currentlyPlaying.item.durationMs
            call.respond(CurrentSongResponse(name, artist, image, isPaused, false, currentMs, maxMs))
        } catch (err: Exception) {
            if(err.message?.contains("because \"currentlyPlaying\" is null") == false) {
                log("Error occurred while fetching currently playing song: $err", LogType.ERROR)
                log(err)
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