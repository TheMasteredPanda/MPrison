package dev.themasteredpanda.mprison.lib.config;

import com.google.common.collect.Sets;
import dev.themasteredpanda.mprison.lib.util.Format;
import dev.themasteredpanda.mprison.lib.util.NumberUtil;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.List;

/**
 * Wrapper for configuration files.
 */
public class ConfigFile
{
    private JavaPlugin instance;
    private String name;
    private File file;
    private Configuration config;
    private Class<? extends ConfigurationProvider> provider;

    public ConfigFile(JavaPlugin instance, String name, File parent, Class<? extends ConfigurationProvider> provider)
    {
        this.instance = instance;
        this.name = name;
        this.file = new File(parent, name);
        this.provider = provider;
    }

    /**
     * Loads the configuration file. If the file does not exist outside of the jar it will create the file before loading.
     */
    @SneakyThrows
    public void load()
    {
        if (!instance.getDataFolder().exists()) {
            instance.getDataFolder().mkdir();
        }

        if (!file.exists()) {
            Files.write(file.toPath(), instance.getResource(name).readAllBytes());
        }

        this.config = ConfigurationProvider.getProvider(provider).load(file);
    }

    /**
     * Populates accessible fields with the annotation 'ConfigPopulate'.
     *
     * @param instance - The instance to populate.
     */
    @SneakyThrows
    public void populate(Object instance)
    {
        for (Field f : instance.getClass().getDeclaredFields()) {
            if (!f.isAnnotationPresent(ConfigPopulate.class)) {
                continue;
            }

            ConfigPopulate annotation = f.getAnnotation(ConfigPopulate.class);

            Object value = this.config.get(annotation.value());

            if (value instanceof String) {
                if (annotation.format()) {
                    value = Format.format((String) value);
                }

                if (annotation.colour()) {
                    value = Format.colour((String) value);
                }
            } else if (Sets.newHashSet(byte.class, short.class, int.class, double.class, float.class).contains(f.getType()) && (value instanceof String)) {
                value = NumberUtil.parse((String) value, f.getType());
            } else if (value instanceof List) {
               List<?> values = (List) value;

               if (values.size() != 0) {
                   if (values.get(0) instanceof String) {
                       if (annotation.colour()) {
                           Format.format((List<String>) values);
                       }

                       if (annotation.format()) {
                           Format.format((List<String>) values);
                       }
                   }
               }
            }

            f.set(instance, value);
        }
    }
}
