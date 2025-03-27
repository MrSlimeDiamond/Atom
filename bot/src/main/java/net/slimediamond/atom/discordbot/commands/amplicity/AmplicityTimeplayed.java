package net.slimediamond.atom.discordbot.commands.amplicity;

import net.dv8tion.jda.api.EmbedBuilder;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.reference.EmbedReference;
import net.slimediamond.atom.util.EmbedUtil;
import net.slimediamond.util.minecraft.MinecraftUtils;
import org.yaml.snakeyaml.Yaml;

import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

// TODO: Get a correct name from something which might be shortened
// TODO: i.e. MrSlimeDia
public class AmplicityTimeplayed implements DiscordCommandExecutor {
    // This is literally a sshfs mount on the bot's host system
    // Super dumb. And it's hard-coded
    // What could go wrong?
    public static File PLAYERDATA_FILE = new File("/home/atom/amplicity_players");

    public void execute(DiscordCommandContext context) throws IOException {
        if (!PLAYERDATA_FILE.exists()) {
            context.replyEmbeds(EmbedUtil.expandedErrorEmbed("Playerdata directory could not be found - it is likely the host system was rebooted."));
            return;
        }

        AtomicReference<String> username = new AtomicReference();
        context.getArguments().get("username").ifPresentOrElse(arg -> username.set(arg.getAsString()), () -> username.set(context.getSender().getName()));

        MinecraftUtils.getPlayerUUID(username.get()).ifPresentOrElse(uuid -> {
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
                BigDecimal hours = new BigDecimal(String.valueOf(playerdata.get("onlinetime"))).divide(new BigDecimal(3600000), 2, RoundingMode.HALF_UP);

                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                context.replyEmbeds(new EmbedBuilder()
                        .setAuthor(username.get(), null, "https://mc-heads.net/avatar/" + username.get())
                        .setDescription(username.get() + " has " + hours + " hours on Amplicity.")
                        .setFooter(EmbedReference.amplicityFooter, EmbedReference.amplicityIcon)
                        .setColor(Color.GREEN)
                        .build()
                );
            } else {
                context.replyEmbeds(EmbedUtil.expandedErrorEmbed("Could not find that player!"));
            }

        }, () -> {
            // UUID was not present, the player probably doesn't exist in Minecraft
            context.replyEmbeds(EmbedUtil.expandedErrorEmbed("Could not find that player!"));
        });

    }
}
