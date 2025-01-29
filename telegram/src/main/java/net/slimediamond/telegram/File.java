package net.slimediamond.telegram;

public record File(String fileId, String fileUniqueId, long fileSize, int width, int height) {
}
