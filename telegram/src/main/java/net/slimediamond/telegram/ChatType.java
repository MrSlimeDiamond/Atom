package net.slimediamond.telegram;

public enum ChatType {
    PRIVATE("private"),
    SUPERGROUP("supergroup");

    private String name;

    ChatType(String name) {
        this.name = name;
    }

    public static ChatType fromName(String name) {
        for (ChatType type : ChatType.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown chat type: " + name);
    }
}
