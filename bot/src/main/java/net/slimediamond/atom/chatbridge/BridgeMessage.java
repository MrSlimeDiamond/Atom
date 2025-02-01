package net.slimediamond.atom.chatbridge;

import java.io.File;
import java.util.ArrayList;

public class BridgeMessage {
    private String username;
    private String avatarUrl;
    private String content;

    private ArrayList<File> files = new ArrayList<>();

    public BridgeMessage(String username, String avatarUrl, String content) {
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getContent() {
        return content;
    }

    public void addFile(File file) {
        this.files.add(file);
    }

    public ArrayList<File> getFiles() {
        return this.files;
    }
}
