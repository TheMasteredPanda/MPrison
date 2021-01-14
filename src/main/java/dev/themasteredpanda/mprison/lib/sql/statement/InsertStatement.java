package dev.themasteredpanda.mprison.lib.sql.statement;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import dev.themasteredpanda.mprison.lib.exception.SQLAlreadyExecutedException;
import dev.themasteredpanda.mprison.lib.sql.Database;
import dev.themasteredpanda.mprison.lib.sql.Model;
import dev.themasteredpanda.mprison.lib.util.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.NotImplementedException;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Statement builder for INSERT statements.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class InsertStatement implements Statement<Void>
{
    private final String tableName;
    private final LinkedList<String> columnNames;
    private final LinkedList<Object> values;
    private final Database database;
    private Consumer<Void> successConsumer;
    private Consumer<Throwable> failureConsumer;
    private ListenableFuture<Boolean> future;

    @Getter
    private boolean executed = false;

    public static Builder builder(Database database)
    {
        return new Builder(database);
    }

    public static class Builder implements Statement.Builder<InsertStatement, Builder>
    {
        private String tableName;
        private LinkedList<String> columnNames = Lists.newLinkedList();
        private LinkedList<Object> values = Lists.newLinkedList();
        private Database database;

        protected Builder(Database database)
        {
           this.database = database;
        }

        @Override
        public Builder name(String tableName)
        {
            this.tableName = tableName;
            return this;
        }

        @Override
        public Builder columns(String... columnNames)
        {
            Collections.addAll(this.columnNames, columnNames);
            return this;
        }

        /**
         * The values to insert.
         *
         * @param values - Array of values.
         */
        public Builder values(Object... values)
        {
            Collections.addAll(this.values, values);
            return this;
        }

        @Override
        public InsertStatement build()
        {
            if (tableName == null) {
                throw new NullPointerException("Table name not set.");
            }

            if (this.values.size() == 0) {
                throw new NullPointerException("Values not set.");
            }

            return new InsertStatement(tableName, columnNames, values, database);
        }
    }


    @Override
    public Statement<Void> success(Consumer<Void> consumer)
    {
        this.successConsumer = consumer;
        return this;
    }

    @Override
    public Statement<Void> failure(Consumer<Throwable> consumer)
    {
        this.failureConsumer = consumer;
        return this;
    }

    @Override
    public Optional<Void> get()
    {
        return Try.wrap(() -> Optional.of(future.get()));
    }

    @Override
    public Statement<Void> serialize(Model model)
    {
        throw new NotImplementedException("Method not applicable to an insert statement.");
    }

    @Override
    public Statement<Void> execute() throws SQLAlreadyExecutedException
    {
        if (executed) throw new SQLAlreadyExecutedException();
        executed = true;
        future = database.getService().submit(() -> {
            try (Connection connection = database.getConnectionPool().getConnection()) {

                StringBuilder sb = new StringBuilder("INSERT INTO '").append(tableName).append("' ");

                if (columnNames.size() > 0) {
                    StringBuilder columnSection = new StringBuilder("(");

                    for (String column : columnNames) {
                        columnSection.append(column);

                        if (!column.equals(columnNames.getLast())) {
                            columnSection.append(", ");
                        }
                    }

                    sb.append(columnSection.append(") ").toString());
                }

                PreparedStatement statement = connection.prepareStatement(sb.append("VALUES (").append(Stream.of(values).map((value) -> "?").collect(Collectors.joining(", "))).append(");").toString());

                for (int i = 0; i < values.size(); i++) {
                    statement.setObject(i + 1, values.get(i));
                }

                statement.executeUpdate();
                return true;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        });

        Futures.addCallback(future, new FutureCallback<>()
        {
            @Override
            public void onSuccess(@Nullable Boolean aBoolean)
            {
               if (successConsumer != null) successConsumer.accept(null);
            }

            @Override
            public void onFailure(Throwable throwable)
            {
                if (failureConsumer != null) failureConsumer.accept(throwable);
            }
        });

        return this;
    }
}
