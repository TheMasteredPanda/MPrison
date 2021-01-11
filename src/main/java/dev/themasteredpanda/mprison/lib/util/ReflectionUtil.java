package dev.themasteredpanda.mprison.lib.util;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Utility class for reflection of NMS and OBC classes.
 */
public class ReflectionUtil
{
    private static String NMS = "net.minecraft.server.";
    private static String OBC = "org.bukkit.craftbukkit.";

    public static String getVersion()
    {
        String pkg = Bukkit.getServer().getClass().getPackage().getName();
        return pkg.substring(pkg.lastIndexOf('.') - 1);
    }

    public static String getNMSClass(String name)
    {
        return Try.wrap(() -> Class.forName(NMS + getVersion() + "." + name));
    }

    public static String getOBCClass(String name)
    {
        return Try.wrap(() -> Class.forName(OBC + getVersion() + "." + name));
    }

    public static Method getMethod(Class<?> clazz, String name, boolean declared, Class<?>... parameters)
    {
        return Try.wrap(() -> {
            Method m = declared ? clazz.getDeclaredMethod(name, parameters) : clazz.getMethod(name, parameters);
            m.setAccessible(true);
            return m;
        });
    }

    public static Field getField(Class<?> clazz, String name, boolean declared)
    {
        return Try.wrap(() -> {
            Field f = declared ? clazz.getDeclaredField(name) : clazz.getField(name);
            f.setAccessible(true);
            return f;
        });
    }

    public static <V> V getFieldValue(Object instance, Field f)
    {
        f.setAccessible(true);
        return Try.wrap(() -> f.get(instance));
    }

    public static Constructor<?> getConstructor(Class<?> clazz, boolean declared, Class<?>... parameters)
    {
        return Try.wrap(() -> {
            Constructor<?> c = declared ? clazz.getDeclaredConstructor(parameters) : clazz.getConstructor(parameters);
            c.setAccessible(true);
            return c;
        });
    }

    public static void set(Field f, Object instance, Object value)
    {
        Try.wrap(() -> {
            f.set(instance, value);
            return null;
        });
    }

    public static <V> V invoke(Method m, Object instance, Object... parameters)
    {
       return Try.wrap(() -> m.invoke(instance, parameters));
    }

    public static <V> V newInstance(Constructor<?> c, Object... parameters)
    {
        return Try.wrap(() -> c.newInstance(parameters));
    }
}
