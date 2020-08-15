package de.cerus.twitchbot.command;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.common.events.domain.EventUser;
import de.cerus.twitchbot.watchtime.WatchTimeCounter;

public class WatchTimeCommand extends Command {

    private final WatchTimeCounter watchTimeCounter;

    public WatchTimeCommand(final TwitchClient twitchClient, final WatchTimeCounter watchTimeCounter) {
        super(twitchClient, "!watchtime");
        this.watchTimeCounter = watchTimeCounter;
    }

    @Override
    public void invoke(final EventUser user, final String[] args, final IRCMessageEvent event) {
        final TwitchChat chat = this.getTwitchClient().getChat();
        final String userName = args.length == 1 ? args[0] : user.getName();

        this.watchTimeCounter.getWatchTime(event.getChannel().getName(), userName).whenComplete((aLong, throwable) -> {
            if (throwable != null) {
                chat.sendMessage(event.getChannel().getName(), "Konnte die Watchtime von "
                        + user.getName() + " nicht abrufen: " + throwable.getMessage());
                return;
            }

            chat.sendMessage(event.getChannel().getName(), userName + " schaut seit " + this.formatDuration(aLong) + " zu.");
        });
    }

    private String formatDuration(final long duration) {
        final long seconds = duration / 1000;
        final long hours = seconds / 3600;
        final long minutes = (seconds % 3600) / 60;

        return String.format("%d Stunden und %d Minuten", hours, minutes);
    }

}
