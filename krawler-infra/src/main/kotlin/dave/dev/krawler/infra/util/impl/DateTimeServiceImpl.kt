package dave.dev.krawler.infra.util.impl

import dave.dev.krawler.infra.util.DateTimeService
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import java.util.Date

@Service
class DateTimeServiceImpl : DateTimeService {
    override fun getNowDate() = Date()
    override fun getNowZonedDateTime(): ZonedDateTime = ZonedDateTime.now()
}
