package de.cerus.twitchbot.command;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.helix.domain.GameList;
import com.github.twitch4j.helix.domain.StreamList;
import de.cerus.twitchbot.Constants;
import java.util.Collections;

public class GameCommand extends Command {

    public GameCommand(final TwitchClient twitchClient) {
        super(twitchClient, "!game");
    }

    @Override
    public void invoke(final EventUser user, final String[] args, final IRCMessageEvent event) {
        if (args.length > 0 && Constants.WHITELIST.contains(user.getName())) {
            //TODO: Change game
            return;
        }

        final StreamList streamList = this.getTwitchClient().getHelix().getStreams(System.getenv("TWITCH_OAUTH"), null, null, 1,
                null, null, null, Collections.singletonList(Constants.USER_ID), null).execute();
        final String gameId = streamList.getStreams().get(0).getGameId();

        final GameList gameList = this.getTwitchClient().getHelix().getGames(System.getenv("TWITCH_OAUTH"), Collections.singletonList(gameId), null).execute();
        final String name = gameList.getGames().get(0).getName();

        this.getTwitchClient().getChat().sendMessage(event.getChannel().getName(), "Es wird gerade " + name + " gespielt.");
    }

}
