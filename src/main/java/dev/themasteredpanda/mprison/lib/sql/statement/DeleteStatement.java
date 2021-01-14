package dev.themasteredpanda.mprison.lib.sql.statement;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import dev.themasteredpanda.mprison.lib.exception.SQLAlreadyExecutedException;
import dev.themasteredpanda.mprison.lib.sql.Database;
import dev.themasteredpanda.mprison.lib.sql.Model;
import dev.themasteredpanda.mprison.lib.util.Try;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.NotImplementedException;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DeleteStatement implements Statement<Void>
{
    private final String tableName;
    private final HashMap<String, Object> conditions;
    private final Database database;
    private Consumer<Void> successConsumer = null;
    private Consumer<Throwable> failureConsumer = null;
    private ListenableFuture<Void> future;

    public static Builder builder(Database database)
    {
        return new Builder(database);
    }

    public static class Builder implements Statement.Builder<DeleteStatement, Builder>
    {
        private String tableName;
        private HashMap<String, Object> conditions = Maps.newHashMap();
        private Database database;

        private Builder(Database database)
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
            throw new NotImplementedException();
        }

        public Builder where(String columnName, Object value)
        {
            if (conditions.containsKey(columnName)) return this;
            conditions.put(columnName, value);
            return this;
        }

        @Override
        public DeleteStatement build()
        {
            return new DeleteStatement(tableName, conditions, database);
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
        throw new NotImplementedException();
    }

    @Override
    public Statement<Void> execute() throws SQLAlreadyExecutedException
    {
        this.future = database.getService().submit(() -> {
            StringBuilder sb = new StringBuilder("DELETE FROM '").append(tableName).append("' WHERE ").append(Joiner.on(" AND ").join(conditions.entrySet().stream().map(entry -> entry.getKey() + " = " + entry.getValue().toString()).collect(Collectors.toList()))).append(";");

            try (Connection connection = database.getConnectionPool().getConnection()) {
                PreparedStatement statement = connection.prepareStatement(sb.toString());
                statement.executeUpdate();
                return null;
            }
        });

        Futures.addCallback(future, new FutureCallback<Void>()
        {
            @Override
            public void onSuccess(@Nullable Void unused)
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
