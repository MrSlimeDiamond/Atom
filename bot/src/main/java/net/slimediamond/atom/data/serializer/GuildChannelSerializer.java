package net.slimediamond.atom.data.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

import java.io.IOException;

public class GuildChannelSerializer extends JsonSerializer<GuildChannel> {
    @Override
    public void serialize(GuildChannel guildChannel, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (guildChannel == null) {
            jsonGenerator.writeNull();
        } else {
            jsonGenerator.writeObject(guildChannel.getIdLong());
        }
    }
}
