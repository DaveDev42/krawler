package dave.dev.krawler.cli

import mu.KotlinLogging
import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class KrawlerCliApplication

private val logger = KotlinLogging.logger { }

fun main(args: Array<String>) {
    logger.debug { "STARTING THE APPLICATION" }
    val app = SpringApplication(KrawlerCliApplication::class.java)
    app.webApplicationType = WebApplicationType.NONE
    app.run(*args)
    logger.debug { "APPLICATION FINISHED" }
}
