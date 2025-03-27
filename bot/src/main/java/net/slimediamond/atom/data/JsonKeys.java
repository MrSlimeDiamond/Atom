package net.slimediamond.atom.data;

import net.slimediamond.data.DataHolder;
import org.json.JSONObject;

public final class JsonKeys {
    public static String json(DataHolder dataHolder) {
        JSONObject node = new JSONObject();
        dataHolder.getKeys().forEach((key, value) -> {
            // pray that it's serializable
            node.put(key.getResourceKey().toString(), value.getValue());
        });
        return node.toString();
    }
}
