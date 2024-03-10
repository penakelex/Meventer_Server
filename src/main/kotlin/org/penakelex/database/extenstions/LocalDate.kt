package org.penakelex.database.extenstions

import java.time.LocalDate

fun LocalDate.getAgeOfPersonToday(): Int {
    val todayDate = LocalDate.now()
    return if (todayDate.monthValue - this.monthValue > 0
        || todayDate.monthValue - this.monthValue == 0
        && todayDate.dayOfMonth - this.dayOfMonth >= 0
    ) todayDate.year - this.year
    else todayDate.year - this.year - 1
}