package dev.themasteredpanda.mprison;

import dev.themasteredpanda.mprison.command.MPrisonCommand;
import dev.themasteredpanda.mprison.lib.command.MCommand;
import dev.themasteredpanda.mprison.lib.config.ConfigFile;
import dev.themasteredpanda.mprison.lib.config.YamlConfiguration;
import dev.themasteredpanda.mprison.lib.util.Format;
import dev.themasteredpanda.mprison.prison.PrisonManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class MPrison extends JavaPlugin
{
    @Getter
    private ConfigFile locale;
    @Getter
    private ConfigFile mainConfig;
    @Getter
    private PrisonManager prisonManager;

    @Override
    public void onLoad()
    {
        locale = new ConfigFile(this, "locale.yml", getDataFolder(), YamlConfiguration.class);
        mainConfig = new ConfigFile(this, "config.yml", getDataFolder(), YamlConfiguration.class);
        locale.load();
        Format.setPrefix(locale.getConfig().getString("locale.prefix"));
        mainConfig.load();
    }

    @Override
    public void onEnable()
    {
        prisonManager = new PrisonManager(this);
        MCommand.register(new MPrisonCommand(this));
    }
}
