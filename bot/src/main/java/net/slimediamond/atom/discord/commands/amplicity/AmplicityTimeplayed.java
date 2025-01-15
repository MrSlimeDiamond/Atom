package net.slimediamond.atom.discord.commands.amplicity;

import net.dv8tion.jda.api.EmbedBuilder;
import net.slimediamond.atom.discord.CommandEvent;
import net.slimediamond.atom.discord.annotations.Command;
import net.slimediamond.atom.reference.DiscordReference;
import net.slimediamond.atom.reference.EmbedReference;
import net.slimediamond.atom.util.EmbedUtil;
import net.slimediamond.util.minecraft.MinecraftUtils;
import org.yaml.snakeyaml.Yaml;

import java.awt.*;
import java.io.*;
import java.util.Map;

public class AmplicityTimeplayed {
    // This is literally a sshfs mount on the bot's host system
    // Super dumb. And it's hard-coded
    // What could go wrong?
    public static File PLAYERDATA_FILE = new File("/home/atom/amplicity_players");

    @Command(
            name = "timeplayed",
            aliases = {"playtime", "pt", "tp"},
            description = "Get a player's hour count on Amplicity",
            usage = "timeplayed <player>",
            whitelistedGuilds = { 1048920042655449138L }
    )
    public void timeplayedCommand(CommandEvent event) throws IOException {
        if (!PLAYERDATA_FILE.exists()) {
            event.replyEmbeds(EmbedUtil.expandedErrorEmbed("Playerdata directory could not be found - it is likely the host system was rebooted."));
            return;
        }

        String username;
        event.sendIncorrectUsageForCommandArgs(false);
        if (event.getCommandArgs() == null) {
            username = event.getAuthor().getName();
        } else {
            username = event.getCommandArgs()[0];
        }

        MinecraftUtils.getPlayerUUID(username).ifPresentOrElse(uuid -> {
            // UUID is present, we may look at the directory now

            // Convert to UUID with dashes
            String uuidDashed = uuid.substring(0, 8) + "-"
                    + uuid.substring(8, 12) + "-"
                    + uuid.substring(12, 16) + "-"
                    + uuid.substring(16, 20) + "-"
                    + uuid.substring(20);

            File playerFile = new File(PLAYERDATA_FILE + "/" + uuidDashed + ".yaml");
            if (playerFile.exists()) {
                // Technically, we don't actually need to do any fancy YAML parsing
                // however, in case there's some random thing in the way, it's better to
                // parse it properly.

                Yaml yaml = new Yaml();
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(playerFile);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                Map<String, Object> playerdata = yaml.load(inputStream);

                // I don't know
                int hours = (Integer) playerdata.get("onlinetime") / 3600 / 1000;
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                event.replyEmbeds(new EmbedBuilder()
                        .setAuthor(username, null, "https://mc-heads.net/avatar/" + username)
                        .setDescription(username + " has " + hours + " hours on Amplicity.")
                        .setFooter(EmbedReference.amplicityFooter, EmbedReference.amplicityIcon)
                        .setColor(Color.GREEN)
                        .build()
                );
            } else {
                event.replyEmbeds(EmbedUtil.expandedErrorEmbed("Could not find that player!"));
            }

        }, () -> {
            // UUID was not present, the player probably doesn't exist in Minecraft
            event.replyEmbeds(EmbedUtil.expandedErrorEmbed("Could not find that player!"));
        });

    }
}
