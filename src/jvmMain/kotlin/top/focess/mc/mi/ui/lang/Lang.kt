package top.focess.mc.mi.ui.lang

import top.focess.util.json.JSONObject
import java.io.File
import java.nio.file.Files


class Lang (file :File) {
    private val lang:JSONObject = JSONObject.parse(Files.readAllLines(file.toPath()).joinToString("\n"))

    fun get(vararg keys: String): String {
        try {
            var obj: JSONObject = lang
            repeat(keys.size-1) {
                obj = obj.getJSON(keys[it])
            }
            return obj.get(keys[keys.size-1]) as String
        } catch (e: Exception) {
            return "error-key"
        }
        return "empty-key"
    }
}

fun I18n(key: String, vararg args: Any): String {
    return ""
}