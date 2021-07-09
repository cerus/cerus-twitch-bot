package dev.cerus.twitchbot.chatlog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ChatLogController {

    private final List<Message> messageList = new ArrayList<>();

    private boolean log;

    public void addMessage(final String user, final String message) {
        if (!this.log) {
            return;
        }

        this.messageList.add(new Message(user, message));
    }

    public void saveAndClear(final OutputStream jsonOut, final String csvPath) {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final JsonArray array = new JsonArray();
        this.messageList.forEach(message -> array.add(gson.toJsonTree(message)));
        final String jsonString = gson.toJson(array);

        try {
            jsonOut.write(jsonString.getBytes(StandardCharsets.UTF_8));
        } catch (final IOException e) {
            e.printStackTrace();
        }

        this.messageList.clear();
    }

    public boolean isLog() {
        return this.log;
    }

    public void setLog(final boolean log) {
        this.log = log;
    }

}
