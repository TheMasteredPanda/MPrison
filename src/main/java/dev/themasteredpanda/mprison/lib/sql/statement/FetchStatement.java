package dev.themasteredpanda.mprison.lib.sql.statement;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import dev.themasteredpanda.mprison.lib.exception.SQLAlreadyExecutedException;
import dev.themasteredpanda.mprison.lib.sql.Database;
import dev.themasteredpanda.mprison.lib.sql.Model;
import dev.themasteredpanda.mprison.lib.util.GenericUtil;
import dev.themasteredpanda.mprison.lib.util.SQLUtil;
import dev.themasteredpanda.mprison.lib.util.Try;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FetchStatement<V> implements Statement<V>
{
    private final String tableName;
    private final LinkedList<String> columns;
    private final HashMap<String, Object> conditions;
    private final Database database;
    private Consumer<V> successConsumer;
    private Consumer<Throwable> failureConsumer;
    private ListenableFuture<ResultSet> future;
    private Model model;
    private boolean executed = false;

    public static Builder builder(Database database)
    {
        return new Builder(database);
    }
    
    public static class Builder implements Statement.Builder<FetchStatement, Builder>
    {
        private String tableName;
        private LinkedList<String> columns = Lists.newLinkedList();
        private HashMap<String, Object> conditions = Maps.newHashMap();
        private Database database;
        private int selectLimit = -0;

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
            Collections.addAll(columns, columnNames);
            return null;
        }

        public Builder where(String columnName, Object value)
        {
            if (this.conditions.containsKey(columnName)) {
                return this;
            }

            this.conditions.put(columnName, value);
            return this;
        }

        public Builder limit(int limit)
        {
            this.selectLimit = limit;
            return this;
        }

        @Override
        public FetchStatement build()
        {
            if (tableName == null) {
                throw new NullPointerException("Table name cannot be null.");
            }

            return new FetchStatement(tableName, columns, conditions, database);
        }
    }

    @Override
    public Statement<V> success(Consumer<V> consumer)
    {
        successConsumer = consumer;
        return this;
    }

    @Override
    public Statement<V> failure(Consumer<Throwable> consumer)
    {
        failureConsumer = consumer;
        return null;
    }

    @Override
    public Optional<V> get()
    {
        if (this.future == null) return Optional.empty();
        if (this.model == null) {
            return Try.wrap(() -> {
                ResultSet set = this.future.get();
                if (set == null) return Optional.empty();
                return Optional.of(set);
            });
        } else {
            return Try.wrap(() ->{
                ResultSet set = this.future.get();
                if (set == null) return Optional.empty();

                if (SQLUtil.count(set) > 0) {
                    LinkedList<Model> entries = Lists.newLinkedList();

                    while (set.next()) {
                        entries.add(model.serialize(set));
                    }

                    return Optional.of(entries);
                } else {
                    return Optional.of(model.serialize(set));
                }
            });
        }
    }

    @Override
    public Statement<V> serialize(Model model)
    {
        this.model = model;
        return this;
    }

    @Override
    public Statement<V> execute() throws SQLAlreadyExecutedException
    {
        if (executed) throw new SQLAlreadyExecutedException();
        executed = true;
        this.future = database.getService().submit(() -> {
            try (Connection connection = database.getConnectionPool().getConnection()) {
                StringBuilder sb = new StringBuilder("SELECT ");

                if (columns.size() == 0) {
                    sb.append("*");
                } else {
                    sb.append(Joiner.on(',').join(columns));
                }

                sb.append(" FROM ").append(tableName);

                if (conditions.size() > 0) {
                    sb.append(" WHERE ");
                    LinkedList<String> conditionSections = Lists.newLinkedList();

                   for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                       conditionSections.add(entry.getValue() + " = " + entry.getValue().toString());
                   }

                   sb.append(Joiner.on(" AND ").join(conditionSections));
                }

                PreparedStatement statement = connection.prepareStatement(sb.append(";").toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                return statement.executeQuery();
            }
        });

        Futures.addCallback(future, new FutureCallback<>()
        {
            @Override
            public void onSuccess(@Nullable ResultSet resultSet)
            {
                if (successConsumer == null) return;

                if (model == null) {
                   if (SQLUtil.count(resultSet) > 0) {
                       List<Model> entries = Lists.newLinkedList();

                       Try.wrap(() -> {
                           while (Objects.requireNonNull(resultSet).next()) {
                               entries.add(model.serialize(resultSet));
                           }

                           return null;
                       });

                       successConsumer.accept(GenericUtil.cast(entries));
                   } else {
                       successConsumer.accept(GenericUtil.cast(model.serialize(resultSet)));
                   }
                } else {
                   successConsumer.accept(GenericUtil.cast(resultSet));
                }
            }

            @Override
            public void onFailure(Throwable throwable)
            {
                if (failureConsumer != null) failureConsumer.accept(throwable);
            }
        });
        return null;
    }
}
