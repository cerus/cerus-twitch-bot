package de.cerus.twitchbot;

import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final List<String> WHITELIST = Arrays.asList(System.getenv("TWITCH_WHITELIST").split(";"));

}
