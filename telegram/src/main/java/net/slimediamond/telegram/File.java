package net.slimediamond.telegram;

public record File(String fileId, String fileUniqueId, long fileSize, String filePath, TelegramClient client) {
    public String download() {
        return client().getFileUrl() + "/" + filePath;
    }
}
