package net.slimediamond.atom.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.command.discord.args.UserArgument;
import net.slimediamond.atom.reference.EmbedReference;
import net.slimediamond.atom.util.HTTPUtil;
import net.slimediamond.util.portal2.Portal2Util;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.Optional;

public class Portal2BoardCommand implements DiscordCommandExecutor {

    public void execute(DiscordCommandContext context) throws IOException {
        context.deferReply();
        Optional<UserArgument> arg = context.getArguments().get("username");

        String username;
        if (arg.isPresent()) {
            username = arg.get().getAsString();
        } else {
            username = context.getSender().getName();
        }

        HTTPUtil.getJsonDataFromURL("https://board.portal2.sr/profile/" + username + "/json").ifPresent(json -> {
            if (json.isNull("profileNumber")) {
                MessageEmbed embed = new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTitle("User does not exist")
                        .setDescription("User " + username + " is not on board.portal2.sr")
                        .build();
                context.replyEmbeds(embed);
            } else {
                JSONObject userData = json.getJSONObject("userData");
                String correctName = userData.getString("displayName");
                String avatarURL = userData.getString("avatar");

                int singleplayerPoints = json.getJSONObject("points").getJSONObject("SP").getInt("score");

                int singleplayerRank;
                if (json.getJSONObject("points").getJSONObject("SP").isNull("playerRank")) {
                    singleplayerRank = 0;
                } else {
                    singleplayerRank = json.getJSONObject("points").getJSONObject("SP").getInt("playerRank");
                }

                int coopPoints = json.getJSONObject("points").getJSONObject("COOP").getInt("score");
                int coopRank;
                if (json.getJSONObject("points").getJSONObject("COOP").isNull("playerRank")) {
                    coopRank = 0;
                } else {
                    coopRank = json.getJSONObject("points").getJSONObject("COOP").getInt("playerRank");
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
                    Portal2Util.getMapByID(bestRankJSON.getInt("map")).ifPresent(p2Map -> bestRank.append("Map: ").append(p2Map.getFormattedName()).append("\n"));
                }

                if (worstRankJSON.get("map").equals("several chambers")) {
                    worstRank.append("Map: Multiple\n");
                } else {
                    Portal2Util.getMapByID(worstRankJSON.getInt("map")).ifPresent(p2Map -> worstRank.append("Map: ").append(p2Map.getFormattedName()).append("\n"));
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

                context.replyEmbeds(embed);
            }
        });
    }
}
