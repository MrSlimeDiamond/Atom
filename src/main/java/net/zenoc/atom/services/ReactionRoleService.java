package net.zenoc.atom.services;

import com.google.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.zenoc.atom.Atom;
import net.zenoc.atom.annotations.Service;

@Service("reaction roles")
public class ReactionRoleService extends ListenerAdapter {
    @Inject
    private JDA jda;

    @Service.Start
    public void startService() throws Exception {
        jda.addEventListener(this);
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
