package dev.cerus.twitchbot.listener;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import dev.cerus.twitchbot.chatlog.ChatLogController;
import dev.cerus.twitchbot.command.CommandRegistry;

public class MessageListener extends Listener {

    private final CommandRegistry commandRegistry;
    private final ChatLogController chatLogController;

    public MessageListener(final SimpleEventHandler eventHandler, final CommandRegistry commandRegistry, final ChatLogController chatLogController) {
        super(eventHandler);
        this.commandRegistry = commandRegistry;
        this.chatLogController = chatLogController;
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
        this.chatLogController.addMessage(event.getUser().getName(), event.getMessage());
    }

}
