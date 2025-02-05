package net.slimediamond.telegram.entity;

import net.slimediamond.telegram.TelegramClient;

public record File(String fileId, String fileUniqueId, long fileSize, String filePath, TelegramClient client) {
    public String download() {
        return client().getFileUrl() + "/" + filePath;
    }
}
