package dev.themasteredpanda.mprison.lib.sql.statement;

import dev.themasteredpanda.mprison.lib.exception.SQLAlreadyExecutedException;
import dev.themasteredpanda.mprison.lib.sql.Model;

import java.util.Optional;
import java.util.function.Consumer;

public interface Statement<V>
{
    /**
     *
     * @param consumer - Consumer executed once the statement has been
     *                 successfully executed.
     */
    Statement<V> success(Consumer<V> consumer);

    /**
     * @param consumer - Consumer executed once the statement has
     *                 failed in execution.
     */
    Statement<V> failure(Consumer<Throwable> consumer);

    /**
     * Fetches the value from the future.
     * @return a value wrapped in an optional container.
     */
    Optional<V> get();

    /**
     * Used in statements to serialize result sets into objects
     * via a model. If the ResultSet contains multiple rows
     * the statement will iterate through the rows and attempt
     * to serialize each row, then return a list of models each
     * representing one row.
     *
     * @param model - The model to serialize a ResultSet into.
     */
    Statement<V> serialize(Model model);

    /**
     *
     * @return
     * @throws SQLAlreadyExecutedException
     */
    Statement<V> execute() throws SQLAlreadyExecutedException;

    interface Builder<S extends Statement, B extends Builder>
    {
        /**
         * The name of the table the statement will execute on.
         *
         * @param tableName - the table name
         */
        B name(String tableName);

        B columns(String... columnNames);

        /**
         * Builds the statement.
         * @return a statement object.
         */
        S build();
    }
}
