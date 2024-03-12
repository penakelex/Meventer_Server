package org.penakelex.database.extenstions

import org.jetbrains.exposed.sql.*

class ILikeOp(
    expr1: Expression<*>, expr2: Expression<*>
) : ComparisonOp(expr1, expr2, "ILIKE")

infix fun <T : String?> ExpressionWithColumnType<T>.iLike(
    pattern: String
): Op<Boolean> = ILikeOp(this, QueryParameter(pattern, columnType))