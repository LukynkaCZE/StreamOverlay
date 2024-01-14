package util

class HTMLReplacer(val html: String) {
    fun replace(vararg pair: Pair<String, String>): String {
        var htmlOut = html
        pair.forEach { htmlOut = htmlOut.replace("{@${it.first}}", it.second) }
        return htmlOut
    }
}