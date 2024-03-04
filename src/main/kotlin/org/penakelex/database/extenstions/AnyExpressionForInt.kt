@file:Suppress("UNCHECKED_CAST")

package org.penakelex.database.extenstions

import org.jetbrains.exposed.sql.*

class AnyOp(
    expr1: Expression<*>, expr2: Expression<Array<*>>
) : ComparisonOp(expr1, expr2, "ANY")

infix fun ExpressionWithColumnType<Array<Int>>.any(
    value: Int
): Op<Boolean> = AnyOp(QueryParameter(value, columnType), this as Expression<Array<*>>)