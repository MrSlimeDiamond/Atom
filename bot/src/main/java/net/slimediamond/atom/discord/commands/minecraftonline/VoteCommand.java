package net.slimediamond.atom.discord.commands.minecraftonline;

import net.dv8tion.jda.api.EmbedBuilder;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.reference.EmbedReference;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class VoteCommand implements DiscordCommandExecutor {

    private static final List<VotingSite> SITES = List.of(
            new VotingSite("Minecraft Server List", "https://minecraft-server-list.com/server/267113/vote/"),
            new VotingSite("Planet Minecraft", "https://www.planetminecraft.com/server/minecraft-online-3375846/vote/")
    );

    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        System.out.println("Command invoked");
        context.replyEmbeds(new EmbedBuilder()
                .setColor(Color.GREEN)
                .setAuthor("MinecraftOnline - Voting sites", null, "https://i.slimediamond.net/64px-Gold_Ingot_JE1-4064757708.png")
                .setDescription(VoteCommand.SITES.stream()
                        .map(site -> " * [" + site.title + "](" + site.url + ")")
                        .collect(Collectors.joining("\n")))
                .setFooter(EmbedReference.mcoFooter, EmbedReference.mcoIcon)
                .build());
    }

    private record VotingSite(String title, String url) {

    }

}
