package de.cerus.twitchbot.watchtime;

public class Session {

    private final String user;
    private final String channel;
    private long joined;

    public Session(final String user, final String channel, final long joined) {
        this.user = user;
        this.channel = channel;
        this.joined = joined;
    }

    public String getUser() {
        return this.user;
    }

    public String getChannel() {
        return this.channel;
    }

    public long getJoined() {
        return this.joined;
    }

    public void setJoined(long joined) {
        this.joined = joined;
    }
    
}
