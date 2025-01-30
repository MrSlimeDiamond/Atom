package net.slimediamond.atom.services;

import com.google.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.engio.mbassy.listener.Handler;
import net.slimediamond.atom.Atom;
import net.slimediamond.atom.chatbridge.*;
import net.slimediamond.atom.chatbridge.irc.IRCBridgeEndpoint;
import net.slimediamond.atom.irc.IRC;
import net.slimediamond.atom.reference.DiscordReference;
import net.slimediamond.atom.reference.IRCReference;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.common.annotations.Service;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.reference.TelegramReference;
import net.slimediamond.atom.telegram.Telegram;
import net.slimediamond.telegram.File;
import net.slimediamond.telegram.Listener;
import org.kitteh.irc.client.library.event.channel.ChannelCtcpEvent;
import org.kitteh.irc.client.library.event.channel.ChannelJoinEvent;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.kitteh.irc.client.library.event.channel.ChannelPartEvent;
import org.kitteh.irc.client.library.event.user.UserNickChangeEvent;
import org.kitteh.irc.client.library.event.user.UserQuitEvent;

import javax.annotation.Nullable;
import java.sql.SQLException;

@Service(value = "chat bridge", priority = 1)
public class ChatBridgeService extends ListenerAdapter implements Listener {
    @Inject
    @Nullable
    private JDA jda;

    @GetService
    private Database database;

    @Service.Start
    public void startService() throws Exception {
        if (jda == null) return; // You can't build a bridge with just one mountain!
        jda.addEventListener(this);
        IRC.client.getEventManager().registerEventListener(this);
        Telegram.getClient().addListener(this);

        while (IRC.client.getChannels().size() != database.getIRCChannels().size()) {
            Thread.sleep(500);
        }

        // Add endpoints and chat rooms to the storage
        database.getAllChatIds().forEach(chatId -> {
            BridgedChat chat = new BridgedChat();
            try {
                database.getEndpoints(chatId).forEach(chat::addEndpoint);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            BridgeStore.getChats().put(chatId, chat);
        });

        // send our connection messages
        BridgeStore.getChats().forEach((id, chat) -> {
            chat.sendUpdate(EventType.CONNECT, null, null, null);
        });
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
        BridgeEndpoint source = chat.getEndpoints().stream()
                .filter(endpoint -> identifier.equals(endpoint.getUniqueIdentifier()))
                .findFirst() // Returns an Optional<BridgeEndpoint>
                .orElseThrow(() -> new RuntimeException("No matching endpoint found for identifier: " + identifier));
        chat.sendMessage(new BridgeMessage(event.getAuthor().getEffectiveName(), event.getAuthor().getEffectiveAvatarUrl(), event.getMessage().getContentDisplay()), source);
    }

    // IRC Message event
    @Handler
    public void onChannelMessage(ChannelMessageEvent event) throws SQLException {
        if (event.getActor().getNick().equals(IRCReference.nickname)) return;
        System.out.println(database.getBridgedChatID(database.getBridgedEndpointId(event.getChannel().getName())));
        BridgedChat chat = BridgeStore.getChats().get(database.getBridgedChatID(database.getBridgedEndpointId(event.getChannel().getName())));
        if (chat == null) {
            return;
        }
        String identifier = event.getChannel().getName();
        BridgeEndpoint source = chat.getEndpoints().stream()
                .filter(endpoint -> identifier.equals(endpoint.getUniqueIdentifier()))
                .findFirst() // Returns an Optional<BridgeEndpoint>
                .orElseThrow(() -> new RuntimeException("No matching endpoint found for identifier: " + identifier));

        String avatarUrl = database.getBridgedEndpointAvatar(source.getId());
        chat.sendMessage(new BridgeMessage(event.getActor().getNick(), avatarUrl, event.getMessage()), source);

    }

    @Handler
    public void onChannelJoin(ChannelJoinEvent event) throws SQLException {
        BridgedChat chat = BridgeStore.getChats().get(database.getBridgedChatID(database.getBridgedEndpointId(event.getChannel().getName())));
        if (chat == null) return;
        String identifier = event.getChannel().getName();
        BridgeEndpoint source = chat.getEndpoints().stream()
                .filter(endpoint -> identifier.equals(endpoint.getUniqueIdentifier()))
                .findFirst().orElse(null);

        if (source != null) {
            if (event.getActor().getNick().equals(IRCReference.nickname)) {
                chat.sendUpdate(EventType.CONNECT, event.getChannel().getName(), source, null);
                return;
            }
            chat.sendUpdate(EventType.JOIN, event.getActor().getNick(), source, null);
        }
    }

    @Handler
    public void onChannelLeave(ChannelPartEvent event) throws SQLException {
        BridgedChat chat = BridgeStore.getChats().get(database.getBridgedChatID(database.getBridgedEndpointId(event.getChannel().getName())));
        if (chat == null) return;
        String identifier = event.getChannel().getName();
        BridgeEndpoint source = chat.getEndpoints().stream()
                .filter(endpoint -> identifier.equals(endpoint.getUniqueIdentifier()))
                .findFirst().orElse(null);

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
                BridgeEndpoint source = chat.getEndpoints().stream()
                        .filter(endpoint -> channel.equals(endpoint.getUniqueIdentifier()))
                        .findFirst().orElse(null);

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
            System.out.println(database.getBridgedChatID(database.getBridgedEndpointId(event.getChannel().getName())));
            BridgedChat chat = BridgeStore.getChats().get(database.getBridgedChatID(database.getBridgedEndpointId(event.getChannel().getName())));
            if (chat == null) {
                return;
            }
            String identifier = event.getChannel().getName();
            BridgeEndpoint source = chat.getEndpoints().stream()
                    .filter(endpoint -> identifier.equals(endpoint.getUniqueIdentifier()))
                    .findFirst() // Returns an Optional<BridgeEndpoint>
                    .orElseThrow(() -> new RuntimeException("No matching endpoint found for identifier: " + identifier));

            String avatarUrl = database.getBridgedEndpointAvatar(source.getId());
            chat.sendActionMessage(new BridgeMessage(event.getActor().getNick(), avatarUrl, event.getMessage().substring(7)), source);
        }
    }

    @Handler
    public void onNicknameChange(UserNickChangeEvent event) {
        event.getOldUser().getChannels().forEach(channel -> {
            try {
                BridgedChat chat = BridgeStore.getChats().get(database.getBridgedChatID(database.getBridgedEndpointId(channel)));
                BridgeEndpoint source = chat.getEndpoints().stream()
                        .filter(endpoint -> channel.equals(endpoint.getUniqueIdentifier()))
                        .findFirst().orElse(null);

                chat.sendUpdate(EventType.NAME_CHANGE, event.getOldUser().getNick(), source, event.getNewUser().getNick());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // Telegram content event
    @Override
    public void onMessage(net.slimediamond.telegram.events.MessageReceivedEvent event) throws SQLException {
        if (event.getText().startsWith(TelegramReference.prefix)) return;
        BridgedChat chat = BridgeStore.getChats().get(database.getBridgedChatID(database.getBridgedEndpointId(String.valueOf(event.getChat().getId()))));
        if (chat == null) {
            return;
        }
        String identifier = String.valueOf(event.getChat().getId());
        BridgeEndpoint source = chat.getEndpoints().stream()
                .filter(endpoint -> identifier.equals(endpoint.getUniqueIdentifier()))
                .findFirst() // Returns an Optional<BridgeEndpoint>
                .orElseThrow(() -> new RuntimeException("No matching endpoint found for identifier: " + identifier));
        File avatar = event.getSender().getProfilePhoto();
        String avatarUrl;
        if (avatar != null) {
            avatarUrl = avatar.download();
        } else {
            avatarUrl = event.getChat().getPhoto().download();
        }

        String name = event.getSender().getFirstName() + " " + event.getSender().getLastName();

        chat.sendMessage(new BridgeMessage(name, avatarUrl, event.getText()), source);
    }
}
