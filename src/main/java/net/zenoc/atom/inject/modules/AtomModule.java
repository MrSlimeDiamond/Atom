package net.zenoc.atom.inject.modules;

import com.google.inject.*;

public class AtomModule extends AbstractModule {
    @Override
    protected void configure() {
        // Provide this module to services if they want it
        // Not used anymore, but why not have it?
        bind(AtomModule.class).toInstance(this);
    }

    // Provides services to other services
    // TODO: Automatically do this (I tried for hours)

    // TODO: Why is it broken

//    @Provides
//    public Database getDatabase() {
//        return Atom.getServiceManager().getInstance(Database.class);
//    }
//
//    @Provides
//    public DiscordBot getDiscordBot() {
//        return Atom.getServiceManager().getInstance(DiscordBot.class);
//    }
//
//    @Provides
//    public IRC getIRC() {
//        return Atom.getServiceManager().getInstance(IRC.class);
//    }
//
//    @Provides
//    public ChatBridgeService getChatBridgeService() {
//        return Atom.getServiceManager().getInstance(ChatBridgeService.class);
//    }
//
//    @Provides
//    public DiscordLoggerService getDiscordLoggerService() {
//        return Atom.getServiceManager().getInstance(DiscordLoggerService.class);
//    }
//
//    @Provides
//    public MemeVoteService getMemeVoteService() {
//        return Atom.getServiceManager().getInstance(MemeVoteService.class);
//    }
//
//    @Provides
//    public MessageCacheService getMessageCacheService() {
//        return Atom.getServiceManager().getInstance(MessageCacheService.class);
//    }
//
//    @Provides
//    public Pinnerino getPinnerino() {
//        return Atom.getServiceManager().getInstance(Pinnerino.class);
//    }
//
//    @Provides
//    public ReactionRoleService getReactionRoleService() {
//        return Atom.getServiceManager().getInstance(ReactionRoleService.class);
//    }
//
//    @Provides
//    public TwitchNotifier getTwitchNotifier() {
//        return Atom.getServiceManager().getInstance(TwitchNotifier.class);
//    }
}
