package com.minecraftonline.mcodata.api.model;

import java.util.List;

public interface MCOServer {

    /**
     * Get a list of online players on MinecraftOnline right now
     *
     * @return Online players
     */
    List<MCOPlayer> getOnlinePlayers();

    /**
     * Get the amount of bans on MinecraftOnline
     *
     * @return Ban count
     */
    int getBanCount();

}
