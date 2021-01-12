package dev.themasteredpanda.mprison.command;

import dev.themasteredpanda.mprison.MPrison;
import dev.themasteredpanda.mprison.lib.command.MCommand;
import org.bukkit.command.CommandSender;

import java.util.LinkedList;

public class TestChildCommand extends MCommand<MPrison>
{
    public TestChildCommand(MPrison instance)
    {
        super(instance, instance.getLocale(), "Test child command.", null, false, "childcommand");
    }

    @Override
    public void execute(CommandSender sender, LinkedList<String> arguments)
    {
        sender.sendMessage("Child command.");
    }
}
