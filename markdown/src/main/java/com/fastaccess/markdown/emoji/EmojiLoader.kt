package com.fastaccess.markdown.emoji

import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

/**
 * Loads the emojis from a JSON database.
 *
 * @author Vincent DURMONT [vdurmont@gmail.com]
 */
internal object EmojiLoader {

    @Throws(IOException::class)
    fun loadEmojis(stream: InputStream): List<Emoji> {
        try {
            val emojisJSON = JSONArray(inputStreamToString(stream))
            val emojis = ArrayList<Emoji>(emojisJSON.length())
            for (i in 0 until emojisJSON.length()) {
                val emoji = buildEmojiFromJSON(emojisJSON.getJSONObject(i))
                if (emoji != null) {
                    emojis.add(emoji)
                }
            }
            return emojis
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return emptyList()
    }

    @Throws(IOException::class)
    private fun inputStreamToString(stream: InputStream): String {
        val sb = StringBuilder()
        val isr = InputStreamReader(stream, "UTF-8")
        val br = BufferedReader(isr)
        do {
            val read = br.readLine()
            sb.append(read)
        } while (read != null)
        br.close()
        return sb.toString()
    }

    @Throws(Exception::class)
    private fun buildEmojiFromJSON(json: JSONObject): Emoji? {
        if (!json.has("emoji")) {
            return null
        }
        val bytes = json.getString("emoji").toByteArray(charset("UTF-8"))
        var description: String? = null
        if (json.has("description")) {
            description = json.getString("description")
        }
        var supportsFitzpatrick = false
        if (json.has("supports_fitzpatrick")) {
            supportsFitzpatrick = json.getBoolean("supports_fitzpatrick")
        }
        val aliases = jsonArrayToStringList(json.getJSONArray("aliases"))
        val tags = jsonArrayToStringList(json.getJSONArray("tags"))
        return Emoji(description ?: "", supportsFitzpatrick, aliases, tags, *bytes)
    }

    private fun jsonArrayToStringList(array: JSONArray): List<String> {
        val strings = ArrayList<String>(array.length())
        try {
            for (i in 0 until array.length()) {
                strings.add(array.getString(i))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return strings
    }
}
