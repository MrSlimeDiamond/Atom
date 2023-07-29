package net.slimediamond.atom.discord;

import net.slimediamond.atom.discord.annotations.Command;

import java.lang.reflect.Method;

public class AtomCommand {

    Command command;
    Object clazz;
    Method method;

    public AtomCommand(Command command, Object clazz, Method method) {
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
