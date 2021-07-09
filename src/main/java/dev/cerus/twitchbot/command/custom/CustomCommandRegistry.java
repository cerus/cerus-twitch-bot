package dev.cerus.twitchbot.command.custom;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import dev.cerus.twitchbot.sql.SqliteService;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CustomCommandRegistry {

    private final Set<CustomCommand> customCommands = new HashSet<>();

    private final SqliteService sqliteService;

    public CustomCommandRegistry(final TwitchClient twitchClient, final SqliteService sqliteService) {
        this.sqliteService = sqliteService;

        try {
            this.load(sqliteService);
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class)
                .onEvent(ChannelMessageEvent.class, event -> {
                    if (!event.getMessageEvent().getChannel().getName().equals("realcerus")) {
                        return;
                    }

                    this.invoke(event);
                });
    }

    private void load(final SqliteService sqliteService) throws SQLException {
        sqliteService.update("CREATE TABLE IF NOT EXISTS `custom_commands` (id BIGINT AUTO_INCREMENT, " +
                "name VARCHAR(128), message TEXT, PRIMARY KEY (id))");

        final ResultSet resultSet = sqliteService.execute("SELECT * FROM `custom_commands`");
        while (resultSet.next()) {
            this.customCommands.add(new CustomCommand(
                    resultSet.getString("name"),
                    resultSet.getString("message")
            ));
        }
    }

    public void register(final CustomCommand command) {
        this.customCommands.add(command);

        try {
            this.sqliteService.update("INSERT INTO `custom_commands` (name, message) VALUES (?, ?)", command.getName(), command.getMessage());
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(final String commandName) {
        new HashSet<>(this.customCommands).stream()
                .filter(command -> command.getName().equals(commandName))
                .forEach(command -> {
                    this.customCommands.remove(command);

                    try {
                        this.sqliteService.update("DELETE FROM `custom_commands` WHERE name = ?", command.getName());
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void invoke(final ChannelMessageEvent event) {
        this.customCommands.stream()
                .filter(command -> command.getName().equalsIgnoreCase(event.getMessage()))
                .findAny()
                .ifPresent(command -> event.getTwitchChat()
                        .sendMessage(event.getMessageEvent().getChannel().getName(), command.getMessage()));
    }

    public Set<CustomCommand> getCustomCommands() {
        return Collections.unmodifiableSet(this.customCommands);
    }
}
