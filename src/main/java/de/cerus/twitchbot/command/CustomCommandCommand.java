package de.cerus.twitchbot.command;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.common.events.domain.EventUser;
import de.cerus.twitchbot.command.custom.CustomCommand;
import de.cerus.twitchbot.command.custom.CustomCommandRegistry;
import de.cerus.twitchbot.sql.SqliteService;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomCommandCommand extends Command {

    private final List<String> whitelist = Arrays.asList(System.getenv("TWITCH_WHITELIST").split(";"));

    private final CustomCommandRegistry customCommandRegistry;

    public CustomCommandCommand(final TwitchClient twitchClient, final SqliteService sqliteService) {
        super(twitchClient, "!command");
        this.customCommandRegistry = new CustomCommandRegistry(sqliteService);
    }

    @Override
    public void invoke(final EventUser user, final String[] args, final IRCMessageEvent event) {
        if (!this.whitelist.contains(user.getName())) {
            return;
        }

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("list")) {
                this.handleListCommand(event);
                return;
            }
            if (args[0].equalsIgnoreCase("add")) {
                this.handleAddCommand(Arrays.copyOfRange(args, 1, args.length), event);
                return;
            }
            if (args[0].equalsIgnoreCase("remove")) {

                return;
            }
        }
    }

    public void handleListCommand(final IRCMessageEvent event) {
        final TwitchChat chat = this.getTwitchClient().getChat();
        chat.sendMessage(event.getChannel().getName(), "Verfügbare custom Commands: "
                + this.customCommandRegistry.getCustomCommands().stream()
                .map(CustomCommand::getName)
                .collect(Collectors.joining(", ")) + " (" + this.customCommandRegistry.getCustomCommands().size() + ")");
    }

    public void handleAddCommand(final String[] args, final IRCMessageEvent event) {
        if (args.length < 2) {
            return;
        }

        final String name = args[0];
        final String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        this.customCommandRegistry.register(new CustomCommand(name, message));
        this.getTwitchClient().getChat().sendMessage(event.getChannel().getName(), "Command hinzugefügt");
    }

}
