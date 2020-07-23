package dave.dev.krawler.infra.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSuccess
import org.junit.jupiter.api.Test
import kotlin.random.Random

class GzipUtilTest {

    private val sut = GzipUtil

    @Test
    fun gzipAndUngzip() {

        repeat(100) {

            val randomBytes = Random.nextBytes(20)

            assertThat {
                GzipUtil.gzip(randomBytes)
            }.isSuccess().transform {
                GzipUtil.ungzip(it)
            }.isEqualTo(randomBytes)
        }
    }
}
