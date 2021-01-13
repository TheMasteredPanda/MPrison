package dev.themasteredpanda.mprison.lib.sql;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.zaxxer.hikari.HikariDataSource;
import dev.themasteredpanda.mprison.lib.config.ConfigFile;
import dev.themasteredpanda.mprison.lib.sql.statement.InsertStatement;
import dev.themasteredpanda.mprison.lib.util.Try;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executors;

@Getter
public class Database
{
    private JavaPlugin instance;
    private ConfigFile configFile;
    private ListeningExecutorService service;
    private HikariDataSource connectionPool;

    public Database(JavaPlugin instance, ConfigFile configFile)
    {
        this.instance = instance;
        this.configFile = configFile;
        service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(configFile.getConfig().getInt("sql.future_threadpool_size")));

        //Insert statement test.
        InsertStatement statement = (InsertStatement) InsertStatement.builder(this).name("test").columns("test1", "test2", "test3").values(1, 2, 3, "hello", "testing2", new Object()).build()
                .success((ignore) -> Bukkit.getLogger().info("Worked!")).failure((Throwable::printStackTrace));
        Try.wrap(statement::execute);
    }
}
