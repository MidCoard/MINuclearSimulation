package top.focess.mc.mi.ui.lang

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.ResourceLoader
import com.google.common.io.CharStreams
import com.google.common.primitives.Bytes
import top.focess.util.json.JSON
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.file.Files
import java.util.*

@OptIn(ExperimentalComposeUiApi::class)
class Lang(inputStream :InputStream) {
    private val lang = JSON(CharStreams.toString(InputStreamReader(inputStream)))

    fun get(vararg keys: String): String {
        return try {
            var obj: JSON = lang
            repeat(keys.size - 1) {
                obj = obj.getSection(keys[it])
            }
            obj.get(keys[keys.size - 1]) as String
        } catch (e: Exception) {
            throw IllegalArgumentException(keys.contentToString())
        }
    }

    companion object {
        val default = Lang(ResourceLoader.Default.load("langs/zh-CN.json"))
        val zh_CN = default
        val en_US = Lang(ResourceLoader.Default.load("langs/en-US.json"))
    }

}