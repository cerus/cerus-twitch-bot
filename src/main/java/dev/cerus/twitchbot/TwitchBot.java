package dev.cerus.twitchbot;

import com.github.philippheuer.events4j.core.EventManager;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.pubsub.TwitchPubSub;
import dev.cerus.twitchbot.chatlog.ChatLogController;
import dev.cerus.twitchbot.command.CodeCommand;
import dev.cerus.twitchbot.command.CommandRegistry;
import dev.cerus.twitchbot.command.CustomCommandCommand;
import dev.cerus.twitchbot.command.GameCommand;
import dev.cerus.twitchbot.command.HelpCommand;
import dev.cerus.twitchbot.command.TitleCommand;
import dev.cerus.twitchbot.command.WatchTimeCommand;
import dev.cerus.twitchbot.listener.FollowListener;
import dev.cerus.twitchbot.listener.MessageListener;
import dev.cerus.twitchbot.listener.VideoListener;
import dev.cerus.twitchbot.sql.SqliteService;
import dev.cerus.twitchbot.watchtime.WatchTimeCounter;
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
        commandRegistry.register(new HelpCommand(this.twitchClient, commandRegistry));

        final TwitchPubSub pubSub = this.twitchClient.getPubSub();
        pubSub.listenForVideoPlaybackEvents(null, Constants.USER_ID);

        final EventManager eventManager = this.twitchClient.getEventManager();
        final SimpleEventHandler eventHandler = eventManager.getEventHandler(SimpleEventHandler.class);
        final ChatLogController chatLogController = new ChatLogController();

        new MessageListener(eventHandler, commandRegistry, chatLogController);
        new FollowListener(eventHandler);
        new VideoListener(eventHandler, chatLogController);

        this.watchTimeCounter.start(this.twitchClient);

        System.out.println("Bot was started");
    }

    public void stop() {
        System.out.println("Stopping bot...");

        this.watchTimeCounter.stop();

        System.out.println("Bot was stopped");
    }

}
