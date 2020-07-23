package dave.dev.krawler.infra.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

fun getZoneId(): ZoneId = ZoneId.systemDefault()

fun Date.toLocalDateTime(): LocalDateTime = this.toInstant().atZone(getZoneId()).toLocalDateTime()

fun LocalDateTime.toDate(): Date = Date.from(this.atZone(getZoneId()).toInstant())

fun ZonedDateTime.toDate(): Date = Date.from(this.toInstant())

fun String.tryParse(formatter: DateTimeFormatter): LocalDateTime? = try {
    LocalDateTime.parse(this, formatter)
} catch (th: Throwable) {
    null
}

fun YearMonth.atStartOfMonth(): LocalDateTime =
    this.atDay(1).atStartOfDay()

fun LocalDate.atStartOfMonth(): LocalDateTime =
    YearMonth.from(this).atStartOfMonth()

fun LocalDateTime.atStartOfMonth(): LocalDateTime =
    YearMonth.from(this).atStartOfMonth()
