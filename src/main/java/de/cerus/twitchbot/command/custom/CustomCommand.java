package de.cerus.twitchbot.command.custom;

public class CustomCommand {

    private final String name;
    private final String message;

    public CustomCommand(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }
    
}
