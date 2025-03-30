package net.slimediamond.atom.data.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import net.dv8tion.jda.internal.entities.channel.concrete.TextChannelImpl;
import net.slimediamond.atom.Atom;
import net.slimediamond.atom.discordbot.DiscordBot;

import java.io.IOException;

public class TextChannelDeserializer extends JsonDeserializer<TextChannelImpl> {
    private DiscordBot discordBot = Atom.getServiceManager().getInstance(DiscordBot.class);

    @Override
    public TextChannelImpl deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
//        System.out.println(node.toPrettyString());
//        if (node.has("value")) {
//            throw new JsonParseException(jsonParser, "ID should be in the 'value' field");
//        }
        long id = node.get("value").asLong();
        return (TextChannelImpl) discordBot.getJDA().getTextChannelById(id);
    }
}
