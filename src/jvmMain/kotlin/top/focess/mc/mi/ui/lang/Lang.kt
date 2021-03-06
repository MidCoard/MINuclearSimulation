package top.focess.mc.mi.ui.lang

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.ResourceLoader
import com.google.common.io.CharStreams
import top.focess.util.json.JSON
import java.io.InputStream
import java.io.InputStreamReader

@OptIn(ExperimentalComposeUiApi::class)
class Lang(inputStream: InputStream) {
    private val lang = JSON(CharStreams.toString(InputStreamReader(inputStream)))

    fun get(vararg keys: String): String {
        return try {
            var obj: JSON = lang
            repeat(keys.size - 1) {
                obj = obj.getSection(keys[it])
            }
            obj.get(keys[keys.size - 1]) as String
        } catch (e: Exception) {
            keys.joinToString(".")
        }
    }

    companion object {
        val default = Lang(ResourceLoader.Default.load("langs/zh-CN.json"))
        val zh_CN = default
        val en_US = Lang(ResourceLoader.Default.load("langs/en-US.json"))
    }

}