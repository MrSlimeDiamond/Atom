package net.zenoc.atom.services;

import com.google.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.zenoc.atom.Atom;
import net.zenoc.atom.annotations.GetService;
import net.zenoc.atom.annotations.Service;
import net.zenoc.atom.database.Database;

@Service("reaction roles")
public class ReactionRoleService extends ListenerAdapter {
    @Inject
    private JDA jda;

    @GetService
    private Database database;
    
    @Service.Start
    public void startService() throws Exception {
        jda.addEventListener(this);
    }

    @SubscribeEvent
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser().isBot()) return;
        if (event.isFromGuild()) {
            if (database.messageHasReactionRoles(event.getMessageIdLong())) {
                database.getReactionRole(event.getMessageIdLong(), event.getEmoji()).ifPresent(role -> {
                    event.getGuild().addRoleToMember(event.getUser(), role).queue();
                });
            }
        }
    }

    @SubscribeEvent
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if (event.getUser().isBot()) return;
        if (event.isFromGuild()) {
            if (database.messageHasReactionRoles(event.getMessageIdLong())) {
                database.getReactionRole(event.getMessageIdLong(), event.getEmoji()).ifPresent(role -> {
                    event.getGuild().removeRoleFromMember(event.getUser(), role).queue();
                });
            }
        }
    }
}
