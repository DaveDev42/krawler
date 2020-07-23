package dave.dev.krawler.infra.util

import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object GzipUtil {

    fun gzip(bytes: ByteArray): ByteArray = ByteArrayOutputStream()
        .use { bos ->
            GZIPOutputStream(bos).use { gos ->
                gos.write(bytes)
            }
            bos.toByteArray()
        }

    fun ungzip(content: ByteArray): ByteArray = GZIPInputStream(content.inputStream())
        .use {
            it.readBytes()
        }
}
