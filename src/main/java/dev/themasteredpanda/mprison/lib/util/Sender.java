package dev.themasteredpanda.mprison.lib.util;

import dev.themasteredpanda.mprison.lib.exception.UtilException;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;

public class Sender
{
    public Sender() throws UtilException
    {
        throw new UtilException();
    }

    public static void send(CommandSender sender, Iterable<BaseComponent[]> components)
    {
        for (BaseComponent[] component : components) {
            sender.spigot().sendMessage(component);
        }
    }
}
