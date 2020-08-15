package de.cerus.twitchbot;

import com.github.philippheuer.events4j.core.EventManager;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.TwitchChat;
import de.cerus.twitchbot.command.CodeCommand;
import de.cerus.twitchbot.command.CommandRegistry;
import de.cerus.twitchbot.command.CustomCommandCommand;
import de.cerus.twitchbot.command.GameCommand;
import de.cerus.twitchbot.command.TitleCommand;
import de.cerus.twitchbot.command.WatchTimeCommand;
import de.cerus.twitchbot.listener.FollowListener;
import de.cerus.twitchbot.listener.MessageListener;
import de.cerus.twitchbot.sql.SqliteService;
import de.cerus.twitchbot.watchtime.WatchTimeCounter;
import java.io.File;
import java.sql.SQLException;

public class TwitchBot {

    private final TwitchClient twitchClient;
    private final SqliteService sqliteService;
    private final WatchTimeCounter watchTimeCounter;

    public TwitchBot(final TwitchClient twitchClient) throws SQLException {
        this.twitchClient = twitchClient;
        this.sqliteService = new SqliteService(new File("storage.db"));
        this.watchTimeCounter = new WatchTimeCounter(this.sqliteService);
    }

    public void start() {
        System.out.println("Starting bot...");

        final TwitchChat chat = this.twitchClient.getChat();
        chat.joinChannel("realcerus");

        final CommandRegistry commandRegistry = new CommandRegistry();
        commandRegistry.register(new WatchTimeCommand(this.twitchClient, this.watchTimeCounter));
        commandRegistry.register(new CodeCommand(this.twitchClient));
        commandRegistry.register(new CustomCommandCommand(this.twitchClient, this.sqliteService));
        commandRegistry.register(new TitleCommand(this.twitchClient));
        commandRegistry.register(new GameCommand(this.twitchClient));

        final EventManager eventManager = this.twitchClient.getEventManager();
        final SimpleEventHandler eventHandler = eventManager.getEventHandler(SimpleEventHandler.class);

        new MessageListener(eventHandler, commandRegistry);
        new FollowListener(eventHandler);

        this.watchTimeCounter.start(this.twitchClient);

        System.out.println("Bot was started");
    }

    public void stop() {
        System.out.println("Stopping bot...");

        this.watchTimeCounter.stop();

        System.out.println("Bot was stopped");
    }

}
