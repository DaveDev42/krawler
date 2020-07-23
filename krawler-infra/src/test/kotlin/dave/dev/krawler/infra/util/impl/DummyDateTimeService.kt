package dave.dev.krawler.infra.util.impl

import dave.dev.krawler.infra.util.DateTimeService
import dave.dev.krawler.infra.util.toDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date

object DummyDateTimeService : DateTimeService {

    private var now: ZonedDateTime = ZonedDateTime.now()

    fun setNow(ldt: LocalDateTime) {
        now = ldt.atZone(ZoneId.systemDefault())
    }

    fun setNow(zdt: ZonedDateTime) {
        now = zdt
    }

    override fun getNowDate(): Date = now.toLocalDateTime().toDate()

    override fun getNowZonedDateTime(): ZonedDateTime = now
}
