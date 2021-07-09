package dev.cerus.twitchbot.chatlog;

public class Message {

    public final String user;
    public final String message;
    public final long timestamp;

    public Message(final String user, final String message) {
        this.user = user;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

}
