package org.penakelex.database.services.events

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.orIfNotNull
import org.penakelex.database.extenstions.eqAny
import org.penakelex.database.extenstions.iLike
import org.penakelex.database.models.EventSelection
import org.penakelex.database.tables.Events
import java.time.Instant

fun EventSelection.getSelectExpression(): Op<Boolean> {
    var expression: Op<Boolean> = Events.start_time.greaterEq(Instant.now())
    if (tags == null
        && age == null
        && minimalPrice == null
        && maximalPrice == null
    ) return expression
    val expressions = listOfNotNull(
        getTagsExpression(),
        getAgeExpression(),
        getPriceExpression()
    )
    for (fieldExpression in expressions) expression = expression and fieldExpression
    return expression
}

private fun EventSelection.getTagsExpression(): Op<Boolean>? {
    if (tags == null) return null
    val firstTag = tags.first()
    var expression = Events.name.iLike("%${firstTag}%") or
            Events.description.iLike("%${firstTag}%") or
            Events.tags.eqAny(firstTag)
    for (i in 1..tags.lastIndex) expression = expression or
            Events.name.iLike("%${tags[i]}%") or
            Events.description.iLike("%${tags[i]}%") or
            Events.tags.eqAny(tags[i])
    return expression
}

private fun EventSelection.getAgeExpression(): Op<Boolean>? {
    if (age == null) return null
    return Events.minimal_age.lessEq(age) orIfNotNull
            Events.maximal_age.greaterEq(age)
}

private fun EventSelection.getPriceExpression(): Op<Boolean>? {
    val isMaxPriceNotNull = maximalPrice != null
    val isMinPriceNotNull = minimalPrice != null
    return when {
        !isMaxPriceNotNull && !isMinPriceNotNull -> null
        isMaxPriceNotNull && !isMinPriceNotNull -> {
            Events.price.lessEq(maximalPrice!!)
        }

        !isMaxPriceNotNull && isMinPriceNotNull -> {
            Events.price.greaterEq(minimalPrice!!)
        }

        else -> {
            Events.price.lessEq(maximalPrice!!) and
                    Events.price.greaterEq(minimalPrice!!)
        }
    }
}