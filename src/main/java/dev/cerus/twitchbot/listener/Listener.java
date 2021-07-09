package dev.cerus.twitchbot.listener;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;

public abstract class Listener {

    public Listener(final SimpleEventHandler eventHandler) {
        this.register(eventHandler);
    }

    public abstract void register(SimpleEventHandler eventHandler);

}
