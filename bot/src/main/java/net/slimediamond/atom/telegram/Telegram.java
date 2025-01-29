package net.slimediamond.atom.telegram;

import net.slimediamond.atom.common.annotations.Service;
import net.slimediamond.atom.reference.TelegramReference;
import net.slimediamond.atom.telegram.event.TelegramMessageListener;
import net.slimediamond.telegram.TelegramClient;

@Service("telegram")
public class Telegram {
    @Service.Start
    public void onStart() {
        // FOR THE LOVE OF GOD PLEASE REMOVE THIS TOKEN BEFORE COMMITTING
        TelegramClient client = new TelegramClient(TelegramReference.token);
        client.addListener(new TelegramMessageListener());
    }
}
