package net.zenoc.atom.util;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Optional;

public class MinecraftUtils {
    public static Optional<String> getPlayerUUID(String username) throws IOException {
        Optional<JSONObject> json =  HTTPUtil.getJsonDataFromURL("https://api.mojang.com/users/profiles/minecraft/" + username);

        json.map(jsonObject -> {
            if (jsonObject.isNull("id")) {
                return Optional.empty();
            } else {
                return jsonObject.getString("id");
            }
        });
        return Optional.empty();
    }
}
