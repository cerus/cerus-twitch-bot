package dev.cerus.twitchbot.listener;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.pubsub.domain.VideoPlaybackData;
import com.github.twitch4j.pubsub.events.VideoPlaybackEvent;
import dev.cerus.twitchbot.Constants;
import dev.cerus.twitchbot.chatlog.ChatLogController;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoListener extends Listener {

    private final ChatLogController chatLogController;

    public VideoListener(final SimpleEventHandler eventHandler, final ChatLogController chatLogController) {
        super(eventHandler);
        this.chatLogController = chatLogController;
    }

    @Override
    public void register(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(VideoPlaybackEvent.class, this::onVideoPlayback);
    }

    private void onVideoPlayback(final VideoPlaybackEvent event) {
        if (!(event.getChannelId().equals(Constants.USER_ID))) {
            return;
        }

        final VideoPlaybackData data = event.getData();
        if (data.getType() == VideoPlaybackData.Type.STREAM_UP) {
            this.chatLogController.setLog(true);
        } else if (data.getType() == VideoPlaybackData.Type.STREAM_DOWN) {
            final File logDir = new File("logs");
            logDir.mkdirs();
            final File jsonLogFile = new File(logDir, "chatlog-" + new SimpleDateFormat("dd.MM.yyyy-HH.mm.ss").format(new Date()) + ".json");
            final File csvLogFile = new File(logDir, "chatlog-" + new SimpleDateFormat("dd.MM.yyyy-HH.mm.ss").format(new Date()) + ".csv");

            try {
                jsonLogFile.createNewFile();
                this.chatLogController.saveAndClear(new FileOutputStream(jsonLogFile), csvLogFile.getPath());
            } catch (final IOException e) {
                e.printStackTrace();
            }
            this.chatLogController.setLog(false);
        }
    }

}
