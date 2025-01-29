package net.slimediamond.telegram;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.slimediamond.atom.common.util.HTTPUtil;
import net.slimediamond.telegram.events.MessageReceivedEvent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class TelegramClient {
    private String token;
    private String baseUrl;
    private long lastUpdateId = 0;
    private ArrayList<Listener> listeners;

    public TelegramClient(String token) {
        this.token = token;
        this.baseUrl = "https://api.telegram.org/bot" + token;
        this.listeners = new ArrayList<>();

        // begin listening
        new Thread(() -> {
            Thread.currentThread().setName("Telegram poller");
            while (true) {
                try {
                    pollApi();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    protected void pollApi() throws IOException {
        String url = baseUrl + "/getUpdates?offset=" + (lastUpdateId + 1);
        //System.out.println("would call to: " + url);

        HTTPUtil.getDataFromURL(url).ifPresent(data -> {
            try {
                // Parse the response
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonResponse = objectMapper.readTree(data);

                // Loop through each result (each message/update)
                for (JsonNode update : jsonResponse.get("result")) {
                    JsonNode message = update.get("message");
                    if (message != null) {
                        // Extract chat ID and message text
                        long updateId = update.get("update_id").asLong();

                        if (updateId > lastUpdateId) {
                            lastUpdateId = updateId; // Update offset to avoid duplicate processing
                            long chatId = message.get("chat").get("id").asLong();
                            String text = message.has("text") ? message.get("text").asText() : "";

                            JsonNode from = message.get("from");

                            // Safely access the fields, returning null if not present
                            String firstName = from.has("first_name") ? from.get("first_name").asText() : null;
                            String lastName = from.has("last_name") ? from.get("last_name").asText() : null;
                            String username = from.has("username") ? from.get("username").asText() : null;
                            Long userId = from.has("id") ? from.get("id").asLong() : null;

                            MessageSender sender = new MessageSender(firstName, lastName, username, userId);
                            Chat chat = new GenericChat(this, chatId);

                            MessageReceivedEvent event = new MessageReceivedEvent(sender, chat, text);
                            this.getListeners().forEach(listener -> listener.onMessage(event));
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error processing updates: " + e.getMessage());
            }
        });
    }

    public void sendMessage(long id, String message) throws IOException {
        String url = baseUrl + "/sendMessage?chat_id=" + id + "&text=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
        HTTPUtil.getDataFromURL(url).ifPresent(System.out::println);
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public ArrayList<Listener> getListeners() {
        return this.listeners;
    }
}
