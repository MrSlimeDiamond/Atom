package net.zenoc.atom.discordbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.zenoc.atom.discordbot.CommandEvent;
import net.zenoc.atom.discordbot.annotations.Command;
import net.zenoc.atom.discordbot.annotations.Option;
import net.zenoc.atom.discordbot.annotations.Subcommand;
import net.zenoc.atom.reference.EmbedReference;
import net.zenoc.atom.util.EmbedUtil;
import net.zenoc.atom.util.HTTPUtil;
import net.zenoc.atom.util.portal2.Portal2;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;

public class PortalCommands {

    @Command(
            name = "portal2",
            description = "Portal 2 speedrunning commands",
            usage = "portal2 cm",
            subcommands = {
                    @Subcommand(
                            name = "cm",
                            description = "Show CM individual level stats for a user",
                            usage = "portal2 cm <user>",
                            options = {
                                    @Option(
                                            name = "username",
                                            id = 0,
                                            type = OptionType.STRING,
                                            description = "Username of the player to lookup",
                                            required = true
                                    )
                            }
                    )
            },
            whitelistedGuilds = {696218632618901504L, 1004897099017637979L}
    )
    public void portalCommand(CommandEvent event) {
        if (!event.isSubCommand()) {
            event.replyEmbeds(EmbedUtil.genericIncorrectUsageEmbed("portal2 cm <username>"));
        }
        if (event.getSubcommandName().equals("cm")) {
            event.deferReply();
            String username = event.getStringOption("username");

            try {
                HTTPUtil.getJsonDataFromURL("https://board.portal2.sr/profile/" + username + "/json").ifPresent(json -> {
                    if (json.isNull("profileNumber")) {
                        MessageEmbed embed = new EmbedBuilder()
                                .setColor(Color.RED)
                                .setTitle("User does not exist")
                                .setDescription("User " + username + " is not on board.portal2.sr")
                                .build();
                        event.replyEmbeds(embed);
                    } else {
                        JSONObject userData = json.getJSONObject("userData");
                        String correctName = userData.getString("displayName");
                        String avatarURL = userData.getString("avatar");

                        int singleplayerPoints = json.getJSONObject("points").getJSONObject("SP").getInt("score");

                        int singleplayerRank;
                        if (json.getJSONObject("points").getJSONObject("SP").isNull("playerRank")) {
                            singleplayerRank = 0;
                        } else {
                            singleplayerRank =json.getJSONObject("points").getJSONObject("SP").getInt("playerRank");
                        }

                        int coopPoints = json.getJSONObject("points").getJSONObject("COOP").getInt("score");
                        int coopRank;
                        if (json.getJSONObject("points").getJSONObject("COOP").isNull("playerRank")) {
                            coopRank = 0;
                        } else {
                            coopRank =json.getJSONObject("points").getJSONObject("COOP").getInt("playerRank");
                        }


                        int overallPoints = json.getJSONObject("points").getJSONObject("global").getInt("score");
                        int overallRank = json.getJSONObject("points").getJSONObject("global").getInt("playerRank");

                        int singleplayerWorldRecords = json.getJSONObject("times").getJSONObject("SP").getJSONObject("chambers").getInt("numWRs");
                        int coopWorldRecords = json.getJSONObject("times").getJSONObject("COOP").getJSONObject("chambers").getInt("numWRs");
                        int overallWorldRecords = singleplayerWorldRecords + coopWorldRecords;

                        StringBuilder bestRank = new StringBuilder();
                        StringBuilder worstRank = new StringBuilder();

                        String singleplayer = "SP Points: " +
                                singleplayerPoints +
                                "\nSP Rank: " +
                                (singleplayerRank == 0 ? "Unranked" : singleplayerRank);

                        String coop = "Coop Points: " +
                                coopPoints +
                                "\nCoop Rank: " +
                                (coopRank == 0 ? "Unranked" : coopRank);

                        String overall = "Overall Points: " +
                                overallPoints +
                                "\nOverall Rank: " +
                                overallRank;

                        String worldRecords = "SP Records: " +
                                singleplayerWorldRecords +
                                "\nCoop Records: " +
                                coopWorldRecords +
                                "\nOverall Records: " +
                                overallWorldRecords;

                        JSONObject bestRankJSON = json.getJSONObject("times").getJSONObject("bestRank");
                        JSONObject worstRankJSON = json.getJSONObject("times").getJSONObject("worstRank");

                        if (bestRankJSON.get("map").equals("several chambers")) {
                            bestRank.append("Map: Multiple\n");
                        } else {
                            Portal2.getMapByID(bestRankJSON.getInt("map")).ifPresent(p2Map -> bestRank.append("Map: ").append(p2Map.getFormattedName()).append("\n"));
                        }

                        if (worstRankJSON.get("map").equals("several chambers")) {
                            worstRank.append("Map: Multiple\n");
                        } else {
                            Portal2.getMapByID(worstRankJSON.getInt("map")).ifPresent(p2Map -> worstRank.append("Map: ").append(p2Map.getFormattedName()).append("\n"));
                        }

                        bestRank.append("Rank: ");
                        bestRank.append(bestRankJSON.getJSONObject("scoreData").getString("playerRank"));
                        bestRank.append("\n");

                        worstRank.append("Rank: ");
                        worstRank.append(worstRankJSON.getJSONObject("scoreData").getString("playerRank"));
                        worstRank.append("\n");


                        // TODO: Most common colour as embed colour
                        MessageEmbed embed = new EmbedBuilder()
                                .setAuthor(correctName, null, avatarURL)
                                .setTitle(correctName + "'s Portal 2 CM")
                                .setThumbnail(avatarURL)
                                .addField("Singleplayer", singleplayer, true)
                                .addField("Cooperative", coop, true)
                                .addField("Overall", overall, true)
                                .addField("Best Rank", bestRank.toString(), true)
                                .addField("Worst Rank", worstRank.toString(), true)
                                .addField("World Records", worldRecords, true)
                                .setFooter(EmbedReference.p2boardsFooter, EmbedReference.p2boardsIcon)
                                .build();

                        event.replyEmbeds(embed);
                    }
                });
            } catch (IOException e) {
                event.replyEmbeds(EmbedUtil.genericErrorEmbed());
            }
        }
    }
}
