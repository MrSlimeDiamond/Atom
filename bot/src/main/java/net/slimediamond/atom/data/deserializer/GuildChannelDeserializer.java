package net.slimediamond.atom.data.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.slimediamond.atom.Atom;
import net.slimediamond.atom.discordbot.DiscordBot;

import java.io.IOException;

public class GuildChannelDeserializer extends JsonDeserializer<GuildChannel> {
    private DiscordBot discordBot = Atom.getServiceManager().getInstance(DiscordBot.class);

    @Override
    public GuildChannel deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        System.out.println("Reached guild channel deserializer");
        return discordBot.getJDA().getTextChannelById(jsonParser.getValueAsLong());
    }
}
