package net.zenoc.atom.services;

import com.google.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.zenoc.atom.Atom;
import net.zenoc.atom.annotations.GetService;
import net.zenoc.atom.annotations.Service;
import net.zenoc.atom.database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

@Service("discord message cache")
public class MessageCacheService extends ListenerAdapter {
    @Inject
    private JDA jda;

    @Inject
    private Logger logger;
    
    @GetService
    private Database database;

    @Service.Start
    public void startService() throws Exception {
        jda.addEventListener(this);
    }
    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        // Don't cache messages that don't really need to be cached
        if (!event.isFromGuild() || event.getAuthor().getIdLong() == event.getJDA().getSelfUser().getIdLong()) return;
        try {
            database.addMessage(event.getMessageIdLong(), event.getGuild().getIdLong(), event.getAuthor().getIdLong(), event.getMessage().getContentDisplay());
        } catch (SQLException e) {
            logger.error("SQLException when caching message, is the database down?");
            e.printStackTrace();
        }
    }
}
