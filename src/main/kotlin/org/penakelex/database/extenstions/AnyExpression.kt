@file:Suppress("UNCHECKED_CAST")

package org.penakelex.database.extenstions

import org.jetbrains.exposed.sql.*
import kotlin.jvm.internal.Intrinsics

class AnyOp(
    expr1: Expression<*>, expr2: Expression<Array<*>>
) : ComparisonOp(expr1, expr2, "= ANY") {
    override fun toQueryBuilder(queryBuilder: QueryBuilder) {
        Intrinsics.checkNotNullParameter(queryBuilder, "queryBuilder")
        queryBuilder.invoke {
            append(expr1)
            append("$opSign(")
            append(expr2)
            append(")")
        }
    }
}

infix fun <Type> ExpressionWithColumnType<Array<Type>>.eqAny(
    value: Type
): Op<Boolean> = AnyOp(QueryParameter(value, columnType), this as Expression<Array<*>>)

/*
infix fun ExpressionWithColumnType<Array<Int>?>.eqAny(
    value: Int
): Op<Boolean> = AnyOp(QueryParameter(value, columnType), this as Expression<Array<*>>)*/
