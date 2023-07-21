package net.zenoc.atom.services;

import com.google.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.zenoc.atom.Atom;
import net.zenoc.atom.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

@Service("discord message cache")
public class MessageCacheService extends ListenerAdapter {
    @Inject
    private JDA jda;

    private static Logger log = LoggerFactory.getLogger(MessageCacheService.class);

    @Service.Start
    public void startService() throws Exception {
        jda.addEventListener(this);
    }
    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        // Don't cache messages that don't really need to be cached
        if (!event.isFromGuild() || event.getAuthor().getIdLong() == event.getJDA().getSelfUser().getIdLong()) return;
        try {
            Atom.database.addMessage(event.getMessageIdLong(), event.getGuild().getIdLong(), event.getAuthor().getIdLong(), event.getMessage().getContentDisplay());
        } catch (SQLException e) {
            log.error("SQLException when caching message, is the database down?");
            e.printStackTrace();
        }
    }
}
