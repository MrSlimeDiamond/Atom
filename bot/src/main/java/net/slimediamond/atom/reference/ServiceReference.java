package net.slimediamond.atom.reference;

import java.util.ArrayList;
import java.util.List;

public class ServiceReference {
    // List.of doesn't work here, no idea why
    public static final List<String> servicePackages = new ArrayList<>();

    static {
        servicePackages.add("net.slimediamond.atom.data");
        servicePackages.add("net.slimediamond.atom.discordbot");
        servicePackages.add("net.slimediamond.atom.telegram");
        servicePackages.add("net.slimediamond.atom.irc");
        servicePackages.add("net.slimediamond.atom.services");
    }
}
