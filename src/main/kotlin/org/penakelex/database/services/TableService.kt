package org.penakelex.database.services

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

/**
 * Abstract table service with database query method
 * */
abstract class TableService {
    /**
     * Database query executor
     * @param query block to execute with database
     * */
    suspend fun <Type : Any> databaseQuery(query: suspend () -> Type) =
        newSuspendedTransaction(Dispatchers.IO) { query() }
}