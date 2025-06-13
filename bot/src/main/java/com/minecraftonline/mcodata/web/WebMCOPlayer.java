package com.minecraftonline.mcodata.web;

import com.minecraftonline.mcodata.api.exceptions.DataNotFoundException;
import com.minecraftonline.mcodata.api.exceptions.PlayerNotFoundException;
import com.minecraftonline.mcodata.api.model.MCOPlayer;
import com.minecraftonline.mcodata.api.model.Note;
import net.slimediamond.util.minecraft.MinecraftUtils;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class WebMCOPlayer implements MCOPlayer {

    private final String name;

    public WebMCOPlayer(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getUUID() {
        try {
            String uuid = MinecraftUtils.getPlayerUUID(name).orElseThrow(() -> new PlayerNotFoundException(name));
            return UUID.fromString(uuid);
        } catch (IOException e) {
            throw new DataNotFoundException(e);
        }
    }

    @Override
    public Date getFirstseen() {
        return WebAPI.getFirstseen(name).orElseThrow(() -> new PlayerNotFoundException(name));
    }

    @Override
    public Date getLastseen() {
        return WebAPI.getLastseen(name).orElseThrow(() -> new PlayerNotFoundException(name));
    }

    @Override
    public int getTimeOnline() {
        return WebAPI.getTimeOnline(name).orElseThrow(() -> new PlayerNotFoundException(name));
    }

    @Override
    public boolean isOnline() {
        return MCOWebDataProvider.web().getServer().getOnlinePlayers().contains(this);
    }

    @Override
    public boolean isBanned() {
        return getBanReason().isPresent();
    }

    @Override
    public Optional<Note> getBanReason() {
        return WebAPI.getBanReason(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebMCOPlayer that = (WebMCOPlayer) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

}
