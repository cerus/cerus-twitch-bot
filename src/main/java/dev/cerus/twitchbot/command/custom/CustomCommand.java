package dev.cerus.twitchbot.command.custom;

public class CustomCommand {

    private final String name;
    private final String message;

    public CustomCommand(final String name, final String message) {
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return this.name;
    }

    public String getMessage() {
        return this.message;
    }

}
