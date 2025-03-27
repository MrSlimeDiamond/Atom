package net.slimediamond.atom.discord.entities;

import net.dv8tion.jda.annotations.DeprecatedSince;
import net.dv8tion.jda.annotations.ForRemoval;
import net.dv8tion.jda.annotations.Incubating;
import net.dv8tion.jda.annotations.ReplaceWith;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.GuildWelcomeScreen;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.VanityInvite;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.automod.AutoModRule;
import net.dv8tion.jda.api.entities.automod.build.AutoModRuleData;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.ICopyableChannel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.api.entities.templates.Template;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.PrivilegeConfig;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.IntegrationPrivilege;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.managers.AutoModRuleManager;
import net.dv8tion.jda.api.managers.GuildManager;
import net.dv8tion.jda.api.managers.GuildStickerManager;
import net.dv8tion.jda.api.managers.GuildWelcomeScreenManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import net.dv8tion.jda.api.requests.restaction.CommandEditAction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.requests.restaction.MemberAction;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import net.dv8tion.jda.api.requests.restaction.ScheduledEventAction;
import net.dv8tion.jda.api.requests.restaction.order.CategoryOrderAction;
import net.dv8tion.jda.api.requests.restaction.order.ChannelOrderAction;
import net.dv8tion.jda.api.requests.restaction.order.RoleOrderAction;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.BanPaginationAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.ImageProxy;
import net.dv8tion.jda.api.utils.cache.MemberCacheView;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import net.dv8tion.jda.api.utils.cache.SortedSnowflakeCacheView;
import net.dv8tion.jda.api.utils.concurrent.Task;
import net.slimediamond.atom.data.JsonKeys;
import net.slimediamond.atom.data.dao.DAO;
import net.slimediamond.data.DataHolder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * An Atom guild.
 *
 * <p>Extending DataHolder, for storing
 * {@link net.slimediamond.data.Key}</p>
 */
public class AtomGuild implements Guild, DAO {
    private int id = -1;
    private final long discordId;
    private final Connection conn;
    private net.dv8tion.jda.api.entities.Guild jdaGuild;

    public AtomGuild(long discordId, Connection conn, net.dv8tion.jda.api.entities.Guild jdaGuild) {
        this.discordId = discordId;
        this.conn = conn;
        this.jdaGuild = jdaGuild;
    }

    public AtomGuild(int primaryKey, long id, Connection conn, net.dv8tion.jda.api.entities.Guild jdaGuild) {
        this(id, conn, jdaGuild);
        this.id = primaryKey;
    }

    @Override
    public long getDiscordId() {
        return discordId;
    }

    @Override
    public void save() throws SQLException {
        // Table structure:
        // CREATE TABLE IF NOT EXISTS guilds (id int NOT NULL AUTO_INCREMENT, discordId BIGINT NOT NULL, keys JSON NOT NULL, PRIMARY KEY(id))
        // Doesn't yet exist, insert it
        if (id == -1) {
            // Do we even have a table?!
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS guilds (id int NOT NULL AUTO_INCREMENT," +
                    "discord_id BIGINT NOT NULL," +
                    "keys JSON NOT NULL," +
                    "PRIMARY KEY(id))").execute();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO guilds (discord_id, keys) VALUES (?, ?)");
            ps.setLong(1, discordId);
            ps.setString(2, JsonKeys.json(this));
            ResultSet rs = ps.executeQuery();
            this.id = rs.getInt("id");
        } else { // Update it
            PreparedStatement ps = conn.prepareStatement("UPDATE GUILDS SET keys = ? WHERE id = ?");
            ps.setString(1, JsonKeys.json(this));
            ps.setInt(2, this.id);
            ps.execute();
        }
    }

    @Override
    public int getPrimaryKey() {
        return id;
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<List<Command>> retrieveCommands() {
        return jdaGuild.retrieveCommands();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<List<Command>> retrieveCommands(boolean b) {
        return jdaGuild.retrieveCommands(b);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<Command> retrieveCommandById(@NotNull String s) {
        return jdaGuild.retrieveCommandById(s);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<Command> retrieveCommandById(long id) {
        return jdaGuild.retrieveCommandById(id);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<Command> upsertCommand(@NotNull CommandData commandData) {
        return jdaGuild.upsertCommand(commandData);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public CommandCreateAction upsertCommand(@NotNull String name, @NotNull String description) {
        return jdaGuild.upsertCommand(name, description);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public CommandListUpdateAction updateCommands() {
        return jdaGuild.updateCommands();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public CommandEditAction editCommandById(@NotNull String s) {
        return jdaGuild.editCommandById(s);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public CommandEditAction editCommandById(long id) {
        return jdaGuild.editCommandById(id);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<Void> deleteCommandById(@NotNull String s) {
        return jdaGuild.deleteCommandById(s);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<Void> deleteCommandById(long commandId) {
        return jdaGuild.deleteCommandById(commandId);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<List<IntegrationPrivilege>> retrieveIntegrationPrivilegesById(@NotNull String s) {
        return jdaGuild.retrieveIntegrationPrivilegesById(s);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<List<IntegrationPrivilege>> retrieveIntegrationPrivilegesById(long targetId) {
        return jdaGuild.retrieveIntegrationPrivilegesById(targetId);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<PrivilegeConfig> retrieveCommandPrivileges() {
        return jdaGuild.retrieveCommandPrivileges();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<EnumSet<Region>> retrieveRegions() {
        return jdaGuild.retrieveRegions();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<EnumSet<Region>> retrieveRegions(boolean b) {
        return jdaGuild.retrieveRegions(b);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<List<AutoModRule>> retrieveAutoModRules() {
        return jdaGuild.retrieveAutoModRules();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<AutoModRule> retrieveAutoModRuleById(@NotNull String s) {
        return jdaGuild.retrieveAutoModRuleById(s);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<AutoModRule> retrieveAutoModRuleById(long id) {
        return jdaGuild.retrieveAutoModRuleById(id);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<AutoModRule> createAutoModRule(@NotNull AutoModRuleData autoModRuleData) {
        return jdaGuild.createAutoModRule(autoModRuleData);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AutoModRuleManager modifyAutoModRuleById(@NotNull String s) {
        return jdaGuild.modifyAutoModRuleById(s);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AutoModRuleManager modifyAutoModRuleById(long id) {
        return jdaGuild.modifyAutoModRuleById(id);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<Void> deleteAutoModRuleById(@NotNull String s) {
        return jdaGuild.deleteAutoModRuleById(s);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<Void> deleteAutoModRuleById(long id) {
        return jdaGuild.deleteAutoModRuleById(id);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public MemberAction addMember(@NotNull String s, @NotNull UserSnowflake userSnowflake) {
        return jdaGuild.addMember(s, userSnowflake);
    }

    @Override
    public boolean isLoaded() {
        return jdaGuild.isLoaded();
    }

    @Override
    public void pruneMemberCache() {
        jdaGuild.pruneMemberCache();
    }

    @Override
    public boolean unloadMember(long l) {
        return jdaGuild.unloadMember(l);
    }

    @Override
    public int getMemberCount() {
        return jdaGuild.getMemberCount();
    }

    @Nonnull
    @Override
    public String getName() {
        return jdaGuild.getName();
    }

    @Nullable
    @Override
    public String getIconId() {
        return jdaGuild.getIconId();
    }

    @Nullable
    @Override
    public String getIconUrl() {
        return jdaGuild.getIconUrl();
    }

    @Nullable
    @Override
    public ImageProxy getIcon() {
        return jdaGuild.getIcon();
    }

    @Nonnull
    @Override
    public Set<String> getFeatures() {
        return jdaGuild.getFeatures();
    }

    @Override
    public boolean isInvitesDisabled() {
        return jdaGuild.isInvitesDisabled();
    }

    @Nullable
    @Override
    public String getSplashId() {
        return jdaGuild.getSplashId();
    }

    @Nullable
    @Override
    public String getSplashUrl() {
        return jdaGuild.getSplashUrl();
    }

    @Nullable
    @Override
    public ImageProxy getSplash() {
        return jdaGuild.getSplash();
    }

    @Nullable
    @Override
    public String getVanityCode() {
        return jdaGuild.getVanityCode();
    }

    @Nullable
    @Override
    public String getVanityUrl() {
        return jdaGuild.getVanityUrl();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<VanityInvite> retrieveVanityInvite() {
        return jdaGuild.retrieveVanityInvite();
    }

    @Nullable
    @Override
    public String getDescription() {
        return jdaGuild.getDescription();
    }

    @Nonnull
    @Override
    public DiscordLocale getLocale() {
        return jdaGuild.getLocale();
    }

    @Nullable
    @Override
    public String getBannerId() {
        return jdaGuild.getBannerId();
    }

    @Nullable
    @Override
    public String getBannerUrl() {
        return jdaGuild.getBannerUrl();
    }

    @Nullable
    @Override
    public ImageProxy getBanner() {
        return jdaGuild.getBanner();
    }

    @Nonnull
    @Override
    public BoostTier getBoostTier() {
        return jdaGuild.getBoostTier();
    }

    @Override
    public int getBoostCount() {
        return jdaGuild.getBoostCount();
    }

    @Nonnull
    @Override
    public List<Member> getBoosters() {
        return jdaGuild.getBoosters();
    }

    @Override
    public int getMaxBitrate() {
        return jdaGuild.getMaxBitrate();
    }

    @Override
    public long getMaxFileSize() {
        return jdaGuild.getMaxFileSize();
    }

    @Override
    public int getMaxEmojis() {
        return jdaGuild.getMaxEmojis();
    }

    @Override
    public int getMaxMembers() {
        return jdaGuild.getMaxMembers();
    }

    @Override
    public int getMaxPresences() {
        return jdaGuild.getMaxPresences();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<MetaData> retrieveMetaData() {
        return jdaGuild.retrieveMetaData();
    }

    @Nullable
    @Override
    public VoiceChannel getAfkChannel() {
        return jdaGuild.getAfkChannel();
    }

    @Nullable
    @Override
    public TextChannel getSystemChannel() {
        return jdaGuild.getSystemChannel();
    }

    @Nullable
    @Override
    public TextChannel getRulesChannel() {
        return jdaGuild.getRulesChannel();
    }

    @Nullable
    @Override
    public TextChannel getCommunityUpdatesChannel() {
        return jdaGuild.getCommunityUpdatesChannel();
    }

    @Nullable
    @Override
    public Member getOwner() {
        return jdaGuild.getOwner();
    }

    @Override
    public long getOwnerIdLong() {
        return jdaGuild.getOwnerIdLong();
    }

    @Nonnull
    @Override
    public String getOwnerId() {
        return jdaGuild.getOwnerId();
    }

    @Nonnull
    @Override
    public Timeout getAfkTimeout() {
        return jdaGuild.getAfkTimeout();
    }

    @Override
    public boolean isMember(@NotNull UserSnowflake userSnowflake) {
        return jdaGuild.isMember(userSnowflake);
    }

    @Nonnull
    @Override
    public Member getSelfMember() {
        return jdaGuild.getSelfMember();
    }

    @Nonnull
    @Override
    public NSFWLevel getNSFWLevel() {
        return jdaGuild.getNSFWLevel();
    }

    @Nullable
    @Override
    public Member getMember(@NotNull UserSnowflake userSnowflake) {
        return jdaGuild.getMember(userSnowflake);
    }

    @Nullable
    @Override
    public Member getMemberById(@NotNull String userId) {
        return jdaGuild.getMemberById(userId);
    }

    @Nullable
    @Override
    public Member getMemberById(long userId) {
        return jdaGuild.getMemberById(userId);
    }

    @Nullable
    @Deprecated
    @ForRemoval
    @Override
    public Member getMemberByTag(@NotNull String tag) {
        return jdaGuild.getMemberByTag(tag);
    }

    @Nullable
    @Deprecated
    @ForRemoval
    @Override
    public Member getMemberByTag(@NotNull String username, @NotNull String discriminator) {
        return jdaGuild.getMemberByTag(username, discriminator);
    }

    @Nonnull
    @Override
    public List<Member> getMembers() {
        return jdaGuild.getMembers();
    }

    @Nonnull
    @Incubating
    @Override
    public List<Member> getMembersByName(@NotNull String name, boolean ignoreCase) {
        return jdaGuild.getMembersByName(name, ignoreCase);
    }

    @Nonnull
    @Override
    public List<Member> getMembersByNickname(@org.jetbrains.annotations.Nullable String nickname, boolean ignoreCase) {
        return jdaGuild.getMembersByNickname(nickname, ignoreCase);
    }

    @Nonnull
    @Override
    public List<Member> getMembersByEffectiveName(@NotNull String name, boolean ignoreCase) {
        return jdaGuild.getMembersByEffectiveName(name, ignoreCase);
    }

    @Nonnull
    @Override
    public List<Member> getMembersWithRoles(@NotNull Role... roles) {
        return jdaGuild.getMembersWithRoles(roles);
    }

    @Nonnull
    @Override
    public List<Member> getMembersWithRoles(@NotNull Collection<Role> roles) {
        return jdaGuild.getMembersWithRoles(roles);
    }

    @Nonnull
    @Override
    public MemberCacheView getMemberCache() {
        return jdaGuild.getMemberCache();
    }

    @Nonnull
    @Override
    public SortedSnowflakeCacheView<ScheduledEvent> getScheduledEventCache() {
        return jdaGuild.getScheduledEventCache();
    }

    @Nonnull
    @Override
    public List<ScheduledEvent> getScheduledEventsByName(@NotNull String name, boolean ignoreCase) {
        return jdaGuild.getScheduledEventsByName(name, ignoreCase);
    }

    @Nullable
    @Override
    public ScheduledEvent getScheduledEventById(@NotNull String id) {
        return jdaGuild.getScheduledEventById(id);
    }

    @Nullable
    @Override
    public ScheduledEvent getScheduledEventById(long id) {
        return jdaGuild.getScheduledEventById(id);
    }

    @Nonnull
    @Override
    public List<ScheduledEvent> getScheduledEvents() {
        return jdaGuild.getScheduledEvents();
    }

    @Nonnull
    @Override
    public SortedSnowflakeCacheView<StageChannel> getStageChannelCache() {
        return jdaGuild.getStageChannelCache();
    }

    @Nonnull
    @Override
    public SortedSnowflakeCacheView<ThreadChannel> getThreadChannelCache() {
        return jdaGuild.getThreadChannelCache();
    }

    @Nonnull
    @Override
    public SortedSnowflakeCacheView<Category> getCategoryCache() {
        return jdaGuild.getCategoryCache();
    }

    @Nonnull
    @Override
    public SortedSnowflakeCacheView<TextChannel> getTextChannelCache() {
        return jdaGuild.getTextChannelCache();
    }

    @Nonnull
    @Override
    public SortedSnowflakeCacheView<NewsChannel> getNewsChannelCache() {
        return jdaGuild.getNewsChannelCache();
    }

    @Nonnull
    @Override
    public SortedSnowflakeCacheView<VoiceChannel> getVoiceChannelCache() {
        return jdaGuild.getVoiceChannelCache();
    }

    @Nonnull
    @Override
    public SortedSnowflakeCacheView<ForumChannel> getForumChannelCache() {
        return jdaGuild.getForumChannelCache();
    }

    @Nonnull
    @Override
    public List<GuildChannel> getChannels() {
        return jdaGuild.getChannels();
    }

    @Nonnull
    @Override
    public List<GuildChannel> getChannels(boolean b) {
        return jdaGuild.getChannels(b);
    }

    @Nullable
    @Override
    public Role getRoleById(@NotNull String id) {
        return jdaGuild.getRoleById(id);
    }

    @Nullable
    @Override
    public Role getRoleById(long id) {
        return jdaGuild.getRoleById(id);
    }

    @Nonnull
    @Override
    public List<Role> getRoles() {
        return jdaGuild.getRoles();
    }

    @Nonnull
    @Override
    public List<Role> getRolesByName(@NotNull String name, boolean ignoreCase) {
        return jdaGuild.getRolesByName(name, ignoreCase);
    }

    @Nullable
    @Override
    public Role getRoleByBot(long userId) {
        return jdaGuild.getRoleByBot(userId);
    }

    @Nullable
    @Override
    public Role getRoleByBot(@NotNull String userId) {
        return jdaGuild.getRoleByBot(userId);
    }

    @Nullable
    @Override
    public Role getRoleByBot(@NotNull User user) {
        return jdaGuild.getRoleByBot(user);
    }

    @Nullable
    @Override
    public Role getBotRole() {
        return jdaGuild.getBotRole();
    }

    @Nullable
    @Override
    public Role getBoostRole() {
        return jdaGuild.getBoostRole();
    }

    @Nonnull
    @Override
    public SortedSnowflakeCacheView<Role> getRoleCache() {
        return jdaGuild.getRoleCache();
    }

    @Nullable
    @Override
    public RichCustomEmoji getEmojiById(@NotNull String id) {
        return jdaGuild.getEmojiById(id);
    }

    @Nullable
    @Override
    public RichCustomEmoji getEmojiById(long id) {
        return jdaGuild.getEmojiById(id);
    }

    @Nonnull
    @Override
    public List<RichCustomEmoji> getEmojis() {
        return jdaGuild.getEmojis();
    }

    @Nonnull
    @Override
    public List<RichCustomEmoji> getEmojisByName(@NotNull String name, boolean ignoreCase) {
        return jdaGuild.getEmojisByName(name, ignoreCase);
    }

    @Nonnull
    @Override
    public SnowflakeCacheView<RichCustomEmoji> getEmojiCache() {
        return jdaGuild.getEmojiCache();
    }

    @Nullable
    @Override
    public GuildSticker getStickerById(@NotNull String id) {
        return jdaGuild.getStickerById(id);
    }

    @Nullable
    @Override
    public GuildSticker getStickerById(long id) {
        return jdaGuild.getStickerById(id);
    }

    @Nonnull
    @Override
    public List<GuildSticker> getStickers() {
        return jdaGuild.getStickers();
    }

    @Nonnull
    @Override
    public List<GuildSticker> getStickersByName(@NotNull String name, boolean ignoreCase) {
        return jdaGuild.getStickersByName(name, ignoreCase);
    }

    @Nonnull
    @Override
    public SnowflakeCacheView<GuildSticker> getStickerCache() {
        return jdaGuild.getStickerCache();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<List<RichCustomEmoji>> retrieveEmojis() {
        return jdaGuild.retrieveEmojis();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<RichCustomEmoji> retrieveEmojiById(@NotNull String s) {
        return jdaGuild.retrieveEmojiById(s);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<RichCustomEmoji> retrieveEmojiById(long id) {
        return jdaGuild.retrieveEmojiById(id);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<RichCustomEmoji> retrieveEmoji(@NotNull CustomEmoji emoji) {
        return jdaGuild.retrieveEmoji(emoji);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<List<GuildSticker>> retrieveStickers() {
        return jdaGuild.retrieveStickers();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<GuildSticker> retrieveSticker(@NotNull StickerSnowflake stickerSnowflake) {
        return jdaGuild.retrieveSticker(stickerSnowflake);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public GuildStickerManager editSticker(@NotNull StickerSnowflake stickerSnowflake) {
        return jdaGuild.editSticker(stickerSnowflake);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public BanPaginationAction retrieveBanList() {
        return jdaGuild.retrieveBanList();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<Ban> retrieveBan(@NotNull UserSnowflake userSnowflake) {
        return jdaGuild.retrieveBan(userSnowflake);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<Integer> retrievePrunableMemberCount(int i) {
        return jdaGuild.retrievePrunableMemberCount(i);
    }

    @Nonnull
    @Override
    public Role getPublicRole() {
        return jdaGuild.getPublicRole();
    }

    @Nullable
    @Override
    public DefaultGuildChannelUnion getDefaultChannel() {
        return jdaGuild.getDefaultChannel();
    }

    @Nonnull
    @Override
    public GuildManager getManager() {
        return jdaGuild.getManager();
    }

    @Override
    public boolean isBoostProgressBarEnabled() {
        return jdaGuild.isBoostProgressBarEnabled();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditLogPaginationAction retrieveAuditLogs() {
        return jdaGuild.retrieveAuditLogs();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<Void> leave() {
        return jdaGuild.leave();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<Void> delete() {
        return jdaGuild.delete();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<Void> delete(@org.jetbrains.annotations.Nullable String s) {
        return jdaGuild.delete(s);
    }

    @Nonnull
    @Override
    public AudioManager getAudioManager() {
        return jdaGuild.getAudioManager();
    }

    @Nonnull
    @Override
    public Task<Void> requestToSpeak() {
        return jdaGuild.requestToSpeak();
    }

    @Nonnull
    @Override
    public Task<Void> cancelRequestToSpeak() {
        return jdaGuild.cancelRequestToSpeak();
    }

    @Nonnull
    @Override
    public JDA getJDA() {
        return jdaGuild.getJDA();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<List<Invite>> retrieveInvites() {
        return jdaGuild.retrieveInvites();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<List<Template>> retrieveTemplates() {
        return jdaGuild.retrieveTemplates();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<Template> createTemplate(@NotNull String s, @org.jetbrains.annotations.Nullable String s1) {
        return jdaGuild.createTemplate(s, s1);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<List<Webhook>> retrieveWebhooks() {
        return jdaGuild.retrieveWebhooks();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<GuildWelcomeScreen> retrieveWelcomeScreen() {
        return jdaGuild.retrieveWelcomeScreen();
    }

    @Nonnull
    @Override
    public List<GuildVoiceState> getVoiceStates() {
        return jdaGuild.getVoiceStates();
    }

    @Nonnull
    @Override
    public VerificationLevel getVerificationLevel() {
        return jdaGuild.getVerificationLevel();
    }

    @Nonnull
    @Override
    public NotificationLevel getDefaultNotificationLevel() {
        return jdaGuild.getDefaultNotificationLevel();
    }

    @Nonnull
    @Override
    public MFALevel getRequiredMFALevel() {
        return jdaGuild.getRequiredMFALevel();
    }

    @Nonnull
    @Override
    public ExplicitContentLevel getExplicitContentLevel() {
        return jdaGuild.getExplicitContentLevel();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public Task<List<Member>> loadMembers() {
        return jdaGuild.loadMembers();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public Task<List<Member>> findMembers(@NotNull Predicate<? super Member> filter) {
        return jdaGuild.findMembers(filter);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public Task<List<Member>> findMembersWithRoles(@NotNull Collection<Role> roles) {
        return jdaGuild.findMembersWithRoles(roles);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public Task<List<Member>> findMembersWithRoles(@NotNull Role... roles) {
        return jdaGuild.findMembersWithRoles(roles);
    }

    @Nonnull
    @Override
    public Task<Void> loadMembers(@NotNull Consumer<Member> consumer) {
        return jdaGuild.loadMembers(consumer);
    }

    @Nonnull
    @Override
    public CacheRestAction<Member> retrieveMember(@NotNull UserSnowflake user) {
        return jdaGuild.retrieveMember(user);
    }

    @Nonnull
    @Override
    public CacheRestAction<Member> retrieveOwner() {
        return jdaGuild.retrieveOwner();
    }

    @Nonnull
    @Override
    public CacheRestAction<Member> retrieveMemberById(@NotNull String id) {
        return jdaGuild.retrieveMemberById(id);
    }

    @Nonnull
    @Override
    public CacheRestAction<Member> retrieveMemberById(long l) {
        return jdaGuild.retrieveMemberById(l);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public Task<List<Member>> retrieveMembers(@NotNull Collection<? extends UserSnowflake> users) {
        return jdaGuild.retrieveMembers(users);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public Task<List<Member>> retrieveMembersByIds(@NotNull Collection<Long> ids) {
        return jdaGuild.retrieveMembersByIds(ids);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public Task<List<Member>> retrieveMembersByIds(@NotNull String... ids) {
        return jdaGuild.retrieveMembersByIds(ids);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public Task<List<Member>> retrieveMembersByIds(@NotNull long... ids) {
        return jdaGuild.retrieveMembersByIds(ids);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public Task<List<Member>> retrieveMembers(boolean includePresence, @NotNull Collection<? extends UserSnowflake> users) {
        return jdaGuild.retrieveMembers(includePresence, users);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public Task<List<Member>> retrieveMembersByIds(boolean includePresence, @NotNull Collection<Long> ids) {
        return jdaGuild.retrieveMembersByIds(includePresence, ids);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public Task<List<Member>> retrieveMembersByIds(boolean includePresence, @NotNull String... ids) {
        return jdaGuild.retrieveMembersByIds(includePresence, ids);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public Task<List<Member>> retrieveMembersByIds(boolean b, @NotNull long... longs) {
        return jdaGuild.retrieveMembersByIds(b, longs);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public Task<List<Member>> retrieveMembersByPrefix(@NotNull String s, int i) {
        return jdaGuild.retrieveMembersByPrefix(s, i);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<List<ThreadChannel>> retrieveActiveThreads() {
        return jdaGuild.retrieveActiveThreads();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public CacheRestAction<ScheduledEvent> retrieveScheduledEventById(long id) {
        return jdaGuild.retrieveScheduledEventById(id);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public CacheRestAction<ScheduledEvent> retrieveScheduledEventById(@NotNull String s) {
        return jdaGuild.retrieveScheduledEventById(s);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<Void> moveVoiceMember(@NotNull Member member, @org.jetbrains.annotations.Nullable AudioChannel audioChannel) {
        return jdaGuild.moveVoiceMember(member, audioChannel);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RestAction<Void> kickVoiceMember(@NotNull Member member) {
        return jdaGuild.kickVoiceMember(member);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<Void> modifyNickname(@NotNull Member member, @org.jetbrains.annotations.Nullable String s) {
        return jdaGuild.modifyNickname(member, s);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<Integer> prune(int days, @NotNull Role... roles) {
        return jdaGuild.prune(days, roles);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<Integer> prune(int i, boolean b, @NotNull Role... roles) {
        return jdaGuild.prune(i, b, roles);
    }

    @Nonnull
    @CheckReturnValue
    @Deprecated
    @ForRemoval
    @ReplaceWith("kick(user).reason(reason)")
    @DeprecatedSince("5.0.0")
    @Override
    public AuditableRestAction<Void> kick(@NotNull UserSnowflake user, @org.jetbrains.annotations.Nullable String reason) {
        return jdaGuild.kick(user, reason);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<Void> kick(@NotNull UserSnowflake userSnowflake) {
        return jdaGuild.kick(userSnowflake);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<Void> ban(@NotNull UserSnowflake userSnowflake, int i, @NotNull TimeUnit timeUnit) {
        return jdaGuild.ban(userSnowflake, i, timeUnit);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<Void> unban(@NotNull UserSnowflake userSnowflake) {
        return jdaGuild.unban(userSnowflake);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<Void> timeoutFor(@NotNull UserSnowflake user, long amount, @NotNull TimeUnit unit) {
        return jdaGuild.timeoutFor(user, amount, unit);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<Void> timeoutFor(@NotNull UserSnowflake user, @NotNull Duration duration) {
        return jdaGuild.timeoutFor(user, duration);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<Void> timeoutUntil(@NotNull UserSnowflake userSnowflake, @NotNull TemporalAccessor temporalAccessor) {
        return jdaGuild.timeoutUntil(userSnowflake, temporalAccessor);
    }

    @Nonnull
    @Override
    public AuditableRestAction<Void> removeTimeout(@NotNull UserSnowflake userSnowflake) {
        return jdaGuild.removeTimeout(userSnowflake);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<Void> deafen(@NotNull UserSnowflake userSnowflake, boolean b) {
        return jdaGuild.deafen(userSnowflake, b);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<Void> mute(@NotNull UserSnowflake userSnowflake, boolean b) {
        return jdaGuild.mute(userSnowflake, b);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<Void> addRoleToMember(@NotNull UserSnowflake userSnowflake, @NotNull Role role) {
        return jdaGuild.addRoleToMember(userSnowflake, role);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<Void> removeRoleFromMember(@NotNull UserSnowflake userSnowflake, @NotNull Role role) {
        return jdaGuild.removeRoleFromMember(userSnowflake, role);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member, @org.jetbrains.annotations.Nullable Collection<Role> collection, @org.jetbrains.annotations.Nullable Collection<Role> collection1) {
        return jdaGuild.modifyMemberRoles(member, collection, collection1);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member, @NotNull Role... roles) {
        return jdaGuild.modifyMemberRoles(member, roles);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member, @NotNull Collection<Role> collection) {
        return jdaGuild.modifyMemberRoles(member, collection);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<Void> transferOwnership(@NotNull Member member) {
        return jdaGuild.transferOwnership(member);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public ChannelAction<TextChannel> createTextChannel(@NotNull String name) {
        return jdaGuild.createTextChannel(name);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public ChannelAction<TextChannel> createTextChannel(@NotNull String s, @org.jetbrains.annotations.Nullable Category category) {
        return jdaGuild.createTextChannel(s, category);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public ChannelAction<NewsChannel> createNewsChannel(@NotNull String name) {
        return jdaGuild.createNewsChannel(name);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public ChannelAction<NewsChannel> createNewsChannel(@NotNull String s, @org.jetbrains.annotations.Nullable Category category) {
        return jdaGuild.createNewsChannel(s, category);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public ChannelAction<VoiceChannel> createVoiceChannel(@NotNull String name) {
        return jdaGuild.createVoiceChannel(name);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public ChannelAction<VoiceChannel> createVoiceChannel(@NotNull String s, @org.jetbrains.annotations.Nullable Category category) {
        return jdaGuild.createVoiceChannel(s, category);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public ChannelAction<StageChannel> createStageChannel(@NotNull String name) {
        return jdaGuild.createStageChannel(name);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public ChannelAction<StageChannel> createStageChannel(@NotNull String s, @org.jetbrains.annotations.Nullable Category category) {
        return jdaGuild.createStageChannel(s, category);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public ChannelAction<ForumChannel> createForumChannel(@NotNull String name) {
        return jdaGuild.createForumChannel(name);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public ChannelAction<ForumChannel> createForumChannel(@NotNull String s, @org.jetbrains.annotations.Nullable Category category) {
        return jdaGuild.createForumChannel(s, category);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public ChannelAction<Category> createCategory(@NotNull String s) {
        return jdaGuild.createCategory(s);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public <T extends ICopyableChannel> ChannelAction<T> createCopyOfChannel(@NotNull T channel) {
        return jdaGuild.createCopyOfChannel(channel);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RoleAction createRole() {
        return jdaGuild.createRole();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RoleAction createCopyOfRole(@NotNull Role role) {
        return jdaGuild.createCopyOfRole(role);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<RichCustomEmoji> createEmoji(@NotNull String s, @NotNull Icon icon, @NotNull Role... roles) {
        return jdaGuild.createEmoji(s, icon, roles);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<GuildSticker> createSticker(@NotNull String s, @NotNull String s1, @NotNull FileUpload fileUpload, @NotNull Collection<String> collection) {
        return jdaGuild.createSticker(s, s1, fileUpload, collection);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<GuildSticker> createSticker(@NotNull String name, @NotNull String description, @NotNull FileUpload file, @NotNull String tag, @NotNull String... tags) {
        return jdaGuild.createSticker(name, description, file, tag, tags);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public AuditableRestAction<Void> deleteSticker(@NotNull StickerSnowflake stickerSnowflake) {
        return jdaGuild.deleteSticker(stickerSnowflake);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public ScheduledEventAction createScheduledEvent(@NotNull String s, @NotNull String s1, @NotNull OffsetDateTime offsetDateTime, @NotNull OffsetDateTime offsetDateTime1) {
        return jdaGuild.createScheduledEvent(s, s1, offsetDateTime, offsetDateTime1);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public ScheduledEventAction createScheduledEvent(@NotNull String s, @NotNull GuildChannel guildChannel, @NotNull OffsetDateTime offsetDateTime) {
        return jdaGuild.createScheduledEvent(s, guildChannel, offsetDateTime);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public ChannelOrderAction modifyCategoryPositions() {
        return jdaGuild.modifyCategoryPositions();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public ChannelOrderAction modifyTextChannelPositions() {
        return jdaGuild.modifyTextChannelPositions();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public ChannelOrderAction modifyVoiceChannelPositions() {
        return jdaGuild.modifyVoiceChannelPositions();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public CategoryOrderAction modifyTextChannelPositions(@NotNull Category category) {
        return jdaGuild.modifyTextChannelPositions(category);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public CategoryOrderAction modifyVoiceChannelPositions(@NotNull Category category) {
        return jdaGuild.modifyVoiceChannelPositions(category);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RoleOrderAction modifyRolePositions() {
        return jdaGuild.modifyRolePositions();
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public RoleOrderAction modifyRolePositions(boolean b) {
        return jdaGuild.modifyRolePositions(b);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public GuildWelcomeScreenManager modifyWelcomeScreen() {
        return jdaGuild.modifyWelcomeScreen();
    }

    @Nullable
    @Override
    public <T extends Channel> T getChannelById(@NotNull Class<T> type, @NotNull String id) {
        return jdaGuild.getChannelById(type, id);
    }

    @Nullable
    @Override
    public <T extends Channel> T getChannelById(@NotNull Class<T> type, long id) {
        return jdaGuild.getChannelById(type, id);
    }

    @Nullable
    @Override
    public GuildChannel getGuildChannelById(@NotNull String id) {
        return jdaGuild.getGuildChannelById(id);
    }

    @Nullable
    @Override
    public GuildChannel getGuildChannelById(long id) {
        return jdaGuild.getGuildChannelById(id);
    }

    @Nullable
    @Override
    public GuildChannel getGuildChannelById(@NotNull ChannelType type, @NotNull String id) {
        return jdaGuild.getGuildChannelById(type, id);
    }

    @Nullable
    @Override
    public GuildChannel getGuildChannelById(@NotNull ChannelType type, long id) {
        return jdaGuild.getGuildChannelById(type, id);
    }

    @Nonnull
    @Override
    public List<StageChannel> getStageChannelsByName(@NotNull String name, boolean ignoreCase) {
        return jdaGuild.getStageChannelsByName(name, ignoreCase);
    }

    @Nullable
    @Override
    public StageChannel getStageChannelById(@NotNull String id) {
        return jdaGuild.getStageChannelById(id);
    }

    @Nullable
    @Override
    public StageChannel getStageChannelById(long id) {
        return jdaGuild.getStageChannelById(id);
    }

    @Nonnull
    @Override
    public List<StageChannel> getStageChannels() {
        return jdaGuild.getStageChannels();
    }

    @Nonnull
    @Override
    public List<ThreadChannel> getThreadChannelsByName(@NotNull String name, boolean ignoreCase) {
        return jdaGuild.getThreadChannelsByName(name, ignoreCase);
    }

    @Nullable
    @Override
    public ThreadChannel getThreadChannelById(@NotNull String id) {
        return jdaGuild.getThreadChannelById(id);
    }

    @Nullable
    @Override
    public ThreadChannel getThreadChannelById(long id) {
        return jdaGuild.getThreadChannelById(id);
    }

    @Nonnull
    @Override
    public List<ThreadChannel> getThreadChannels() {
        return jdaGuild.getThreadChannels();
    }

    @Nonnull
    @Override
    public List<Category> getCategoriesByName(@NotNull String name, boolean ignoreCase) {
        return jdaGuild.getCategoriesByName(name, ignoreCase);
    }

    @Nullable
    @Override
    public Category getCategoryById(@NotNull String id) {
        return jdaGuild.getCategoryById(id);
    }

    @Nullable
    @Override
    public Category getCategoryById(long id) {
        return jdaGuild.getCategoryById(id);
    }

    @Nonnull
    @Override
    public List<Category> getCategories() {
        return jdaGuild.getCategories();
    }

    @Nonnull
    @Override
    public List<TextChannel> getTextChannelsByName(@NotNull String name, boolean ignoreCase) {
        return jdaGuild.getTextChannelsByName(name, ignoreCase);
    }

    @Nullable
    @Override
    public TextChannel getTextChannelById(@NotNull String id) {
        return jdaGuild.getTextChannelById(id);
    }

    @Nullable
    @Override
    public TextChannel getTextChannelById(long id) {
        return jdaGuild.getTextChannelById(id);
    }

    @Nonnull
    @Override
    public List<TextChannel> getTextChannels() {
        return jdaGuild.getTextChannels();
    }

    @Nonnull
    @Override
    public List<NewsChannel> getNewsChannelsByName(@NotNull String name, boolean ignoreCase) {
        return jdaGuild.getNewsChannelsByName(name, ignoreCase);
    }

    @Nullable
    @Override
    public NewsChannel getNewsChannelById(@NotNull String id) {
        return jdaGuild.getNewsChannelById(id);
    }

    @Nullable
    @Override
    public NewsChannel getNewsChannelById(long id) {
        return jdaGuild.getNewsChannelById(id);
    }

    @Nonnull
    @Override
    public List<NewsChannel> getNewsChannels() {
        return jdaGuild.getNewsChannels();
    }

    @Nonnull
    @Override
    public List<VoiceChannel> getVoiceChannelsByName(@NotNull String name, boolean ignoreCase) {
        return jdaGuild.getVoiceChannelsByName(name, ignoreCase);
    }

    @Nullable
    @Override
    public VoiceChannel getVoiceChannelById(@NotNull String id) {
        return jdaGuild.getVoiceChannelById(id);
    }

    @Nullable
    @Override
    public VoiceChannel getVoiceChannelById(long id) {
        return jdaGuild.getVoiceChannelById(id);
    }

    @Nonnull
    @Override
    public List<VoiceChannel> getVoiceChannels() {
        return jdaGuild.getVoiceChannels();
    }

    @Nonnull
    @Override
    public List<ForumChannel> getForumChannelsByName(@NotNull String name, boolean ignoreCase) {
        return jdaGuild.getForumChannelsByName(name, ignoreCase);
    }

    @Nullable
    @Override
    public ForumChannel getForumChannelById(@NotNull String id) {
        return jdaGuild.getForumChannelById(id);
    }

    @Nullable
    @Override
    public ForumChannel getForumChannelById(long id) {
        return jdaGuild.getForumChannelById(id);
    }

    @Nonnull
    @Override
    public List<ForumChannel> getForumChannels() {
        return jdaGuild.getForumChannels();
    }

    @Nonnull
    @Override
    public String getId() {
        return jdaGuild.getId();
    }

    @Override
    public long getIdLong() {
        return jdaGuild.getIdLong();
    }

    @Nonnull
    @Override
    public OffsetDateTime getTimeCreated() {
        return jdaGuild.getTimeCreated();
    }
}
