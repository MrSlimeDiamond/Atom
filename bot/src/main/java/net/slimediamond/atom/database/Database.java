package net.slimediamond.atom.database;

import com.google.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.slimediamond.atom.chatbridge.BridgeEndpoint;
import net.slimediamond.atom.chatbridge.BridgeStore;
import net.slimediamond.atom.chatbridge.BridgedChat;
import net.slimediamond.atom.chatbridge.discord.DiscordBridgeEndpoint;
import net.slimediamond.atom.chatbridge.irc.IRCBridgeEndpoint;
import net.slimediamond.atom.chatbridge.telegram.TelegramBridgeEndpoint;
import net.slimediamond.atom.discord.CachedMessage;
import net.slimediamond.atom.reference.DBReference;
import net.slimediamond.atom.irc.IRC;
import net.slimediamond.atom.common.annotations.Service;
import net.slimediamond.atom.telegram.Telegram;
import net.slimediamond.util.number.NumberUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/*
 * Database must be started first!
 */
@Service(value = "database", priority = 9999)
public class Database {
    @Inject
    private Logger logger;

    @Inject
    @Nullable
    private JDA jda;

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
    private PreparedStatement disableIRCAutojoin;
    private PreparedStatement enableIRCAutojoin;
    private PreparedStatement getIRCChannels;

    private PreparedStatement isIRCAdmin;

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

    private PreparedStatement getMCOuuid;

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
    private PreparedStatement addStreamerMessage;
    private PreparedStatement deleteStreamerMessage;
    private PreparedStatement getStreamerMessages;

    private PreparedStatement getServerMemesChannel;
    private PreparedStatement setServerMemesChannel;

    private PreparedStatement insertBridgedRoom;
    private PreparedStatement removeBridgedRoom;
    private PreparedStatement insertBridgeEndpoint;
    private PreparedStatement removeBridgedEndpoint;
    private PreparedStatement setBridgedEndpointAvatar;
    private PreparedStatement disableBridgedEndpoint;
    private PreparedStatement isBridgedEndpointEnabled;
    private PreparedStatement getBridgedEndpointId;
    private PreparedStatement getBridgedEndpointAvatar;
    private PreparedStatement getBridgedEndpointIdentifier;
    private PreparedStatement getBridgedChatID;
    private PreparedStatement getAllBridgedChatIDs;
    private PreparedStatement getEndpointsForChat;

    @Service.Start
    public void startService() throws Exception {
        openConnection();
    }

    public void openConnection() throws SQLException {
        logger.info("Opening connection");

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

        enableIRCAutojoin = conn.prepareStatement("UPDATE irc_channels SET AutoJoin = TRUE WHERE ChannelName = ?");
        disableIRCAutojoin = conn.prepareStatement("UPDATE irc_channels SET AutoJoin = FALSE WHERE ChannelName = ?");

        getIRCChannels = conn.prepareStatement("SELECT * FROM irc_channels");

        isIRCAdmin = conn.prepareStatement("SELECT * FROM admins WHERE IRCNick = ? AND IRCHostname = ?");

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

        getMCOuuid = conn.prepareStatement("SELECT UUID FROM minecraftonline_users WHERE MinecraftName = ?");

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
        addStreamerMessage = conn.prepareStatement("INSERT INTO streamer_messages (Login, MessageID) VALUES (?, ?)");
        deleteStreamerMessage = conn.prepareStatement("DELETE * FROM streamer_messages WHERE MessageID = ?");
        getStreamerMessages = conn.prepareStatement("SELECT MessageID from streamer_messages WHERE Login = ?");

        getServerMemesChannel = conn.prepareStatement("SELECT MemesChannel FROM guilds WHERE GuildID = ?");
        setServerMemesChannel = conn.prepareStatement("UPDATE guilds SET MemesChannel = ? WHERE GuildID = ?");

        insertBridgedRoom = conn.prepareStatement("INSERT INTO bridged_chats DEFAULT VALUES");
        removeBridgedRoom = conn.prepareStatement("DELETE FROM bridged_chats WHERE ChatID = ?");
        insertBridgeEndpoint = conn.prepareStatement(
                "INSERT INTO endpoints (ChatID, Type, UniqueIdentifier, IsEnabled) " +
                        "VALUES (?, ?, ?, ?)"
        );
        removeBridgedEndpoint = conn.prepareStatement("DELETE FROM endpoints WHERE EndpointID = ?");
        setBridgedEndpointAvatar = conn.prepareStatement("UPDATE endpoints SET CustomAvatarURL = ? WHERE EndpointID = ?");
        disableBridgedEndpoint = conn.prepareStatement("UPDATE endpoints SET IsEnabled = FALSE WHERE EndpointID = ?");
        isBridgedEndpointEnabled = conn.prepareStatement("SELECT IsEnabled FROM endpoints WHERE EndpointID = ?");
        getBridgedEndpointId = conn.prepareStatement("SELECT EndpointID from endpoints WHERE UniqueIdentifier = ?");
        getBridgedEndpointAvatar = conn.prepareStatement("SELECT CustomAvatarURL FROM endpoints WHERE EndpointID = ?");
        getBridgedEndpointIdentifier = conn.prepareStatement("SELECT UniqueIdentifier FROM endpoints WHERE EndpointID = ?");
        getAllBridgedChatIDs = conn.prepareStatement("SELECT ChatID FROM bridged_chats");
        getEndpointsForChat = conn.prepareStatement("SELECT * FROM endpoints WHERE ChatID = ?");

        getBridgedChatID = conn.prepareStatement("SELECT ChatID FROM endpoints WHERE EndpointID = ?");

    }

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
            TextChannel channel = jda.getTextChannelById(channelID);
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
                CachedMessage msg = new CachedMessage(jda.retrieveUserById(authorID).complete(), messageContent, jda.getGuildById(guildID));

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
        TextChannel channel = jda.getTextChannelById(id);
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

    public void joinAllIRCChannels() throws SQLException, InterruptedException {
        ResultSet resultSet = getIRCChannels.executeQuery();
        while (resultSet.next()) {
            if (resultSet.getBoolean("AutoJoin")) {
                String channelName = resultSet.getString("ChannelName");
                IRC.client.addChannel(channelName);
            }
        }
    }

    public List<String> getIRCChannels() throws SQLException {
        ResultSet resultSet = getIRCChannels.executeQuery();
        List<String> channels = new ArrayList<>();
        while (resultSet.next()) {
            if (resultSet.getBoolean("AutoJoin")) {
                String channelName = resultSet.getString("ChannelName");
                channels.add(channelName);
            }
        }

        return channels;
    }

    public boolean isIRCAdmin(org.kitteh.irc.client.library.element.User user) throws SQLException {
        isIRCAdmin.setString(1, user.getNick());
        isIRCAdmin.setString(2, user.getHost());
        ResultSet resultSet = isIRCAdmin.executeQuery();
        return resultSet.next();
    }


    public EmojiUnion getServerPinnerinoEmojiUnion(long serverID) throws SQLException {
        String emoji = getServerPinnerinoEmoji(serverID);
        Guild guild = jda.getGuildById(serverID);
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
            String emoji = getServerPinnerinoEmoji(serverID);
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

    public Optional<String> getMCOuuid(String username) throws SQLException {
        getMCOuuid.setString(1, username);
        ResultSet resultSet = getMCOuuid.executeQuery();
        if (resultSet.next()) {
            return Optional.of(resultSet.getString(1));
        } else {
            return Optional.empty();
        }
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
                Role role = jda.getRoleById(roleID);
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

    public void addStreamerMessage(String login, Long messageId) throws SQLException {
        addStreamerMessage.setString(1, login);
        addStreamerMessage.setLong(2, messageId);
        addStreamerMessage.execute();
    }

    public void deleteStreamerMessage(Long messageId) throws SQLException {
        deleteStreamerMessage.setLong(1, messageId);
        deleteStreamerMessage.execute();
    }

    public ArrayList<Long> getStreamerMessageIDs(String login) throws SQLException {
        ArrayList<Long> ids = new ArrayList<>();

        getStreamerMessages.setString(1, login);
        ResultSet rs = getStreamerMessages.executeQuery();
        while (rs.next()) {
            ids.add(rs.getLong(1));
        }

        return ids;
    }

    public Optional<TextChannel> getServerMemesChannel(Guild guild) {
        try {
            getServerMemesChannel.setLong(1, guild.getIdLong());
            ResultSet resultSet = getServerMemesChannel.executeQuery();
            if (resultSet.next()) {
                return Optional.ofNullable(jda.getTextChannelById(resultSet.getLong("MemesChannel")));
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

    public void insertBridgedRoom() throws SQLException {
        insertBridgedRoom.executeUpdate();
    }

    public void removeBridgedRoom(int chatID) throws SQLException {
        removeBridgedRoom.setInt(1, chatID);
        removeBridgedRoom.executeUpdate();
    }

    public void insertBridgeEndpoint(int chatID, String type, String uniqueIdentifier, boolean isEnabled) throws SQLException {
        insertBridgeEndpoint.setInt(1, chatID);
        insertBridgeEndpoint.setString(2, type);
        insertBridgeEndpoint.setString(3, uniqueIdentifier);
        insertBridgeEndpoint.setBoolean(4, isEnabled);
        insertBridgeEndpoint.executeUpdate();
    }

    public void removeBridgedEndpoint(int endpointID) throws SQLException {
        removeBridgedEndpoint.setInt(1, endpointID);
        removeBridgedEndpoint.executeUpdate();
    }

    public void setBridgedEndpointAvatar(int endpointID, String avatarURL) throws SQLException {
        setBridgedEndpointAvatar.setString(1, avatarURL);
        setBridgedEndpointAvatar.setInt(2, endpointID);
        setBridgedEndpointAvatar.executeUpdate();
    }

    public void disableBridgedEndpoint(int endpointID) throws SQLException {
        disableBridgedEndpoint.setInt(1, endpointID);
        disableBridgedEndpoint.executeUpdate();
    }

    public boolean isBridgedEndpointEnabled(int endpointID) throws SQLException {
        isBridgedEndpointEnabled.setInt(1, endpointID);
        try (ResultSet rs = isBridgedEndpointEnabled.executeQuery()) {
            return rs.next() && rs.getBoolean(1);
        }
    }

    public int getBridgedEndpointId(String uniqueIdentifier) throws SQLException {
        getBridgedEndpointId.setString(1, uniqueIdentifier);
        ResultSet rs = getBridgedEndpointId.executeQuery();
        if (rs.next()) {
            return rs.getInt("EndpointID");
        }

        return -1;
    }

    public String getBridgedEndpointAvatar(int endpointID) throws SQLException {
        getBridgedEndpointAvatar.setInt(1, endpointID);
        try (ResultSet rs = getBridgedEndpointAvatar.executeQuery()) {
            return rs.next() ? rs.getString(1) : null;
        }
    }

    public String getBridgedEndpointIdentifier(int endpointID) throws SQLException {
        getBridgedEndpointIdentifier.setInt(1, endpointID);
        try (ResultSet rs = getBridgedEndpointIdentifier.executeQuery()) {
            return rs.next() ? rs.getString(1) : null;
        }
    }

    public int getBridgedChatID(int endpointID) throws SQLException {
        getBridgedChatID.setInt(1, endpointID);
        try (ResultSet rs = getBridgedChatID.executeQuery()) {
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    public List<Integer> getAllChatIds() throws SQLException {
        List<Integer> ids = new ArrayList<>();
        ResultSet rs = getAllBridgedChatIDs.executeQuery();
        while (rs.next()) {
            ids.add(rs.getInt("ChatID"));
        }

        return ids;
    }

    public List<BridgeEndpoint> getEndpoints(int id) throws SQLException {
        getEndpointsForChat.setLong(1, id);
        ResultSet rs = getEndpointsForChat.executeQuery();
        List<BridgeEndpoint> endpoints = new ArrayList<>();
        while (rs.next()) {
            int endpointId = rs.getInt("EndpointID");
            int chatId = rs.getInt("ChatID");
            String type = rs.getString("Type");
            String unique = rs.getString("UniqueIdentifier");
            int isEnabled = rs.getInt("isEnabled");
            String avatarUrl = rs.getString("CustomAvatarURL");

            switch (type) {
                case "discord" ->
                        endpoints.add(new DiscordBridgeEndpoint(jda.getTextChannelById(unique), unique, endpointId));
                case "irc" ->
                        endpoints.add(new IRCBridgeEndpoint(IRC.client.getChannel(unique).get(), unique, endpointId));
                case "telegram" -> endpoints.add(new TelegramBridgeEndpoint(Telegram.getClient().getChatById(Long.parseLong(unique)), chatId));
            }
        }

        return endpoints;
    }
}
