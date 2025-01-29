package net.slimediamond.telegram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.slimediamond.atom.common.util.HTTPUtil;
import net.slimediamond.telegram.events.MessageReceivedEvent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.slimediamond.atom.common.util.HTTPUtil.getDataFromURL;
import static net.slimediamond.atom.common.util.HTTPUtil.getJsonDataFromURL;

public class TelegramClient {
    private String baseUrl;
    private long lastUpdateId = 0;
    private ArrayList<Listener> listeners;
    private String username;

    public TelegramClient(String token) {
        this.baseUrl = "https://api.telegram.org/bot" + token;
        this.listeners = new ArrayList<>();

        // get info about the bot (in this case, username)
        String getMe = baseUrl + "/getMe";
        try {
            Optional<String> data = getDataFromURL(getMe);
            if (data.isPresent()) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonResponse = objectMapper.readTree(data.get());

                this.username = jsonResponse.get("result").get("username").asText();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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

                            MessageReceivedEvent event = new MessageReceivedEvent(sender, chat, text, this);
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
        HTTPUtil.getDataFromURL(url); // ignore output
    }

    public String getUsername() {
        return this.username;
    }

    public List<File> getUserProfilePhotos(long userId) throws IOException {
        String url = baseUrl + "/getUserProfilePhotos?user_id=" + userId;
        ArrayList<File> files = new ArrayList<>();
        HTTPUtil.getDataFromURL(url).ifPresent(data -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonResponse = objectMapper.readTree(data);

                JsonNode photos = jsonResponse.get("result").get("photos");
                for (JsonNode photo : photos) {
                    // just add the largest one for now, which is located at index [2]
                    JsonNode file = photo.get(2);
                    files.add(new File(file.get("file_id").asText(), file.get("file_unique_id").asText(), file.get("file_size").asLong(), file.get("width").asInt(), file.get("height").asInt()));
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        return files;
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public ArrayList<Listener> getListeners() {
        return this.listeners;
    }
}
