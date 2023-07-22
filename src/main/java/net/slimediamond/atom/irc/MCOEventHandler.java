package net.slimediamond.atom.irc;

import net.engio.mbassy.listener.Handler;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;

public class MCOEventHandler {
    @Handler
    public void onMessage(ChannelMessageEvent event) {
        McObotMessageParser mcObotMessageParser = new McObotMessageParser(event.getActor(), event.getMessage());

        if (mcObotMessageParser.isJoinMessage()) {
            // I'm sure this won't throw any out of bounds exceptions... clueless
            String username = event.getMessage().split("\\(MCS\\) ")[1].split(" joined the game")[0];
            //System.out.println("(MCO) " + username + " joined");
            try {
                MCOEvents.onMCOJoin(username);
            } catch (Exception e) {
                System.out.println("Exception occurred in MCO player join");
                throw new RuntimeException(e);
            }
        } else if (mcObotMessageParser.isLeaveMessage()) {
            // I'm sure this won't throw any out of bounds exceptions... clueless
            String username = event.getMessage().split("\\(MCS\\) ")[1].split(" (left|disconnected:)")[0];
            //System.out.println("(MCO) " + username + " disconnected");
            try {
                MCOEvents.onMCOLeave(username);
            } catch (Exception e) {
                System.out.println("Exception occurred in MCO player join");
                throw new RuntimeException(e);
            }
        }
    }
}
