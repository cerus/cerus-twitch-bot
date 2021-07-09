package dev.cerus.twitchbot.command;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.common.events.domain.EventUser;

public class CodeCommand extends Command {

    public CodeCommand(final TwitchClient twitchClient) {
        super(twitchClient, "!code");
    }

    @Override
    public void invoke(final EventUser user, final String[] args, final IRCMessageEvent event) {
        this.getTwitchClient().getChat().sendMessage(event.getChannel().getName(), "Dieser Bot ist Open Source: " +
                "https://github.com/RealCerus/cerus-twitch-bot");
    }

}
