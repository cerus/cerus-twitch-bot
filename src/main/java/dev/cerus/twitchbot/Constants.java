package dev.cerus.twitchbot;

import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final List<String> WHITELIST = Arrays.asList(System.getenv("TWITCH_WHITELIST").split(";"));
    public static String USER_ID = "253946595";

}
