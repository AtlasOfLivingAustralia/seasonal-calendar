package org.jooq.impl;

import static org.jooq.impl.Tools.DataKey.DATA_DEFAULT_TRANSACTION_PROVIDER_AUTOCOMMIT;
import static org.jooq.impl.Tools.DataKey.DATA_DEFAULT_TRANSACTION_PROVIDER_CONNECTION;
import static org.jooq.impl.Tools.DataKey.DATA_DEFAULT_TRANSACTION_PROVIDER_SAVEPOINTS;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayDeque;
import java.util.Deque;

import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.TransactionContext;
import org.jooq.TransactionProvider;
import org.jooq.exception.DataAccessException;

/**
 * COPY AND PASTE OF THE DEFAULT jOOQ TRANSACTION PROVIDER WITH READ ONLY
 * TRANSACTIONS, DELETE WHEN JOOQ PROVIDES NATIVE READ ONLY TRANSACTIONS (3.12 according to their issue tracker)
 *
 * A read only implementation for the {@link TransactionProvider} SPI.
 * <p>
 * This implementation is entirely based on JDBC transactions and is intended to
 * work with {@link DefaultConnectionProvider} (which is implicitly created when
 * using {@link DSL#using(Connection)}).
 * <p>
 * <h3>Nesting of transactions</h3> By default, nested transactions are
 * supported by modeling them implicitly with JDBC {@link Savepoint}s, if
 * supported by the underlying JDBC driver, and if {@link #nested()} is
 * <code>true</code>. To deactivate nested transactions, use
 * {@link #ReadOnlyTransactionProvider(ConnectionProvider, boolean)}.
 *
 * @author Lukas Eder
 */
public class ReadOnlyTransactionProvider implements TransactionProvider {

    /**
     * This {@link Savepoint} serves as a marker for top level
     * transactions in dialects that do not support Savepoints.
     */
    private static final Savepoint UNSUPPORTED_SAVEPOINT = new DefaultSavepoint();

    /**
     * This {@link Savepoint} serves as a marker for top level
     * transactions if {@link #nested()} transactions are deactivated.
     */
    private static final Savepoint IGNORED_SAVEPOINT     = new DefaultSavepoint();

    private final ConnectionProvider connectionProvider;
    private final boolean            nested;

    public ReadOnlyTransactionProvider(ConnectionProvider connectionProvider) {
        this(connectionProvider, true);
    }

    /**
     * @param nested Whether nested transactions via {@link Savepoint}s are
     *            supported.
     */
    public ReadOnlyTransactionProvider(ConnectionProvider connectionProvider, boolean nested) {
        this.connectionProvider = connectionProvider;
        this.nested = nested;
    }

    public final boolean nested() {
        return nested;
    }

    final int nestingLevel(Configuration configuration) {
        return savepoints(configuration).size();
    }

    @SuppressWarnings("unchecked")
    private final Deque<Savepoint> savepoints(Configuration configuration) {
        Deque<Savepoint> savepoints = (Deque<Savepoint>) configuration.data(DATA_DEFAULT_TRANSACTION_PROVIDER_SAVEPOINTS);

        if (savepoints == null) {
            savepoints = new ArrayDeque<Savepoint>();
            configuration.data(DATA_DEFAULT_TRANSACTION_PROVIDER_SAVEPOINTS, savepoints);
        }

        return savepoints;
    }

    private final boolean autoCommit(Configuration configuration) {
        Boolean autoCommit = (Boolean) configuration.data(DATA_DEFAULT_TRANSACTION_PROVIDER_AUTOCOMMIT);

        if (autoCommit == null) {
            autoCommit = connection(configuration).getAutoCommit();
            configuration.data(DATA_DEFAULT_TRANSACTION_PROVIDER_AUTOCOMMIT, autoCommit);
        }

        return autoCommit;
    }

    private final boolean readOnly(Configuration configuration) {
        Boolean readOnly = (Boolean) configuration.data("READ_ONLY_TRANSACTION_PROVIDER_READONLY");

        if (readOnly == null) {
            readOnly = connection(configuration).isReadOnly();
            configuration.data("READ_ONLY_TRANSACTION_PROVIDER_READONLY", readOnly);
        }

        return readOnly;
    }

    private final DefaultConnectionProvider connection(Configuration configuration) {
        DefaultConnectionProvider connectionWrapper = (DefaultConnectionProvider) configuration.data(DATA_DEFAULT_TRANSACTION_PROVIDER_CONNECTION);

        if (connectionWrapper == null) {
            Connection connection = connectionProvider.acquire();
            connectionWrapper = new DefaultConnectionProvider(connection);
            configuration.data(DATA_DEFAULT_TRANSACTION_PROVIDER_CONNECTION, connectionWrapper);
        }

        return connectionWrapper;
    }

    @Override
    public final void begin(TransactionContext ctx) {
        Deque<Savepoint> savepoints = savepoints(ctx.configuration());

        // This is the top-level transaction
        boolean topLevel = savepoints.isEmpty();
        if (topLevel)
            brace(ctx.configuration(), true);

        Savepoint savepoint = setSavepoint(ctx.configuration(), topLevel);

        if (topLevel) {
            DefaultConnectionProvider conProv = connection(ctx.configuration());
            conProv.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            try {
                conProv.connection.createStatement().execute("set transaction_read_only = on;");
            } catch (SQLException e) {
                throw new DataAccessException("Can not set transaction_read_only = on");
            }
        }

        if (savepoint == UNSUPPORTED_SAVEPOINT && !topLevel)
            throw new DataAccessException("Cannot nest transactions because Savepoints are not supported");

        savepoints.push(savepoint);
    }

    private final Savepoint setSavepoint(Configuration configuration, boolean topLevel) {
        if (topLevel || !nested())
            return IGNORED_SAVEPOINT;

        switch (configuration.family()) {



            case CUBRID:
                return UNSUPPORTED_SAVEPOINT;
            default:
                return connection(configuration).setSavepoint();
        }
    }

    @Override
    public final void commit(TransactionContext ctx) {
        Deque<Savepoint> savepoints = savepoints(ctx.configuration());
        Savepoint savepoint = savepoints.pop();

        // [#3489] Explicitly release savepoints prior to commit
        if (savepoint != null && savepoint != UNSUPPORTED_SAVEPOINT && savepoint != IGNORED_SAVEPOINT)
            try {
                connection(ctx.configuration()).releaseSavepoint(savepoint);
            }

            // [#3537] Ignore those cases where the JDBC driver incompletely implements the API
            // See also http://stackoverflow.com/q/10667292/521799
            catch (DataAccessException ignore) {}

        // This is the top-level transaction
        if (savepoints.isEmpty()) {
            connection(ctx.configuration()).commit();
            brace(ctx.configuration(), false);
        }

        // Nested commits have no effect
        else {
        }
    }

    @Override
    public final void rollback(TransactionContext ctx) {
        Deque<Savepoint> savepoints = savepoints(ctx.configuration());
        Savepoint savepoint = null;

        // [#3537] If something went wrong with the savepoints per se
        if (!savepoints.isEmpty())
            savepoint = savepoints.pop();

        try {
            if (savepoint == null || savepoint == UNSUPPORTED_SAVEPOINT) {
                connection(ctx.configuration()).rollback();
            }

            // [#3955] ROLLBACK is only effective if an exception reaches the
            //         top-level transaction.
            else if (savepoint == IGNORED_SAVEPOINT) {
                if (savepoints.isEmpty())
                    connection(ctx.configuration()).rollback();
            }
            else {
                connection(ctx.configuration()).rollback(savepoint);
            }
        }

        finally {
            if (savepoints.isEmpty())
                brace(ctx.configuration(), false);
        }
    }

    /**
     * Ensure an <code>autoCommit</code> value on the connection, if it was set
     * to <code>true</code>, originally.
     */
    private final void brace(Configuration configuration, boolean start) {
        DefaultConnectionProvider connection = connection(configuration);

        try {
            boolean autoCommit = autoCommit(configuration);
            boolean readOnly = readOnly(configuration);

            // Transactions cannot run with autoCommit = true. Change the value for
            // the duration of a transaction
            if (autoCommit == true)
                connection.setAutoCommit(!start);

            if (start) {
                connection.setReadOnly(true);
            } else {
                connection.setReadOnly(readOnly);
            }
        }

        // [#3718] Chances are that the above JDBC interactions throw additional exceptions
        //         try-finally will ensure that the ConnectionProvider.release() call is made
        finally {
            if (!start) {
                connectionProvider.release(connection.connection);
                configuration.data().remove(DATA_DEFAULT_TRANSACTION_PROVIDER_CONNECTION);
            }
        }
    }

    private static class DefaultSavepoint implements Savepoint {
        @Override
        public int getSavepointId() throws SQLException {
            return 0;
        }

        @Override
        public String getSavepointName() throws SQLException {
            return null;
        }
    }
}