package net.slimediamond.atom.irc;

import net.slimediamond.atom.reference.IRCReference;
import org.kitteh.irc.client.library.element.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class McObotMessageParser {
    String message;
    User user;
    McObotMessageType type = McObotMessageType.UNKNOWN;
    public McObotMessageParser(User user, String message) {
        this.message = message;
        this.user = user;

        if (!user.getNick().equals("McObot")) {
            this.type = McObotMessageType.NON_MCOBOT;
            return;
        }

        Pattern joinMessage = Pattern.compile("\\(MCS\\) .* joined the game");
        Pattern leaveMessage = Pattern.compile("\\(MCS\\) .* (left the game|disconnected:.*)");
        Pattern chatMessage = Pattern.compile("\\(MCS\\) <.*> .*");

        Matcher joinMatcher = joinMessage.matcher(message);
        Matcher leaveMatcher = leaveMessage.matcher(message);
        Matcher chatMatcher = chatMessage.matcher(message);

        if (joinMatcher.find()) {
            this.type = McObotMessageType.JOINGAME;
        } else if (leaveMatcher.find()) {
            this.type = McObotMessageType.LEAVEGAME;
        } else if (chatMatcher.find()) {
            this.type = McObotMessageType.CHATMESSAGE;
        }
    }

    public McObotMessageType getType() {
        return this.type;
    }

    public boolean isJoinMessage() {
        return this.getType() == McObotMessageType.JOINGAME;
    }

    public boolean isLeaveMessage() {
        return this.getType() == McObotMessageType.LEAVEGAME;
    }

    public boolean isChatMessage() {
        return this.getType() == McObotMessageType.CHATMESSAGE;
    }

    public boolean isCommandMessage() {
        if (!isChatMessage()) return false;
        Pattern command = Pattern.compile("\\(MCS\\) <.*> " + IRCReference.prefix);
        Matcher commandMatcher = command.matcher(message);
        return commandMatcher.find();
    }

    // Extract the sender username from a chat content
    public String getSenderUsername() {
        if (isChatMessage()) {
            Pattern senderPattern = Pattern.compile("\\(MCS\\) <(.*?)> .*");
            Matcher matcher = senderPattern.matcher(message);
            if (matcher.find()) {
                return matcher.group(1).strip(); // Return the sender username
            }
        }
        return null;
    }

    // Extract the actual content content from a chat content
    public String getMessageContent() {
        if (isChatMessage()) {
            Pattern messagePattern = Pattern.compile("\\(MCS\\) <.*?> (.*)");
            Matcher matcher = messagePattern.matcher(message);
            if (matcher.find()) {
                return matcher.group(1); // Return the content content
            }
        }
        return null;
    }
}
