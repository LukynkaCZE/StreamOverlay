package cz.lukynka

import util.getResourceAsText

object HTML {
    val AUTO_CLOSE = getResourceAsText("/html/autoclose.html")
    val CURRENT_SONG = getResourceAsText("/html/song.html")
    val CURRENT_TASK = getResourceAsText("/html/task.html")
    val YAHAHA = getResourceAsText("/html/yahaha.html")
}