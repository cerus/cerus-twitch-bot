package dev.cerus.twitchbot.command;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.common.events.domain.EventUser;

public abstract class Command {

    private final TwitchClient twitchClient;
    private final String commandName;

    public Command(final TwitchClient twitchClient, final String commandName) {
        this.twitchClient = twitchClient;
        this.commandName = commandName;
    }

    public abstract void invoke(EventUser user, String[] args, IRCMessageEvent event);

    protected TwitchClient getTwitchClient() {
        return this.twitchClient;
    }

    public String getCommandName() {
        return this.commandName;
    }

}
