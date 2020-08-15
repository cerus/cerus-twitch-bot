package de.cerus.twitchbot.command;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.common.events.domain.EventUser;
import de.cerus.twitchbot.Constants;

public class TitleCommand extends Command {

    public TitleCommand(final TwitchClient twitchClient) {
        super(twitchClient, "!title");
    }

    @Override
    public void invoke(final EventUser user, final String[] args, final IRCMessageEvent event) {
        if (!Constants.WHITELIST.contains(user.getName())) {
            return;
        }

        if (args.length == 0) {
            return;
        }

        this.getTwitchClient().getKraken().updateTitle(System.getenv("TWITCH_OAUTH"), "realcerus", String.join(" ", args));
        this.getTwitchClient().getChat().sendMessage("realcerus", "Titel wurde geändert");
    }

}
