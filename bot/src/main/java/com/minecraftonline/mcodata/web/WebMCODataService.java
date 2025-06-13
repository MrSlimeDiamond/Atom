package com.minecraftonline.mcodata.web;

import com.minecraftonline.mcodata.api.exceptions.PlayerNotFoundException;
import com.minecraftonline.mcodata.api.model.MCOPlayer;
import com.minecraftonline.mcodata.api.model.MCOServer;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class WebMCODataService {

    public MCOPlayer getPlayerByUUID(UUID uuid) {
        return null;
    }

    public Optional<MCOPlayer> getPlayerByName(String partial) {
        try {
            String correctName = WebAPI.getCorrectName(partial).orElseThrow(() -> new PlayerNotFoundException(partial));
            return Optional.of(new WebMCOPlayer(correctName));
        } catch (IOException | PlayerNotFoundException e) {
            return Optional.empty();
        }
    }

    public MCOServer getServer() {
        return new WebMCOServer();
    }

}
