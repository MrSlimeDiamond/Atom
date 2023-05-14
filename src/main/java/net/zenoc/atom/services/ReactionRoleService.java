package net.zenoc.atom.services;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.zenoc.atom.Atom;

public class ReactionRoleService extends ListenerAdapter implements Service {

    @Override
    public void startService() throws Exception {
        DiscordBot.jda.addEventListener(this);
    }

    @SubscribeEvent
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser().isBot()) return;
        if (event.isFromGuild()) {
            if (Atom.database.messageHasReactionRoles(event.getMessageIdLong())) {
                Atom.database.getReactionRole(event.getMessageIdLong(), event.getEmoji()).ifPresent(role -> {
                    event.getGuild().addRoleToMember(event.getUser(), role).queue();
                });
            }
        }
    }

    @SubscribeEvent
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if (event.getUser().isBot()) return;
        if (event.isFromGuild()) {
            if (Atom.database.messageHasReactionRoles(event.getMessageIdLong())) {
                Atom.database.getReactionRole(event.getMessageIdLong(), event.getEmoji()).ifPresent(role -> {
                    event.getGuild().removeRoleFromMember(event.getUser(), role).queue();
                });
            }
        }
    }
}
