package net.slimediamond.atom.command.exceptions;

public class CommandBuildException extends RuntimeException {
    public CommandBuildException(String reason) {
        super(reason);
    }
}
