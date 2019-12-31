package de.mrtnlhmnn.berlinapple.data

import de.mrtnlhmnn.berlinapple.BerlinappleConfig
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class DayRepo(val config: BerlinappleConfig) {
    private var days = mutableListOf<LocalDate>()

    init {
        val dateTimePattern = "yyyyMMdd'T'HHmm"
        val dateTimePatternFormatter = DateTimeFormatter.ofPattern(dateTimePattern)

        val berlinaleStartDay = LocalDateTime.parse(config.berlinaleStartDateTime, dateTimePatternFormatter).toLocalDate()
        val berlinaleEndDay   = LocalDateTime.parse(config.berlinaleEndDateTime,   dateTimePatternFormatter).toLocalDate()

        var day = berlinaleStartDay
        while (!day.isAfter(berlinaleEndDay)) {
            days.add(day)
            day = day.plusDays(1)
        }
    }

    fun getDaysAsStrings() = days.map {
        val dateTimePattern = "yyyy-MM-dd"
        val formatter = DateTimeFormatter.ofPattern(dateTimePattern)

        it.format(formatter)
    }.toList()
}
