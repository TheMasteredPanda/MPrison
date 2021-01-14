package dev.themasteredpanda.mprison.lib.sql;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.zaxxer.hikari.HikariDataSource;
import dev.themasteredpanda.mprison.lib.config.ConfigFile;
import dev.themasteredpanda.mprison.lib.exception.SQLAlreadyExecutedException;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.concurrent.Executors;

@Getter
public class Database
{
    private JavaPlugin instance;
    private ConfigFile configFile;
    private ListeningExecutorService service;
    private HikariDataSource connectionPool;

    public Database(JavaPlugin instance, ConfigFile configFile) throws SQLAlreadyExecutedException
    {
        this.instance = instance;
        this.configFile = configFile;
        service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(configFile.getConfig().getInt("sql.future_threadpool_size")));

        //Insert statement.
        //InsertStatement statement = (InsertStatement) InsertStatement.builder(this).name("test").columns("test1", "test2", "test3").values(1, 2, 3, "hello", "testing2", new Object()).build()
        //        .success((ignore) -> Bukkit.getLogger().info("Worked!")).failure((Throwable::printStackTrace));
        //Try.wrap(statement::execute);

        //Update statement.
        //UpdateStatement statement1 = (UpdateStatement) UpdateStatement.builder(this).name("test").update("test33", 3).update("hello_four", new Object()).where("id", UUID.randomUUID().toString()).build()
        //        .success((ignore) -> Bukkit.getLogger().info("Success")).failure((t) -> Bukkit.getLogger().info(t.toString())).execute();

        //Delete statement.
        //DeleteStatement statement2 = (DeleteStatement) DeleteStatement.builder(this).name("test").where("id", UUID.randomUUID().toString()).build().execute();

        // Fetch (SELECT) statement
        // FetchStatement statement3 = (FetchStatement) FetchStatement.builder(this).name("test3").limit(5).where("test", UUID.randomUUID().toString()).build().execute();

        HikariDataSource source = new HikariDataSource();
        source.setJdbcUrl("jdbc: " + configFile.getConfig().getString("sql.driver") + "://" + configFile.getConfig().getString("sql.address") + ":" + configFile.getConfig().getString("sql.port") + "/" + configFile.getConfig().getString("sql.database"));
        Collection<String> hikariDataSettingsKeys = configFile.getConfig().getSection("sql.hikari_settings").getKeys();

        for (String key : hikariDataSettingsKeys) {
            source.addDataSourceProperty(key, configFile.getConfig().get("sql.hikari_settings." + key));
        }

        connectionPool = source;
    }

    public void close()
    {
        connectionPool.close();
    }
}
