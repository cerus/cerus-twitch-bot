package de.cerus.twitchbot.listener;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import de.cerus.twitchbot.command.CommandRegistry;

public class MessageListener extends Listener {

    private final CommandRegistry commandRegistry;

    public MessageListener(final SimpleEventHandler eventHandler, final CommandRegistry commandRegistry) {
        super(eventHandler);
        this.commandRegistry = commandRegistry;
    }

    @Override
    public void register(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onMessage);
    }

    private void onMessage(final ChannelMessageEvent event) {
        if (!event.getMessageEvent().getChannel().getName().equals("realcerus")) {
            return;
        }

        this.commandRegistry.invoke(event);
    }

}
