package dev.cerus.twitchbot.command;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.helix.domain.ChannelInformation;
import com.github.twitch4j.helix.domain.Game;
import dev.cerus.twitchbot.Constants;
import java.util.Collections;
import java.util.List;

public class GameCommand extends Command {

    public GameCommand(final TwitchClient twitchClient) {
        super(twitchClient, "!game");
    }

    @Override
    public void invoke(final EventUser user, final String[] args, final IRCMessageEvent event) {
        if (args.length == 0 || !Constants.WHITELIST.contains(user.getName())) {
            final ChannelInformation channelInfo = this.getTwitchClient().getHelix().getChannelInformation(System.getenv("TWITCH_OAUTH"),
                    Collections.singletonList(Constants.USER_ID)).execute().getChannels().get(0);

            this.getTwitchClient().getChat().sendMessage(event.getChannel().getName(), "Es wird gerade " + channelInfo.getGameName() + " gespielt.");
            return;
        }

        final List<Game> games = this.getTwitchClient().getHelix().getGames(System.getenv("TWITCH_OAUTH"), null,
                Collections.singletonList(String.join(" ", args))).execute().getGames();

        if (games.isEmpty()) {
            this.getTwitchClient().getChat().sendMessage(event.getChannel().getName(), "Es wurde kein entsprechendes Spiel gefunden.");
            return;
        }
        final Game game = games.get(0);

        final ChannelInformation channelInfo = this.getTwitchClient().getHelix().getChannelInformation(System.getenv("CERUS_TOKEN"),
                Collections.singletonList(Constants.USER_ID)).execute().getChannels().get(0);
        final ChannelInformation newChannelInfo = new ChannelInformation(channelInfo.getBroadcasterId(), channelInfo.getBroadcasterName(),
                channelInfo.getBroadcasterLanguage(), game.getId(), game.getName(), channelInfo.getTitle());

        this.getTwitchClient().getHelix().updateChannelInformation(System.getenv("CERUS_TOKEN"), Constants.USER_ID, newChannelInfo).execute();

        this.getTwitchClient().getChat().sendMessage(event.getChannel().getName(), "Es wird jetzt " + game.getName() + " gespielt.");
    }

}
