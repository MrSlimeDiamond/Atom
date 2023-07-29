package net.slimediamond.atom.inject.providers;


import com.google.inject.Provider;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.slimediamond.atom.reference.DiscordReference;

public class JDAProvider implements Provider<JDA> {
    private static JDA jda;
    @Override
    public JDA get() {
        if (jda == null) {
            JDABuilder builder = JDABuilder.createDefault(DiscordReference.token);
            builder.enableIntents(
                    GatewayIntent.MESSAGE_CONTENT,
                    GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.GUILD_WEBHOOKS,
                    GatewayIntent.GUILD_MESSAGE_REACTIONS
            );
            builder.setMemberCachePolicy(MemberCachePolicy.ALL);
            builder.setChunkingFilter(ChunkingFilter.ALL);

            jda = builder.build();
        }

        return jda;
    }
}
