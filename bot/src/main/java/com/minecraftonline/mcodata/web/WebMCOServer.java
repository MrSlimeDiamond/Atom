package com.minecraftonline.mcodata.web;

import com.minecraftonline.mcodata.api.model.MCOPlayer;
import com.minecraftonline.mcodata.api.model.MCOServer;

import java.io.IOException;
import java.util.List;

public class WebMCOServer implements MCOServer {

    @Override
    public List<MCOPlayer> getOnlinePlayers() {
        try {
            return WebAPI.getOnlinePlayers().orElse(List.of()).stream()
                    .flatMap(player -> MCOWebDataProvider.web().getPlayerByName(player).stream())
                    .toList();
        } catch (IOException e) {
            // MCO is likely down or empty
            return List.of();
        }
    }

}
