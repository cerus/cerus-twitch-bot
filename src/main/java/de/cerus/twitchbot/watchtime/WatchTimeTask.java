package de.cerus.twitchbot.watchtime;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.tmi.domain.Chatters;
import com.netflix.hystrix.HystrixCommand;
import java.util.List;
import java.util.Optional;

public class WatchTimeTask implements Runnable {

    private final TwitchClient twitchClient;
    private final WatchTimeCounter watchTimeCounter;

    public WatchTimeTask(final TwitchClient twitchClient, final WatchTimeCounter watchTimeCounter) {
        this.twitchClient = twitchClient;
        this.watchTimeCounter = watchTimeCounter;
    }

    @Override
    public void run() {
        final HystrixCommand<Chatters> chatters = this.twitchClient.getMessagingInterface().getChatters("realcerus");
        final List<String> allViewers = chatters.execute().getAllViewers();

        for (final String viewer : allViewers) {
            final Optional<Session> optional = this.watchTimeCounter.getCurrentSessions().stream()
                    .filter(session -> session.getUser().equals(viewer))
                    .filter(session -> session.getChannel().equals("realcerus"))
                    .findAny();

            if (!optional.isPresent()) {
                this.watchTimeCounter.getCurrentSessions().add(new Session(viewer, "realcerus", System.currentTimeMillis()));
            } else {
                this.watchTimeCounter.saveWatchTime(optional.get());
            }
        }

        this.watchTimeCounter.getCurrentSessions().stream()
                .filter(session -> !allViewers.contains(session.getUser()))
                .forEach(session -> this.watchTimeCounter.getCurrentSessions().remove(session));
    }

}
