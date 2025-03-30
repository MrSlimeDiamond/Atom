package net.slimediamond.atom.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.internal.entities.channel.concrete.TextChannelImpl;
import net.slimediamond.atom.Atom;
import net.slimediamond.atom.data.deserializer.TextChannelDeserializer;
import net.slimediamond.atom.data.deserializer.ValueDeserializer;
import net.slimediamond.atom.data.serializer.GuildChannelSerializer;
import net.slimediamond.data.DataHolder;
import net.slimediamond.data.Key;
import net.slimediamond.data.identification.ResourceKey;
import net.slimediamond.data.value.Value;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

public final class JsonKeys {
    public static final ObjectMapper om;

    static {
        om = new ObjectMapper();

        SimpleModule module = new SimpleModule();
        module.addDeserializer(Value.class, new ValueDeserializer());
        module.addSerializer(GuildChannel.class, new GuildChannelSerializer());

        // TODO: Polymorphism logic for classes like this
        module.addDeserializer(TextChannelImpl.class, new TextChannelDeserializer());

        om.registerModule(module);
    }

    public static String write(DataHolder dataHolder) throws JsonProcessingException {
        Map<String, Value<?>> map = new HashMap<>();
        dataHolder.getKeys().forEach((key, value) -> map.put(key.getResourceKey().toString(), value));
        return om.writeValueAsString(map);
    }

    public static Map<Key<?>, Value<?>> read(String json) throws JsonProcessingException {
        Map<String, Value<?>> values = om.readValue(json, new TypeReference<>() {});
        Map<Key<?>, Value<?>> result = new HashMap<>();
        // Find the key for each value
        values.forEach((key, value) -> {
            String[] split = key.split(":");
            String namespace = split[0];
            String id = split[1];
            Key<?> registeredKey = Atom.getRegistry(Key.class).from(ResourceKey.of(namespace, id)).orElseThrow();
//            System.out.println("Type: " + registeredKey.getType());
//            Class<?> clazz = (Class<?>)registeredKey.getType();
//            System.out.println(registeredKey.getType() instanceof ParameterizedType);
//            System.out.println("Casting to: " + clazz);
            result.put(registeredKey, value);
        });
        return result;
    }
}
