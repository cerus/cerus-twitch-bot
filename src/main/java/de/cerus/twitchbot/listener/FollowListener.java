package de.cerus.twitchbot.listener;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.FollowEvent;

public class FollowListener extends Listener {

    public FollowListener(final SimpleEventHandler eventHandler) {
        super(eventHandler);
    }

    @Override
    public void register(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(FollowEvent.class, this::onFollow);
    }

    private void onFollow(final FollowEvent event) {
        if (!event.getChannel().getName().equals("realcerus")) {
            return;
        }

        event.getTwitchChat().sendMessage(event.getChannel().getName(), "Vielen Dank für deinen Follow, "
                + event.getUser().getName() + "!");
    }

}
