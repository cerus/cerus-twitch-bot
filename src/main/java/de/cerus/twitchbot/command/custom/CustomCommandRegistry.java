package de.cerus.twitchbot.command.custom;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import de.cerus.twitchbot.sql.SqliteService;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CustomCommandRegistry {

    private final Set<CustomCommand> customCommands = new HashSet<>();

    public CustomCommandRegistry(final SqliteService sqliteService) {
        try {
            this.load(sqliteService);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private void load(final SqliteService sqliteService) throws SQLException {
        sqliteService.update("CREATE TABLE IF NOT EXISTS `custom_commands` (id BIGINT AUTO_INCREMENT, " +
                "name VARCHAR(128), message TEXT, PRIMARY KEY (id))");

        final ResultSet resultSet = sqliteService.execute("SELECT * FROM `custom_commands`");
        while (resultSet.next()) {
            this.register(new CustomCommand(
                    resultSet.getString("name"),
                    resultSet.getString("message")
            ));
        }
    }

    public void register(final CustomCommand command) {
        this.customCommands.add(command);
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
