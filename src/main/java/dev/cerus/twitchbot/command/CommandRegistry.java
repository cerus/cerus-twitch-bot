package dev.cerus.twitchbot.command;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CommandRegistry {

    private final Set<Command> commands = new HashSet<>();

    public void register(final Command command) {
        this.commands.add(command);
    }

    public void invoke(final ChannelMessageEvent event) {
        final String message = event.getMessage();
        final String[] strings = message.split("\\s+");
        final String command = strings[0];
        final String[] args = Arrays.copyOfRange(strings, 1, strings.length);

        this.commands.stream()
                .filter(cmd -> cmd.getCommandName().equalsIgnoreCase(command))
                .forEach(cmd -> cmd.invoke(event.getUser(), args, event.getMessageEvent()));
    }

    public Set<Command> getCommands() {
        return Collections.unmodifiableSet(this.commands);
    }
}
