package dev.themasteredpanda.mprison.lib.util;

import com.google.common.collect.Lists;
import dev.themasteredpanda.mprison.lib.exception.UtilException;
import lombok.NonNull;
import org.bukkit.ChatColor;

import java.util.List;

/**
 * Utility class for formatting strings.
 */
public class Format
{
    private static String PREFIX;

    public Format() throws UtilException
    {
        throw new UtilException();
    }

    /**
     * Sets the prefix often prepended to every single line message.
     * @param prefix - the prefix.
     */
    public static void setPrefix(@NonNull String prefix)
    {
        PREFIX = prefix;
    }

    /**
     * Format a string with the prefix and all pairs. This will also serialize colour characters. A pair is a string
     * split with a semicolon, the first section containing the placeholder and the last containing the string to
     * replace the placeholder.
     *
     * @param message - The message.
     * @return A formatted and coloured message.
     */
    public static String format(String message, String... pairs)
    {
        for (String pair : pairs) {
            String[] splitPair = pair.split(";");

            message = message.replace(splitPair[0], splitPair[1]);
        }

        return Format.colour(message.replace("{prefix}", PREFIX));
    }

    /**
     * Format a list of strings with the prefix. This will also serialize colour characters.
     * @param message - The list of messages.
     * @return A formatted and coloured list of messages.
     */
    public static List<String> format(List<String> message)
    {
        List<String> list = Lists.newLinkedList();

        for (String line : message) {
            list.add(Format.format(line));
        }

        return list;
    }

    /**
     * Serialized colour characters.
     * @param message - The message.
     * @return A coloured message.
     */
    public static String colour(String message)
    {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
