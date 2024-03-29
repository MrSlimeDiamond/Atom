package net.slimediamond.atom.irc;

import net.slimediamond.atom.irc.annotations.Command;

import java.lang.reflect.Method;

public class IRCCommand {

    Command command;
    Object clazz;
    Method method;

    public IRCCommand(Command command, Object clazz, Method method) {
        this.command = command;
        this.clazz = clazz;
        this.method = method;
    }

    public Command getCommand() {
        return command;
    }

    public Object getCaller() {
        return clazz;
    }

    public Method getMethod() {
        return method;
    }
}
