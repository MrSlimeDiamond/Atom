/*
Requires MariaDB
Creation statements: not all of them lmao

create table guilds (GuildID BIGINT, LogChannel BIGINT, PinnerinoChannel BIGINT, PinnerinoEmoji varchar(256), PinnerinoThreshold INT, StreamsChannel BIGINT)
create table messages (MessageID BIGINT, GuildID BIGINT, AuthorID BIGINT, MessageContent LONGTEXT)
create table pinnerino_blacklist (ChannelID BIGINT)
create table irc_channels (ChannelName TINYTEXT, DiscordChannel BIGINT, Pipe BOOLEAN, AutoJoin BOOLEAN, BridgeIcon TINYTEXT);


alter table guilds add StreamsChannel BIGINT
 */

package net.zenoc.atom.services;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.zenoc.atom.Atom;
import net.zenoc.atom.discordbot.CachedMessage;
import net.zenoc.atom.util.NumberUtils;
import net.zenoc.atom.reference.DBReference;
import net.zenoc.atom.reference.IRCReference;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class AtomDatabase implements Service {
    private static final Logger log = LoggerFactory.getLogger(AtomDatabase.class);
    private Connection conn;

    private PreparedStatement isDiscordAdminByID;

    private PreparedStatement updateServerLogChannel;
    private PreparedStatement getServerLogChannel;

    private PreparedStatement insertGuild;
    private PreparedStatement isGuildInDatabase;

    private PreparedStatement insertMessage;
    private PreparedStatement getMessage;
    private PreparedStatement updateMessage;

    private PreparedStatement getServerPinnerinoThreshold;
    private PreparedStatement getServerPinnerinoEmoji;
    private PreparedStatement getServerPinnerinoChannel;
    private PreparedStatement setServerPinnerinoThreshold;
    private PreparedStatement setServerPinnerinoEmoji;
    private PreparedStatement setServerPinnerinoChannel;
    private PreparedStatement insertPinnerino;
    private PreparedStatement isMessagePinnerinoed;
    private PreparedStatement addPinnerinoBlacklist;
    private PreparedStatement removePinnerinoBlacklist;
    private PreparedStatement channelBlacklistedPinnerino;
    private PreparedStatement addIRCChannel;
    private PreparedStatement setIRCDiscordBridgeChannelID;
    private PreparedStatement enableIRCPipe;
    private PreparedStatement disableIRCPipe;
    private PreparedStatement disableIRCAutojoin;
    private PreparedStatement enableIRCAutojoin;
    private PreparedStatement getIRCChannels;

    private PreparedStatement isIRCAdmin;

    private PreparedStatement isChannelBridged;
    private PreparedStatement isPipeEnabled;

    private PreparedStatement getDiscordChannel;
    private PreparedStatement getBridgeIcon;
    private PreparedStatement getDiscordChannelsList;
    private PreparedStatement addBlacklistIRC;
    private PreparedStatement removeBlacklistIRC;
    private PreparedStatement addBlacklistDiscord;
    private PreparedStatement removeBlacklistDiscord;
    private PreparedStatement isBlacklistIRC;
    private PreparedStatement isBlacklistDiscord;
    private PreparedStatement getMCOFirstseenByUUID;
    private PreparedStatement getMCOFirstseenByUsername;
    private PreparedStatement getMCOLastseenByUUID;
    private PreparedStatement getMCOLastseenByUsername;
    private PreparedStatement insertMCOUser;
    private PreparedStatement setMCOFirstseenByUsername;
    private PreparedStatement setMCOLastseenByUsername;
    private PreparedStatement setMCOFirstseenByUUID;
    private PreparedStatement setMCOLastseenByUUID;

    private PreparedStatement isMCOUserInDatabaseUsername;
    private PreparedStatement isMCOUserInDatabaseUUID;

    private PreparedStatement getPinnerino;

    private PreparedStatement insertReactionRole;
    private PreparedStatement messageHasReactionRoles;
    private PreparedStatement getReactionRole;
    private PreparedStatement removeReactionRole;
    private PreparedStatement getReactionRoleEmoji;
    private PreparedStatement removeAllReactionRoles;

    private PreparedStatement setServerStreamsChannel;
    private PreparedStatement getServerStreamsChannel;
    private PreparedStatement getServerStreamers;
    private PreparedStatement insertServerStreamer;
    private PreparedStatement deleteServerStreamer;

    private PreparedStatement getServerMemesChannel;
    private PreparedStatement setServerMemesChannel;
    @Override
    public void startService() throws Exception {
        openConnection();
    }

    private void openConnection() throws SQLException {
        conn = DriverManager.getConnection(
                "jdbc:mariadb://" +
                DBReference.host +
                ":" +
                DBReference.port +
                "/" +
                DBReference.database +
                "?user=" +
                DBReference.user +
                "&password=" +
                DBReference.password +
                "&autoReconnect=true&allowPublicKeyRetrieval=true");


        isDiscordAdminByID = conn.prepareStatement("SELECT * FROM admins WHERE DiscordID = ?");

        updateServerLogChannel = conn.prepareStatement("UPDATE guilds SET LogChannel = ? WHERE GuildID = ?");
        getServerLogChannel = conn.prepareStatement("SELECT LogChannel FROM guilds WHERE GuildID = ?");

        insertGuild = conn.prepareStatement("INSERT INTO guilds (GuildID) VALUES (?)");
        isGuildInDatabase = conn.prepareStatement("SELECT * FROM guilds WHERE GuildID = ?");

        insertMessage = conn.prepareStatement("INSERT INTO messages (MessageID, GuildID, AuthorID, MessageContent) VALUES (?, ?, ?, ?)");
        getMessage = conn.prepareStatement("SELECT * FROM messages WHERE MessageID = ?");
        updateMessage = conn.prepareStatement("UPDATE messages SET MessageContent = ? WHERE MessageID = ?");

        getServerPinnerinoThreshold = conn.prepareStatement("SELECT PinnerinoThreshold FROM guilds WHERE GuildID = ?");
        getServerPinnerinoChannel = conn.prepareStatement("SELECT PinnerinoChannel FROM guilds WHERE GuildID = ?");
        getServerPinnerinoEmoji = conn.prepareStatement("SELECT PinnerinoEmoji FROM guilds WHERE GuildID = ?");
        setServerPinnerinoThreshold = conn.prepareStatement("UPDATE guilds SET PinnerinoThreshold = ? WHERE GuildID = ?");
        setServerPinnerinoChannel = conn.prepareStatement("UPDATE guilds SET PinnerinoChannel = ? WHERE GuildID = ?");
        setServerPinnerinoEmoji = conn.prepareStatement("UPDATE guilds SET PinnerinoEmoji = ? WHERE GuildID = ?");
        addPinnerinoBlacklist = conn.prepareStatement("INSERT INTO pinnerino_blacklist (ChannelID) VALUES (?)");
        removePinnerinoBlacklist = conn.prepareStatement("DELETE FROM pinnerino_blacklist WHERE ChannelID = ?");
        channelBlacklistedPinnerino = conn.prepareStatement("SELECT * FROM pinnerino_blacklist WHERE ChannelID = ?");
        insertPinnerino = conn.prepareStatement("INSERT INTO pinnerino (GuildID, MessageID, PinnerinoMessageID) VALUES (?, ?, ?)");
        isMessagePinnerinoed = conn.prepareStatement("SELECT * FROM pinnerino WHERE MessageID = ?");
        getPinnerino = conn.prepareStatement("SELECT PinnerinoMessageID FROM pinnerino WHERE MessageID = ?");

        addIRCChannel = conn.prepareStatement("INSERT INTO irc_channels (ChannelName) VALUES (?)");
        setIRCDiscordBridgeChannelID = conn.prepareStatement("UPDATE irc_channels SET DiscordChannel = ? WHERE ChannelName = ?");

        enableIRCAutojoin = conn.prepareStatement("UPDATE irc_channels SET AutoJoin = TRUE WHERE ChannelName = ?");
        disableIRCAutojoin = conn.prepareStatement("UPDATE irc_channels SET AutoJoin = FALSE WHERE ChannelName = ?");

        getIRCChannels = conn.prepareStatement("SELECT * FROM irc_channels");

        isIRCAdmin = conn.prepareStatement("SELECT * FROM admins WHERE IRCNick = ? AND IRCHostname = ?");

        enableIRCPipe = conn.prepareStatement("UPDATE irc_channels SET Pipe = TRUE Where ChannelName = ?");
        disableIRCPipe = conn.prepareStatement("UPDATE irc_channels SET Pipe = FALSE Where ChannelName = ?");

        isChannelBridged = conn.prepareStatement("SELECT * FROM irc_channels WHERE DiscordChannel = ?");

        isPipeEnabled = conn.prepareStatement("SELECT * FROM irc_channels WHERE ChannelName = ? AND Pipe = TRUE");

        getDiscordChannel = conn.prepareStatement("SELECT DiscordChannel FROM irc_channels WHERE ChannelName = ?");

        getBridgeIcon = conn.prepareStatement("SELECT BridgeIcon FROM irc_channels WHERE ChannelName = ?");

        getDiscordChannelsList = conn.prepareStatement("SELECT DiscordChannel FROM irc_channels");

        addBlacklistIRC = conn.prepareStatement("INSERT INTO bridge_blacklist (IRCNick) VALUES (?)");
        removeBlacklistIRC = conn.prepareStatement("DELETE FROM bridge_blacklist WHERE IRCNICK = ?");

        addBlacklistDiscord = conn.prepareStatement("INSERT INTO bridge_blacklist (DiscordID) VALUES (?)");
        removeBlacklistDiscord = conn.prepareStatement("DELETE FROM bridge_blacklist WHERE DiscordID = ?");

        isBlacklistIRC = conn.prepareStatement("SELECT * FROM bridge_blacklist WHERE IRCNick = ?");
        isBlacklistDiscord = conn.prepareStatement("SELECT * FROM bridge_blacklist WHERE DiscordID = ?");

        insertMCOUser = conn.prepareStatement("INSERT INTO minecraftonline_users (MinecraftName, UUID) VALUES (?, ?)");

        getMCOFirstseenByUUID = conn.prepareStatement("SELECT * FROM minecraftonline_users WHERE UUID = ?");
        getMCOFirstseenByUsername = conn.prepareStatement("SELECT Firstseen FROM minecraftonline_users WHERE MinecraftName = ?");
        getMCOLastseenByUUID = conn.prepareStatement("SELECT Lastseen FROM minecraftonline_users WHERE UUID = ?");
        getMCOLastseenByUsername = conn.prepareStatement("SELECT Lastseen FROM minecraftonline_users WHERE MinecraftName = ?");

        setMCOFirstseenByUsername = conn.prepareStatement("UPDATE minecraftonline_users SET Firstseen = ? WHERE MinecraftName = ?");
        setMCOFirstseenByUUID = conn.prepareStatement("UPDATE minecraftonline_users SET Firstseen = ? WHERE UUID = ?");
        setMCOLastseenByUsername = conn.prepareStatement("UPDATE minecraftonline_users SET Lastseen = ? WHERE MinecraftName = ?");
        setMCOLastseenByUUID = conn.prepareStatement("UPDATE minecraftonline_users SET Lastseen = ? WHERE UUID = ?");

        isMCOUserInDatabaseUsername = conn.prepareStatement("SELECT * FROM minecraftonline_users WHERE MinecraftName = ?");
        isMCOUserInDatabaseUUID = conn.prepareStatement("SELECT * FROM minecraftonline_users UUID MinecraftName = ?");

        insertReactionRole = conn.prepareStatement("INSERT INTO reaction_roles (MessageID, Emoji, RoleID) VALUES (?, ?, ?)");
        messageHasReactionRoles = conn.prepareStatement("SELECT * FROM reaction_roles WHERE MessageID = ?");
        getReactionRole = conn.prepareStatement("SELECT RoleID FROM reaction_roles WHERE MessageID = ? AND Emoji = ?");
        removeReactionRole = conn.prepareStatement("DELETE FROM reaction_roles WHERE MessageID = ? AND RoleID = ?");
        getReactionRoleEmoji = conn.prepareStatement("SELECT Emoji FROM reaction_roles WHERE MessageID = ? AND RoleID = ?");
        removeAllReactionRoles = conn.prepareStatement("DELETE FROM reaction_roles WHERE MessageID = ?");

        setServerStreamsChannel = conn.prepareStatement("UPDATE guilds SET StreamsChannel = ? WHERE GuildID = ?");
        getServerStreamsChannel = conn.prepareStatement("SELECT StreamsChannel FROM guilds WHERE GuildID = ?");
        getServerStreamers = conn.prepareStatement("SELECT * FROM streamers WHERE GuildID = ?");
        insertServerStreamer = conn.prepareStatement("INSERT INTO streamers (GuildID, Login) VALUES (?, ?)");
        deleteServerStreamer = conn.prepareStatement("DELETE FROM streamers WHERE GuildID = ? AND Login = ?");

        getServerMemesChannel = conn.prepareStatement("SELECT MemesChannel FROM guilds WHERE GuildID = ?");
        setServerMemesChannel = conn.prepareStatement("UPDATE guilds SET MemesChannel = ? WHERE GuildID = ?");
    }
    public AtomDatabase() {}

    public boolean isDiscordAdminByID(long idLong) throws SQLException {
        isDiscordAdminByID.setLong(1, idLong);
        ResultSet resultSet = isDiscordAdminByID.executeQuery();
        return resultSet.next();
    }

    public void updateServerLogChannel(long guildID, long channelID) throws SQLException {
        updateServerLogChannel.setLong(1, channelID);
        updateServerLogChannel.setLong(2, guildID);
        updateServerLogChannel.execute();
    }

    public long getServerLogChannel(long guildID) throws SQLException {
        getServerLogChannel.setLong(1, guildID);
        ResultSet resultSet = getServerLogChannel.executeQuery();
        if (resultSet.next()) {
            long channel = resultSet.getLong("LogChannel");
            return channel;
        }
        return -1L;
    }

    public Optional<TextChannel> getGuildLog(Guild guild) {
        try {
            long channelID = getServerLogChannel(guild.getIdLong());
            TextChannel channel = DiscordBot.jda.getTextChannelById(channelID);
            if (channel == null) return Optional.empty();
            return Optional.of(channel);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addGuild(long guildID) throws SQLException {
        insertGuild.setLong(1, guildID);
        insertGuild.execute();
    }

    public void addMessage(long messageID, long guildID, long authorID, String content) throws SQLException {
        insertMessage.setLong(1, messageID);
        insertMessage.setLong(2, guildID);
        insertMessage.setLong(3, authorID);
        insertMessage.setString(4, content);
        insertMessage.execute();
    }

    public Optional<CachedMessage> getMessage(long messageID) {
        try {
            getMessage.setLong(1, messageID);
            ResultSet resultSet = getMessage.executeQuery();

            if (resultSet.next()) {
                long authorID = resultSet.getLong("AuthorID");
                long guildID = resultSet.getLong("GuildID");
                String messageContent = resultSet.getString("MessageContent");
                CachedMessage msg = new CachedMessage(DiscordBot.jda.retrieveUserById(authorID).complete(), messageContent, DiscordBot.jda.getGuildById(guildID));

                return Optional.of(msg);
            } else {
                return Optional.empty();
            }
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateMessage(long messageID, String content) {
        try {
            updateMessage.setLong(2, messageID);
            updateMessage.setString(1, content);
            updateMessage.execute();
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Long getServerPinnerinoChannel(long guildID) {
        try {
            getServerPinnerinoChannel.setLong(1, guildID);
            ResultSet resultSet = getServerPinnerinoChannel.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("PinnerinoChannel");
            }
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Optional<TextChannel> getServerPinnerinoChannel(Guild guild) {
        long id = getServerPinnerinoChannel(guild.getIdLong());
        TextChannel channel = DiscordBot.jda.getTextChannelById(id);
        if (channel != null) return Optional.of(channel);
        return Optional.empty();
    }
    public Optional<Long> getServerPinnerinoThreshold(Guild guild) {
        try {
            getServerPinnerinoThreshold.setLong(1, guild.getIdLong());
            ResultSet resultSet = getServerPinnerinoThreshold.executeQuery();
            if (resultSet.next()) {
                return Optional.of(resultSet.getLong("PinnerinoThreshold"));
                //return resultSet.getLong("PinnerinoThreshold");
            }
            return Optional.empty();
            //return -1L;
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getServerPinnerinoEmoji(long guildID) throws SQLException {
        getServerPinnerinoEmoji.setLong(1, guildID);
        ResultSet resultSet = getServerPinnerinoEmoji.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString("PinnerinoEmoji");
        }
        return null;
    }
    public void setServerPinnerinoThreshold(long guildID, int threshold) throws SQLException {
        setServerPinnerinoThreshold.setLong(2, guildID);
        setServerPinnerinoThreshold.setInt(1, threshold);
        setServerPinnerinoThreshold.execute();
    }

    public void setServerPinnerinoChannel(long guildID, long channel) throws SQLException {
        setServerPinnerinoChannel.setLong(2, guildID);
        setServerPinnerinoChannel.setLong(1, channel);
        setServerPinnerinoChannel.execute();
    }

    public void setServerPinnerinoEmoji(long guildID, String emoji) throws SQLException {
        setServerPinnerinoEmoji.setLong(2, guildID);
        setServerPinnerinoEmoji.setString(1, emoji);
        setServerPinnerinoEmoji.execute();
    }

    public void addPinnerino(long guildID, long messageID, long pinnerinoMessageID) {
        try {
            insertPinnerino.setLong(1, guildID);
            insertPinnerino.setLong(2, messageID);
            insertPinnerino.setLong(3, pinnerinoMessageID);
            insertPinnerino.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addPinnerinoBlacklist(long channelID) throws SQLException {
        addPinnerinoBlacklist.setLong(1, channelID);
        addPinnerinoBlacklist.execute();
    }

    public void removePinnerinoBlacklist(long channelID) throws SQLException {
        removePinnerinoBlacklist.setLong(1, channelID);
        removePinnerinoBlacklist.execute();
    }

    public Optional<RestAction<Message>> getPinnerino(Guild guild, long messageID) {
        try {
            getPinnerino.setLong(1, messageID);
            ResultSet resultSet = getPinnerino.executeQuery();
            if (resultSet.next()) {
                long pinMessageID = resultSet.getLong("PinnerinoMessageID");
                AtomicReference<TextChannel> channel = new AtomicReference<>();
                this.getServerPinnerinoChannel(guild).ifPresent(channel::set);
                return Optional.of(channel.get().retrieveMessageById(pinMessageID));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public boolean isChannelBlacklistedPinnerino(long channelID) {
        try {
            channelBlacklistedPinnerino.setLong(1, channelID);
            ResultSet resultSet = channelBlacklistedPinnerino.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isMessagePinnerinoed(long messageID) {
        try {
            isMessagePinnerinoed.setLong(1, messageID);
            ResultSet resultSet = isMessagePinnerinoed.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addIRCChannel(String channelName) throws SQLException {
        addIRCChannel.setString(1, channelName);
        addIRCChannel.execute();
    }

    public void enableIRCAutojoin(String channelName) throws SQLException {
        enableIRCAutojoin.setString(1, channelName);
        enableIRCAutojoin.execute();
    }

    public void disableIRCAutojoin(String channelName) throws SQLException {
        disableIRCAutojoin.setString(1, channelName);
        disableIRCAutojoin.execute();
    }

    public void joinAllIRCChannels() throws SQLException {
        ResultSet resultSet = getIRCChannels.executeQuery();
        while (resultSet.next()) {
            if (resultSet.getBoolean("AutoJoin")) {
                String channelName = resultSet.getString("ChannelName");
                IRC.client.addChannel(channelName);
            }
        }
    }

    public boolean isIRCAdmin(org.kitteh.irc.client.library.element.User user) throws SQLException {
        isIRCAdmin.setString(1, user.getNick());
        isIRCAdmin.setString(2, user.getHost());
        ResultSet resultSet = isIRCAdmin.executeQuery();
        return resultSet.next();
    }

    public void setIRCDiscordBridgeChannelID(String ircChannel, long discordChannel) throws SQLException {
        setIRCDiscordBridgeChannelID.setString(2, ircChannel);
        setIRCDiscordBridgeChannelID.setLong(1, discordChannel);
        setIRCDiscordBridgeChannelID.execute();
    }

    public void enableIRCPipe(String ircChannel) throws SQLException {
        enableIRCPipe.setString(1, ircChannel);
        enableIRCPipe.execute();

        // Send the pipe enable message
        TextChannel channel = DiscordBot.jda.getTextChannelById(getDiscordBridgeChannelID(ircChannel));
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setDescription("Bridge pipe enabled")
                .build();
        if (channel == null) return;
        channel.sendMessageEmbeds(embed).queue();
        IRC.client.sendMessage(ircChannel, "Bridge pipe on");
    }

    public void disableIRCPipe(String ircChannel) throws SQLException {
        disableIRCPipe.setString(1, ircChannel);
        disableIRCPipe.execute();

        // Send the pipe disable message
        TextChannel channel = DiscordBot.jda.getTextChannelById(getDiscordBridgeChannelID(ircChannel));
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.RED)
                .setDescription("Bridge pipe disabled")
                .build();
        if (channel == null) return;
        channel.sendMessageEmbeds(embed).queue();
        IRC.client.sendMessage(ircChannel, "Bridge pipe off");
    }

    public boolean isChannelBridged(long channelID) throws SQLException {
        isChannelBridged.setLong(1, channelID);
        ResultSet resultSet = isChannelBridged.executeQuery();
        return resultSet.next();
    }

    public String getIRCBridgeChannel(long discordChannel) throws SQLException {
        isChannelBridged.setLong(1, discordChannel);
        ResultSet resultSet = isChannelBridged.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString("ChannelName");
        } else {
            return null;
        }
    }

    public Optional<String> getBridgedChannel(Channel channel) {
        try {
            isChannelBridged.setLong(1, channel.getIdLong());
            ResultSet resultSet = isChannelBridged.executeQuery();
            if (resultSet.next()) {
                return Optional.of(resultSet.getString("ChannelName"));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long getDiscordBridgeChannelID(String channelName) throws SQLException {
        getDiscordChannel.setString(1, channelName);
        ResultSet resultSet = getDiscordChannel.executeQuery();
        if (resultSet.first()) {
            return resultSet.getLong("DiscordChannel");
        } else {
            return -1L;
        }
    }

    public boolean isPipeEnabled(String channelName) throws SQLException {
        isPipeEnabled.setString(1, channelName);
        ResultSet resultSet = isPipeEnabled.executeQuery();
        return resultSet.next();
    }

    public String getChannelBridgeIcon(String channelName) throws SQLException {
        getBridgeIcon.setString(1, channelName);
        ResultSet resultSet = getBridgeIcon.executeQuery();
        if (resultSet.next()) {
            String icon = resultSet.getString("BridgeIcon");
            if (icon == null) return IRCReference.defaultIcon;
            return icon;
        } else {
            return IRCReference.defaultIcon;
        }
    }

    public ArrayList<TextChannel> getBridgedChannelsDiscord() {
        try {
            ArrayList<TextChannel> channels = new ArrayList<>();
            ResultSet resultSet = getDiscordChannelsList.executeQuery();
            while (resultSet.next()) {
                long channel = resultSet.getLong("DiscordChannel");
                if (isPipeEnabled(getIRCBridgeChannel(channel))) {
                    channels.add(DiscordBot.jda.getTextChannelById(channel));
                }
            }
            return channels;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addUserIRCBridgeBlacklist(String nickname) throws SQLException {
        addBlacklistIRC.setString(1, nickname);
        addBlacklistIRC.execute();
    }

    public void removeUserIRCBridgeBlacklist(String nickname) throws SQLException {
        removeBlacklistIRC.setString(1, nickname);
        removeBlacklistIRC.execute();
    }

    public void addUserDiscordBridgeBlacklist(long userID) throws SQLException {
        addBlacklistDiscord.setLong(1, userID);
        addBlacklistDiscord.execute();
    }

    public void removeUserDiscordBridgeBlacklist(long userID) throws SQLException {
        removeBlacklistDiscord.setLong(1, userID);
        removeBlacklistDiscord.execute();
    }

    public EmojiUnion getServerPinnerinoEmojiUnion(long serverID) throws SQLException {
        String emoji = Atom.database.getServerPinnerinoEmoji(serverID);
        Guild guild = DiscordBot.jda.getGuildById(serverID);
        if (guild == null) return null;
        DataObject data;
        if (NumberUtils.isNumeric(emoji)) {
            // Unicode
            data = guild.retrieveEmojiById(emoji).complete().toData();
        } else {
            // Custom
            data = Emoji.fromUnicode(emoji).toData();
        }
        return Emoji.fromData(data);
    }

    public Optional<EmojiUnion> getServerPinnerinoEmoji(Guild guild) {
        try {
            long serverID = guild.getIdLong();
            String emoji = Atom.database.getServerPinnerinoEmoji(serverID);
            if (emoji == null) return Optional.empty();
            DataObject data;
            if (NumberUtils.isNumeric(emoji)) {
                data = guild.retrieveEmojiById(emoji).complete().toData();
            } else {
                data = Emoji.fromUnicode(emoji).toData();
            }
            return Optional.of(Emoji.fromData(data));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isUserBlacklisted(String nickname) throws SQLException {
        isBlacklistIRC.setString(1, nickname);
        ResultSet resultSet = isBlacklistIRC.executeQuery();
        return resultSet.next();
    }

    public boolean isUserBlacklisted(long id) throws SQLException {
        isBlacklistDiscord.setLong(1, id);
        ResultSet resultSet = isBlacklistDiscord.executeQuery();
        return resultSet.next();
    }

    public Optional<Date> getMCOFirstseenByName(String username) throws SQLException {
        getMCOFirstseenByUsername.setString(1, username);
        ResultSet resultSet = getMCOFirstseenByUsername.executeQuery();
        if (resultSet.next()) {
            Timestamp timestamp = resultSet.getTimestamp("Firstseen");
            if (timestamp == null) return Optional.empty();
            Date date = new Date(timestamp.getTime());
            return Optional.of(date);
        }
        return Optional.empty();
    }

    public void insertMCOUser(String username, String uuid) throws SQLException {
        insertMCOUser.setString(1, username);
        insertMCOUser.setString(2, uuid);
        insertMCOUser.execute();
    }

    public void setMCOFirstseenByUUID(String uuid, java.util.Date date) throws SQLException {
        setMCOFirstseenByUUID.setString(2, uuid);
        setMCOFirstseenByUUID.setTimestamp(1, Timestamp.from(date.toInstant())); // works I guess? Lol
        setMCOFirstseenByUUID.execute();
    }

    public boolean isMCOUserInDatabaseByUsername(String username) throws SQLException {
        isMCOUserInDatabaseUsername.setString(1, username);
        ResultSet resultSet = isMCOUserInDatabaseUsername.executeQuery();
        return resultSet.next();
    }

    public Optional<Date> getMCOLastseenByName(String username) throws SQLException {
        getMCOLastseenByUsername.setString(1, username);
        ResultSet resultSet = getMCOLastseenByUsername.executeQuery();
        if (resultSet.next()) {
            Timestamp timestamp = resultSet.getTimestamp("Lastseen");
            if (timestamp == null) return Optional.empty();
            Date date = new Date(timestamp.getTime());
            return Optional.of(date);
        }
        return Optional.empty();
    }

    public void setMCOLastseenByUUID(String uuid, Date date) throws SQLException {
        setMCOLastseenByUUID.setString(2, uuid);
        setMCOLastseenByUUID.setTimestamp(1, Timestamp.from(date.toInstant()));
        setMCOLastseenByUUID.execute();
    }

    public void insertReactionRole(long messageID, String emoji, long roleID) throws SQLException {
        insertReactionRole.setLong(1, messageID);
        insertReactionRole.setString(2, emoji);
        insertReactionRole.setLong(3, roleID);
        insertReactionRole.execute();
    }

    public boolean messageHasReactionRoles(long messageID) {
        try {
            messageHasReactionRoles.setLong(1, messageID);
            ResultSet resultSet = messageHasReactionRoles.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Role> getReactionRole(long messageID, EmojiUnion emoji) {
        try {
            SerializableData data = emoji.toData();
            String emojiID;
            if (data.toData().isNull("id")) {
                // Unicode emoji
                emojiID = emoji.asUnicode().getAsCodepoints();
            } else {
                // Custom emoji
                emojiID = emoji.asCustom().getId();
            }
            getReactionRole.setLong(1, messageID);
            getReactionRole.setString(2, emojiID);
            ResultSet resultSet = getReactionRole.executeQuery();
            if (resultSet.next()) {
                String roleID = resultSet.getString("RoleID");
                Role role = DiscordBot.jda.getRoleById(roleID);
                if (role == null) {
                    return Optional.empty();
                }
                return Optional.of(role);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeReactionRole(long messageID, long roleID) throws SQLException {
        removeReactionRole.setLong(1, messageID);
        removeReactionRole.setLong(2, roleID);
        removeReactionRole.execute();
    }

    public Optional<Emoji> getReactionRoleEmoji(Guild guild, long messageID, long roleID) throws SQLException {
        getReactionRoleEmoji.setLong(1, messageID);
        getReactionRoleEmoji.setLong(2, roleID);
        ResultSet resultSet = getReactionRoleEmoji.executeQuery();
        if (resultSet.next()) {
            DataObject data;
            String emoji = resultSet.getString("EmojiID");

            if (NumberUtils.isNumeric(emoji)) {
                data = guild.retrieveEmojiById(emoji).complete().toData();
            } else {
                data = Emoji.fromUnicode(emoji).toData();
            }

            return Optional.of(Emoji.fromData(data));
        }
        return Optional.empty();
    }

    public void removeAllReactionRoles(long messageID) throws SQLException {
        removeAllReactionRoles.setLong(1, messageID);
        removeAllReactionRoles.execute();
    }

    public boolean isGuildInDatabase(Guild guild) {
        try {
            isGuildInDatabase.setLong(1, guild.getIdLong());
            ResultSet resultSet = isGuildInDatabase.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setServerStreamsChannel(Guild guild, @Nullable TextChannel channel) throws SQLException {
        if (channel == null) {
            setServerStreamsChannel.setNull(1, Types.BIGINT);
        }
        setServerStreamsChannel.setLong(2, guild.getIdLong());
        setServerStreamsChannel.setLong(1, channel.getIdLong());
        setServerStreamsChannel.execute();
    }

    public Optional<TextChannel> getServerStreamsChannel(Guild guild) {
        try {
            getServerStreamsChannel.setLong(1, guild.getIdLong());
            ResultSet resultSet = getServerStreamsChannel.executeQuery();
            if (resultSet.next()) {
                return Optional.ofNullable(guild.getTextChannelById(resultSet.getLong("StreamsChannel")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Optional<ArrayList<String>> getServerStreamers(Guild guild) {
        ArrayList<String> logins = new ArrayList<>();
        try {
            getServerStreamers.setLong(1, guild.getIdLong());
            ResultSet resultSet = getServerStreamers.executeQuery();
            while (resultSet.next()) {
                logins.add(resultSet.getString("Login"));
            }
            if (logins.size() == 0) {
                return Optional.empty();
            } else {
                return Optional.of(logins);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertServerStreamer(Guild guild, String login) throws SQLException {
        insertServerStreamer.setLong(1, guild.getIdLong());
        insertServerStreamer.setString(2, login);
        insertServerStreamer.execute();
    }

    public void deleteServerStreamer(Guild guild, String login) throws SQLException {
        deleteServerStreamer.setLong(1, guild.getIdLong());
        deleteServerStreamer.setString(2, login);
        deleteServerStreamer.execute();
    }

    public Optional<TextChannel> getServerMemesChannel(Guild guild) {
        try {
            getServerMemesChannel.setLong(1, guild.getIdLong());
            ResultSet resultSet = getServerMemesChannel.executeQuery();
            if (resultSet.next()) {
                return Optional.ofNullable(DiscordBot.jda.getTextChannelById(resultSet.getLong("MemesChannel")));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setServerMemesChannel(long channelID, Guild guild) throws SQLException {
        setServerMemesChannel.setLong(1, channelID);
        setServerMemesChannel.setLong(2, guild.getIdLong());
        setServerMemesChannel.execute();
    }

    public void unsetServerMemesChannel(Guild guild) throws SQLException {
        setServerMemesChannel.setNull(1, Types.BIGINT);
        setServerMemesChannel.setLong(2, guild.getIdLong());
        setServerMemesChannel.execute();
    }
}
