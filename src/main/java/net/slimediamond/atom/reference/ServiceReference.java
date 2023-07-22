package net.slimediamond.atom.reference;

import java.util.ArrayList;

public class ServiceReference {
    public static final ArrayList<String> SERVICES_PACKAGES = new ArrayList<>();

    static {
        SERVICES_PACKAGES.add("net.slimediamond.atom.database");
        SERVICES_PACKAGES.add("net.slimediamond.atom.discord");
        SERVICES_PACKAGES.add("net.slimediamond.atom.irc");
        SERVICES_PACKAGES.add("net.slimediamond.atom.services");
    }
}
