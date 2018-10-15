package au.org.ala.sc.services

import au.org.ala.sc.db.forReadOnlyTransaction
import org.jooq.Configuration


/**
 * Implementors of this type can participate in database transactions
 */
interface Transactionable<T> {

    val configuration: Configuration

    /**
     * Return a copy of this [Transactionable] that uses the given jOOQ [Configuration].  This configuration should also
     * be applied to all dependent services that might be interested in participating in a transaction.
     */
    fun withTransaction(configuration: Configuration): T

}

interface Transactional<T: Transactionable<T>> {
    val service: T

    fun <R> (T.() -> R).runInTransaction(readOnly: Boolean = false): R =
        (if (readOnly) service.configuration.forReadOnlyTransaction() else service.configuration).runInTransaction(this)

    private inline fun <R> Configuration.runInTransaction(crossinline f: T.() -> R): R {
        return this.dsl().transactionResult { txConfig ->
            return@transactionResult service.withTransaction(txConfig).f()
        }
    }
}