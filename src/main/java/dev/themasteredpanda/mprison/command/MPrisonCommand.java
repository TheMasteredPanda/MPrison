package dev.themasteredpanda.mprison.command;

import dev.themasteredpanda.mprison.MPrison;
import dev.themasteredpanda.mprison.lib.command.MCommand;
import org.bukkit.command.CommandSender;

import java.util.LinkedList;

public class MPrisonCommand extends MCommand<MPrison>
{
   public MPrisonCommand(MPrison instance)
   {
      super(instance, instance.getLocale(), "MPrison main command.", null, false, "mprison");
      addChildren(new TestChildCommand(instance));
   }

   @Override
   public void execute(CommandSender sender, LinkedList<String> arguments)
   {
      sender.sendMessage("Main MPrison command.");
   }
}
