package de.cerus.twitchbot.command;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.helix.domain.ChannelInformation;
import de.cerus.twitchbot.Constants;
import java.util.Collections;

public class TitleCommand extends Command {

    public TitleCommand(final TwitchClient twitchClient) {
        super(twitchClient, "!title");
    }

    @Override
    public void invoke(final EventUser user, final String[] args, final IRCMessageEvent event) {
        if (!Constants.WHITELIST.contains(user.getName()) || args.length == 0) {
            return;
        }

        final ChannelInformation channelInfo = this.getTwitchClient().getHelix().getChannelInformation(System.getenv("CERUS_TOKEN"),
                Collections.singletonList(Constants.USER_ID)).execute().getChannels().get(0);
        final ChannelInformation newChannelInfo = new ChannelInformation(channelInfo.getBroadcasterId(), channelInfo.getBroadcasterName(),
                channelInfo.getBroadcasterLanguage(), channelInfo.getGameId(), channelInfo.getGameName(), String.join(" ", args));

        this.getTwitchClient().getHelix().updateChannelInformation(System.getenv("CERUS_TOKEN"), Constants.USER_ID, newChannelInfo).execute();

        this.getTwitchClient().getChat().sendMessage("realcerus", "Titel wurde geändert");
    }

}
