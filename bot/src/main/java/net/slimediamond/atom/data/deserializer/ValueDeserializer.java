package net.slimediamond.atom.data.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import net.slimediamond.data.value.Value;

import java.io.IOException;

public class ValueDeserializer extends JsonDeserializer<Value<?>> {
    @Override
    public Value<?> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        try {
            Class<?> clazz = Class.forName(node.get("type").asText());
            Object value = jsonParser.getCodec().treeToValue(node, clazz);
            return Value.of(value);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
