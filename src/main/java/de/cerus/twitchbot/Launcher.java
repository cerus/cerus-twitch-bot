package de.cerus.twitchbot;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import java.sql.SQLException;

public class Launcher {

    public static void main(final String[] args) throws SQLException {
        final TwitchClient twitchClient = TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withEnableKraken(true)
                .withEnableTMI(true)
                .withEnableChat(true)
                .withChatAccount(new OAuth2Credential("twitch", System.getenv("TWITCH_OAUTH")))
                .withClientId(System.getenv("TWITCH_CLIENTID"))
                .withClientSecret(System.getenv("TWITCH_SECRET"))
                .build();

        final TwitchBot twitchBot = new TwitchBot(twitchClient);
        twitchBot.start();

        Runtime.getRuntime().addShutdownHook(new Thread(twitchBot::stop));
    }

}
