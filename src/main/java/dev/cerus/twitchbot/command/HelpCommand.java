package dev.cerus.twitchbot.command;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.common.events.domain.EventUser;
import java.util.stream.Collectors;

public class HelpCommand extends Command {

    private final CommandRegistry commandRegistry;

    public HelpCommand(final TwitchClient twitchClient, final CommandRegistry commandRegistry) {
        super(twitchClient, "!help");
        this.commandRegistry = commandRegistry;
    }

    @Override
    public void invoke(final EventUser user, final String[] args, final IRCMessageEvent event) {
        final String commandList = this.commandRegistry.getCommands().stream()
                .map(Command::getCommandName)
                .collect(Collectors.joining(", "));

        this.getTwitchClient().getChat().sendMessage(event.getChannel().getName(), "Verfügbare Commands: "
                + commandList + " (" + this.commandRegistry.getCommands().size() + ")");
    }

}
