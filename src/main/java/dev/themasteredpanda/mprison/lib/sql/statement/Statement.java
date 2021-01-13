package dev.themasteredpanda.mprison.lib.sql.statement;

import dev.themasteredpanda.mprison.lib.exception.SQLAlreadyExecutedException;
import dev.themasteredpanda.mprison.lib.sql.Model;

import java.util.Optional;
import java.util.function.Consumer;

public interface Statement<V>
{
    Statement<V> success(Consumer<V> consumer);

    Statement<V> failure(Consumer<Throwable> consumer);

    Optional<V> get();

    Statement<V> serialize(Model model);

    Statement<V> execute() throws SQLAlreadyExecutedException;

    interface Builder<S extends Statement, B extends Builder>
    {
        B name(String tableName);

        B columns(String... columnNames);

        S build();
    }
}
