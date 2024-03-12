package org.penakelex.database.extenstions

import java.time.LocalDate

/**
 * Gets age of person on current day, using this LocalDate as date of birth
 * @receiver [LocalDate] date of birth of the person
 * @return [Int] - age of the person
 * */
fun LocalDate.getAgeOfPersonToday(): Int {
    val todayDate = LocalDate.now()
    return if (todayDate.monthValue - this.monthValue > 0
        || todayDate.monthValue - this.monthValue == 0
        && todayDate.dayOfMonth - this.dayOfMonth >= 0
    ) todayDate.year - this.year
    else todayDate.year - this.year - 1
}