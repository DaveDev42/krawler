package dave.dev.krawler.infra.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZonedDateTime
import java.util.Date

interface DateTimeService {
    fun getNowDate(): Date
    fun getNowLocalDate(): LocalDate = getNowLocalDateTime().toLocalDate()
    fun getNowYearMonth(): YearMonth = YearMonth.from(getNowLocalDateTime())
    fun getNowLocalTime(): LocalTime = getNowLocalDateTime().toLocalTime()
    fun getNowLocalDateTime(): LocalDateTime = getNowZonedDateTime().toLocalDateTime()
    fun getNowZonedDateTime(): ZonedDateTime
}
