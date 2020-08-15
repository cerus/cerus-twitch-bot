package de.cerus.twitchbot.watchtime;

import com.github.twitch4j.TwitchClient;
import de.cerus.twitchbot.sql.SqliteService;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WatchTimeCounter {

    private final List<Session> currentSessions = new CopyOnWriteArrayList<>();

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final SqliteService sqliteService;

    public WatchTimeCounter(final SqliteService sqliteService) {
        this.sqliteService = sqliteService;
    }

    public void start(final TwitchClient twitchClient) {
        this.executorService.scheduleAtFixedRate(new WatchTimeTask(twitchClient, this), 0, 30, TimeUnit.SECONDS);

        try {
            this.sqliteService.update("CREATE TABLE IF NOT EXISTS `watchtime` (id BIGINT AUTO_INCREMENT, user VARCHAR(128), " +
                    "channel VARCHAR(128), watched LONG, PRIMARY KEY (id))");
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        this.executorService.shutdown();

        for (final Session session : this.currentSessions) {
            this.saveWatchTime(session);
        }
    }

    void saveWatchTime(final Session session) {
        final String channel = session.getChannel();
        final String user = session.getUser();

        try {
            final ResultSet resultSet = this.sqliteService.execute("SELECT * FROM `watchtime` WHERE user = ? AND channel = ?", user, channel);
            if (resultSet.next()) {
                this.sqliteService.update("UPDATE `watchtime` SET watched = ? WHERE user = ? AND channel = ?",
                        resultSet.getLong("watched") + (System.currentTimeMillis() - session.getJoined()), user, channel);
            } else {
                this.sqliteService.update("INSERT INTO `watchtime` (user, channel, watched) VALUES (?, ?, ?)",
                        user, channel, System.currentTimeMillis() - session.getJoined());
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<Long> getWatchTime(final String channel, final String user) {
        final CompletableFuture<Long> future = new CompletableFuture<>();
        this.executorService.submit(() -> {
            try {
                final ResultSet resultSet = this.sqliteService.execute("SELECT * FROM `watchtime` WHERE user = ? AND channel = ?", user, channel);
                if (resultSet.next()) {
                    future.complete(resultSet.getLong("watched"));
                } else {
                    future.complete(0L);
                }
            } catch (final SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    public List<Session> getCurrentSessions() {
        return this.currentSessions;
    }

}
