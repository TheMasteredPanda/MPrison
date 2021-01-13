package dev.themasteredpanda.mprison.lib.command;
import com.google.common.collect.Lists;
import dev.themasteredpanda.mprison.lib.config.ConfigFile;
import dev.themasteredpanda.mprison.lib.config.ConfigPopulate;
import dev.themasteredpanda.mprison.lib.exception.DeveloperException;
import dev.themasteredpanda.mprison.lib.util.Format;
import dev.themasteredpanda.mprison.lib.util.ReflectionUtil;
import dev.themasteredpanda.mprison.lib.util.Sender;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * A command wrapper alternative to using CommandExecutor. Handles the repetitive instructions
 * preset in every command, in addition to handling child and parent commands, arguments, argu-
 * ment types, and
 * @param <P>
 */
public class MCommand<P extends JavaPlugin> extends Command
{
    protected P instance;
    private LinkedList<MCommand> children = Lists.newLinkedList();
    private LinkedList<MCommand> parents = Lists.newLinkedList();
    private LinkedList<ArgumentField> arguments = Lists.newLinkedList();
    private boolean playerOnlyCommand = false;

    @ConfigPopulate("locale.player_only_command")
    public String PLAYER_ONLY_COMMAND;

    @ConfigPopulate(value = "locale.no_permission", format = true)
    public String NO_PERMISSION;

    @ConfigPopulate(value = "locale.not_enough_arguments", format = true)
    public String NOT_ENOUGH_ARGUMENTS;

    @ConfigPopulate("locale.command_help.command_permission")
    public String COMMAND_HELP_COMMAND_PERMISSION;

    @ConfigPopulate("locale.incorrect_argument")
    public String INCORRECT_ARGUMENT;

    @ConfigPopulate("locale.command_help.header")
    public String COMMAND_HELP_HEADER;

    @ConfigPopulate("locale.command_help.usage")
    public String COMMAND_HELP_USAGE;

    @ConfigPopulate("locale.command_help.description")
    public String COMMAND_HELP_DESCRIPTION;

    @ConfigPopulate("locale.command_help.children_header")
    public String COMMAND_HELP_CHILDREN_HEADER;

    @ConfigPopulate("locale.command_help.child_entry")
    public String COMMAND_HELP_CHILD_ENTRY;

    @ConfigPopulate("locale.command_help.footer")
    public String COMMAND_HELP_FOOTER;

    @ConfigPopulate("locale.command_help.format")
    public List<String> COMMAND_HELP_FORMAT;

    public MCommand(P instance, ConfigFile locale, String description, String permission, boolean playerOnlyCommand, String... aliases)
    {
        super(aliases[0], description, permission, Arrays.asList(aliases));
        this.instance = instance;
        this.playerOnlyCommand = playerOnlyCommand;
        locale.populate(this);
    }

    /**
     * Registers a command direclty to the command map, instead of having to manually register the command
     * via the plugin.yml.
     *
     * @param commands - The list of commands to register.
     */
    public static void register(MCommand... commands)
    {
        Field commandMapField = ReflectionUtil.getField(Bukkit.getServer().getClass(), "commandMap", true);
        CommandMap map = ReflectionUtil.getFieldValue(Bukkit.getServer(), commandMapField);

        for (MCommand command : commands) {
            map.register(command.getName(), command);
        }
    }

    /**
     * Adds command children to this command. This will also
     * add this command as parent commands to these child
     * commands.
     *
     * @param children - Command children to add.
     */
    public void addChildren(MCommand... children)
    {
        for (MCommand command : children) {
           if (!this.children.contains(command)) {
               this.children.add(command);
               command.addParents(this);
           }
        }
    }

    /**
     * Adds parents to this command. This will also
     * add this command as child commands to the parent
     * commands.
     *
     * @param parents - Command parents to add.
     */
    public void addParents(MCommand... parents)
    {
        for (MCommand command : parents) {
            if (!this.parents.contains(command)) {
                this.parents.add(command);
                command.addChildren(this);
            }
        }
    }

    /**
     * Returns the amount of required arguments within the argument list.
     *
     * @return amount of required arguments.
     */
    public long getRequiredArguments()
    {
        return this.arguments.stream().filter(ArgumentField::isRequired).count();
    }

    /**
     * Check to see if the sender has permission to execute this command.
     *
     * @param sender - the command sender.
     * @return true for permission granted else false.
     */
    public boolean hasPermission(CommandSender sender)
    {
        return sender.isOp() || (this.getPermission() != null && sender.hasPermission(this.getPermission()));
    }

    /**
     * Add argument fields to the list. Note that required arguments cannot be added after
     * optional arguments.
     *
     * @param arguments - list of arguments to add.
     * @throws DeveloperException
     */
    public void addArguments(ArgumentField... arguments) throws DeveloperException
    {
        ArgumentField lastField = null;

        for (ArgumentField field : arguments) {
            if (lastField != null) {
                if (!lastField.isRequired() && field.isRequired()) {
                    throw new DeveloperException("You cannot have a required argument after an optional argument.");
                }
            }

            this.arguments.add(field);
            lastField = field;
        }
    }

    /**
     * Constructs a string representation of a field. Note that square brackets represent required
     * arguments and curly is optional.
     *
     * @param field - the argument field to represent.
     * @return string representation of an argument field.
     */
    public String constructArgumentRepresentation(ArgumentField field)
    {
        return (field.isRequired() ? "[" : "<") + field.getName() + (field.isRequired() ? "]" : ">");
    }

    /**
     * Constructs the command path using the list of parent commands and the name of this command.
     *
     * @return command path.
     */
    public String getCommandPath()
    {
        StringBuilder sb = new StringBuilder();

        for (MCommand parent : this.parents) {
            sb.append(parent.getName()).append(" ");
        }

       return sb.append(getName()).toString();
    }

    /**
     * Constructs the usage of the command by the command path and the string representation of all argument
     * fields.
     *
     * @return command usage.
     */
    public String getCommandUsage()
    {
        StringBuilder sb = new StringBuilder(getCommandPath()).append(" ");

        for (ArgumentField field : this.arguments) {
            sb.append(constructArgumentRepresentation(field));

            if (!this.arguments.getLast().equals(field)) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args)
    {
        Bukkit.getLogger().info("Executed command " + label);
        if (!(sender instanceof Player) && this.playerOnlyCommand) {
            sender.sendMessage(PLAYER_ONLY_COMMAND);
            return false;
        }

        LinkedList<String> stringArguments = Lists.newLinkedList(Arrays.asList(args));

        if (stringArguments.size() >= 1) {
            if (args[0].equalsIgnoreCase("help")) {
                LinkedList<BaseComponent[]> messages = Lists.newLinkedList();

                for (String line : COMMAND_HELP_FORMAT) {
                    if (line.contains("{command_children}")) {
                        if (this.children.size() == 0) {
                            continue;
                        }

                        LinkedList<BaseComponent[]> childCommands = Lists.newLinkedList();
                        childCommands.add(TextComponent.fromLegacyText(Format.colour(COMMAND_HELP_CHILDREN_HEADER)));
                        ComponentBuilder builder = new ComponentBuilder();

                        for (MCommand child : this.children) {
                            builder.append(Format.format(COMMAND_HELP_CHILD_ENTRY, "{command_name};" + child.getName())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Execute command"))).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + child.getCommandPath() + " help"));
                            childCommands.add(builder.create());
                        }

                        messages.addAll(childCommands);
                        continue;
                    }

                    messages.add(TextComponent.fromLegacyText(Format.format(line, "{header};" + COMMAND_HELP_HEADER, "{footer};" + COMMAND_HELP_FOOTER,
                            "{command_description};" + Format.format(COMMAND_HELP_DESCRIPTION, "{description};" + getDescription()),
                            "{command_permission};" + Format.format(COMMAND_HELP_COMMAND_PERMISSION, "{command_has_permission};" + (hasPermission(sender) ? "yes": "no")),
                            "{command_usage};" + Format.format(COMMAND_HELP_USAGE, "{command};" + getCommandUsage()))));
                }

                Sender.send(sender, messages);
                return true;
            }


            for (MCommand child : this.children) {
                if (child.getAliases().contains(args[0].toLowerCase(Locale.ROOT))) {
                    stringArguments.removeFirst();
                    child.execute(sender, label, stringArguments.toArray(new String[0]));
                    return true;
                }
            }


            if (getRequiredArguments() > stringArguments.size()) {
                sender.sendMessage(Format.format(NOT_ENOUGH_ARGUMENTS, "{required_argument_count};" + getRequiredArguments(), "{argument_count};" + stringArguments.size()));
                return true;
            }

            for (int i = 0; i < stringArguments.size(); i++) {
               String arg = stringArguments.get(0);

               if (this.arguments.size() < i) {
                   break;
               }

               ArgumentField field = this.arguments.get(i);

               if (!field.isValid(arg)) {
                  sender.sendMessage(Format.format(INCORRECT_ARGUMENT, "{argument_name};" + field.getName(), "{argument_type};" + field.getType().toString().toLowerCase(Locale.ROOT)));
                  return false;
               }
            }
        }

        execute(sender, stringArguments);
        return true;
    }

    public void execute(CommandSender sender, LinkedList<String> arguments) { }
}
