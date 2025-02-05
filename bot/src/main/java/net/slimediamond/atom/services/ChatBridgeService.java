package net.slimediamond.atom.services;

import com.google.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.engio.mbassy.listener.Handler;
import net.slimediamond.atom.chatbridge.*;
import net.slimediamond.atom.chatbridge.irc.IRCBridgeEndpoint;
import net.slimediamond.atom.chatbridge.mco.MCOBridgeSource;
import net.slimediamond.atom.common.util.HTTPUtil;
import net.slimediamond.atom.irc.IRC;
import net.slimediamond.atom.irc.McObotMessageParser;
import net.slimediamond.atom.reference.DiscordReference;
import net.slimediamond.atom.reference.EmbedReference;
import net.slimediamond.atom.reference.IRCReference;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.common.annotations.Service;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.reference.TelegramReference;
import net.slimediamond.atom.telegram.Telegram;
import net.slimediamond.telegram.entity.File;
import net.slimediamond.telegram.Listener;
import net.slimediamond.telegram.event.UserAddedToChatEvent;
import net.slimediamond.telegram.event.UserRemovedFromChatEvent;
import org.kitteh.irc.client.library.event.channel.ChannelCtcpEvent;
import org.kitteh.irc.client.library.event.channel.ChannelJoinEvent;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.kitteh.irc.client.library.event.channel.ChannelPartEvent;
import org.kitteh.irc.client.library.event.user.UserNickChangeEvent;
import org.kitteh.irc.client.library.event.user.UserQuitEvent;

import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service(value = "chat bridge", priority = 1)
public class ChatBridgeService extends ListenerAdapter implements Listener {
    @Inject
    @Nullable
    private JDA jda;

    @GetService
    private Database database;

    private boolean netsplitActive = false;
    private boolean joinsActive = false;
    private Netsplit netsplit;

    private final HashMap<BridgeEndpoint, MCOBridgeSource> ircMcoMap = new HashMap<>();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Service.Start
    public void startService() throws Exception {
        if (jda == null) return; // You can't build a bridge with just one mountain!
        jda.addEventListener(this);
        IRC.client.getEventManager().registerEventListener(this);
        Telegram.getClient().addListener(this);

        // not the best solution
        while (IRC.client.getChannels().size() != database.getIRCChannels().size()) {
            Thread.sleep(500);
        }

        // Add endpoints and chat rooms to the storage
        database.getAllChatIds().forEach(chatId -> {
            try {
                String name = database.getBridgedChatName(chatId);
                BridgedChat chat = new BridgedChat(database.isBridgedChatEnabled(chatId), chatId, name);
                database.getEndpoints(chatId).forEach(chat::addEndpoint);

                BridgeStore.getChats().put(chatId, chat);
            } catch (SQLException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

//        // send our connection messages
//        BridgeStore.getChats().forEach((id, chat) -> {
//            chat.sendUpdate(EventType.CONNECT, null, null, null);
//        });
    }

    @Service.Shutdown
    public void shutdownService() {
        BridgeStore.getChats().forEach((id, chat) -> {
            chat.sendUpdate(EventType.DISCONNECT, null, null, null);
        });
    }

    // Discord content event
    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getMessage().getContentRaw().startsWith(DiscordReference.prefix)) return;

        BridgedChat chat;
        try {
            chat = BridgeStore.getChats().get(database.getBridgedChatID(database.getBridgedEndpointId(event.getChannel().getId())));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (chat == null) {
            return;
        }
        String identifier = event.getChannel().getId();
        BridgeEndpoint source = BridgeStore.getEndpointByIdentifier(chat, identifier);

        if (!event.getMessage().getContentDisplay().isEmpty()) {
            chat.sendMessage(new BridgeMessage(event.getAuthor().getEffectiveName(), event.getAuthor().getEffectiveAvatarUrl(), event.getMessage().getContentDisplay()), source);
        }

        for (Message.Attachment attachment : event.getMessage().getAttachments()) {
            chat.sendMessage(new BridgeMessage(event.getAuthor().getEffectiveName(), event.getAuthor().getEffectiveAvatarUrl(), attachment.getUrl()), source);
        }
    }

    // IRC Message event
    @Handler
    public void onChannelMessage(ChannelMessageEvent event) throws SQLException {
        if (event.getActor().getNick().equals(IRCReference.nickname)) return;
        BridgedChat chat = BridgeStore.getChats().get(database.getBridgedChatID(database.getBridgedEndpointId(event.getChannel().getName())));
        if (chat == null) {
            return;
        }
        String identifier = event.getChannel().getName();
        BridgeEndpoint source = BridgeStore.getEndpointByIdentifier(chat, identifier);

        // Nullable
        String avatarUrl = database.getBridgedEndpointAvatar(source.getId());
        String username = event.getActor().getNick();
        String content = event.getMessage();

        if (event.getActor().getNick().equals("McObot")) {
            avatarUrl = EmbedReference.mcoIconLarge;

            if (event.getMessage().startsWith("(MCS) ")) {
                if (!ircMcoMap.containsKey(source)) {
                    ircMcoMap.put(source, new MCOBridgeSource((IRCBridgeEndpoint) source));
                }

                source = ircMcoMap.get(source);

                McObotMessageParser parser = new McObotMessageParser(event.getActor(), event.getMessage());

                if (parser.isChatMessage()) {
                    username = parser.getSenderUsername();
                    content = parser.getMessageContent();

                    avatarUrl = "https://minecraftonline.com/cgi-bin/getplayerhead.sh?" + username + "&128";
                } else if (parser.isJoinMessage()) {
                    username = event.getMessage().split("\\(MCS\\) ")[1].split(" joined the game")[0];
                    chat.sendUpdate(EventType.JOIN, username, source, null);
                    return;
                } else if (parser.isLeaveMessage()) {
                    Pattern pattern = Pattern.compile("\\(MCS\\) (.+?) (?:left|disconnected: (.+))");
                    Matcher matcher = pattern.matcher(event.getMessage().trim());

                    if (matcher.find()) {
                        username = matcher.group(1);
                        String comment = matcher.group(2);

                        chat.sendUpdate(EventType.LEAVE, username, source, comment);
                    }
                    return;
                } else {
                    // remove MCS prefix
                    content = content.substring(6);
                }
            }
        }

        BridgeMessage message = new BridgeMessage(username, avatarUrl, content);

        chat.sendMessage(message, source);

    }

    @Handler
    public void onChannelJoin(ChannelJoinEvent event) throws SQLException {
        BridgedChat chat = BridgeStore.getChats().get(database.getBridgedChatID(database.getBridgedEndpointId(event.getChannel().getName())));
        if (chat == null) return;
        String identifier = event.getChannel().getName();
        BridgeEndpoint source = BridgeStore.getEndpointByIdentifier(chat, identifier);

        if (source != null) {
            if (netsplitActive && netsplit != null) {
                if (netsplit.getQuits().contains(event.getActor().getNick())) {
                    if (!joinsActive) {
                        joinsActive = true;

                        scheduler.schedule(() -> this.netsplitJoins(chat, netsplit, source), Netsplit.NETSPLIT_WAIT_TIME, TimeUnit.SECONDS);
                    }

                    netsplit.addJoin(event.getActor().getNick());

                    // Mark a netsplit as completed when everyone has rejoined
                    if (netsplit.getQuits().equals(netsplit.getJoins())) {
                        this.netsplitJoins(chat, netsplit, source);
                    }

                    return;
                }
            }

            if (event.getActor().getNick().equals(IRCReference.nickname)) {
                chat.sendUpdate(EventType.CONNECT, event.getChannel().getName(), source, null);
                return;
            }
            chat.sendUpdate(EventType.JOIN, event.getActor().getNick(), source, null);
        }
    }

    private void netsplitJoins(BridgedChat chat, Netsplit netsplit, BridgeEndpoint source) {
        if (netsplitActive) {
            chat.netsplitJoins(netsplit, source);
        }
        netsplitActive = false;
    }

    @Handler
    public void onChannelLeave(ChannelPartEvent event) throws SQLException {
        BridgedChat chat = BridgeStore.getChats().get(database.getBridgedChatID(database.getBridgedEndpointId(event.getChannel().getName())));
        if (chat == null) return;
        String identifier = event.getChannel().getName();
        BridgeEndpoint source = BridgeStore.getEndpointByIdentifier(chat, identifier);

        if (source != null) {
            if (event.getActor().getNick().equals(IRCReference.nickname)) {
                chat.sendUpdate(EventType.DISCONNECT, event.getChannel().getName(), source, null);
                return;
            }
            chat.sendUpdate(EventType.LEAVE, event.getActor().getNick(), source, null);
        }
    }

    @Handler
    public void onUserQuit(UserQuitEvent event) {
        event.getUser().getChannels().forEach(channel -> {
            try {
                BridgedChat chat = BridgeStore.getChats().get(database.getBridgedChatID(database.getBridgedEndpointId(channel)));
                BridgeEndpoint source = BridgeStore.getEndpointByIdentifier(chat, channel);

                if (source == null) return;

                if (event.getMessage().startsWith("*.net") || event.getMessage().startsWith(IRC.client.getServerInfo().getAddress().get())) {
                    if (!netsplitActive) {
                        netsplitActive = true;
                        // make a new netsplit object
                        String[] netsplitServers = new String[2];

                        netsplitServers[0] = event.getMessage().split(" ")[0];
                        netsplitServers[1] = event.getMessage().split(" ")[1];

                        netsplit = new Netsplit(source, netsplitServers);

                        scheduler.schedule(() -> chat.netsplitQuits(netsplit, source), Netsplit.NETSPLIT_WAIT_TIME, TimeUnit.SECONDS);
                    }

                    netsplit.addQuit(event.getUser().getNick());
                    return;
                }

                if (event.getActor().getNick().equals(IRCReference.nickname)) {
                    chat.sendUpdate(EventType.DISCONNECT, channel, source, null);
                } else {
                    chat.sendUpdate(EventType.QUIT, event.getUser().getNick(), source, event.getMessage());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Handler
    public void onAction(ChannelCtcpEvent event) throws SQLException {
        if (event.getCommand().equals("ACTION")) {
            if (event.getActor().getNick().equals(IRCReference.nickname)) return;
            BridgedChat chat = BridgeStore.getChats().get(database.getBridgedChatID(database.getBridgedEndpointId(event.getChannel().getName())));
            if (chat == null) {
                return;
            }
            String identifier = event.getChannel().getName();
            BridgeEndpoint source = BridgeStore.getEndpointByIdentifier(chat, identifier);

            String avatarUrl = database.getBridgedEndpointAvatar(source.getId());
            chat.sendActionMessage(new BridgeMessage(event.getActor().getNick(), avatarUrl, event.getMessage().substring(7)), source);
        }
    }

    @Handler
    public void onNicknameChange(UserNickChangeEvent event) {
        event.getOldUser().getChannels().forEach(channel -> {
            try {
                BridgedChat chat = BridgeStore.getChats().get(database.getBridgedChatID(database.getBridgedEndpointId(channel)));
                BridgeEndpoint source = BridgeStore.getEndpointByIdentifier(chat, channel);

                chat.sendUpdate(EventType.NAME_CHANGE, event.getOldUser().getNick(), source, event.getNewUser().getNick());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // Telegram content event
    @Override
    public void onMessage(net.slimediamond.telegram.event.MessageReceivedEvent event) throws SQLException, IOException {
        if (event.getMessage().getContent().startsWith(TelegramReference.prefix)) return;
        BridgedChat chat = BridgeStore.getChats().get(database.getBridgedChatID(database.getBridgedEndpointId(String.valueOf(event.getChat().getId()))));
        if (chat == null) {
            return;
        }
        String identifier = String.valueOf(event.getChat().getId());
        BridgeEndpoint source = BridgeStore.getEndpointByIdentifier(chat, identifier);

        File avatar = event.getSender().getProfilePhoto();
        String avatarUrl;
        if (avatar != null) {
            avatarUrl = avatar.download();
        } else {
            avatarUrl = event.getChat().getPhoto().download();
        }

        String name = event.getSender().getFirstName() + " " + event.getSender().getLastName();

        BridgeMessage message = new BridgeMessage(name, avatarUrl, event.getMessage().getContent());

        if (event.getMessage().getPhoto() != null) {
            message.addFile(HTTPUtil.downloadFile(event.getMessage().getPhoto().download(), event.getMessage().getPhoto().filePath().split("/")[1]));
        }

        chat.sendMessage(message, source);
    }

    @Override
    public void onUserJoinChat(UserAddedToChatEvent event) throws SQLException {
        BridgedChat chat = BridgeStore.getChats().get(database.getBridgedChatID(database.getBridgedEndpointId(String.valueOf(event.getChat().getId()))));
        if (chat == null) {
            return;
        }
        String identifier = String.valueOf(event.getChat().getId());
        BridgeEndpoint source = chat.getEndpoints().stream()
                .filter(endpoint -> identifier.equals(endpoint.getUniqueIdentifier()))
                .findFirst() // Returns an Optional<BridgeEndpoint>
                .orElseThrow(() -> new RuntimeException("No matching endpoint found for identifier: " + identifier));

        chat.sendUpdate(EventType.JOIN, event.getNewUser().getFullName(), source, event.getUser().getFullName());
    }

    @Override
    public void onUserLeaveChat(UserRemovedFromChatEvent event) throws SQLException {
        BridgedChat chat = BridgeStore.getChats().get(database.getBridgedChatID(database.getBridgedEndpointId(String.valueOf(event.getChat().getId()))));
        if (chat == null) {
            return;
        }
        String identifier = String.valueOf(event.getChat().getId());
        BridgeEndpoint source = chat.getEndpoints().stream()
                .filter(endpoint -> identifier.equals(endpoint.getUniqueIdentifier()))
                .findFirst() // Returns an Optional<BridgeEndpoint>
                .orElseThrow(() -> new RuntimeException("No matching endpoint found for identifier: " + identifier));

        chat.sendUpdate(EventType.LEAVE, event.getNewUser().getFullName(), source, event.getUser().getFullName());
    }
}
