package net.zenoc.atom.reference;

import java.util.ArrayList;

public class ServiceReference {
    public static final ArrayList<String> SERVICES_PACKAGES = new ArrayList<>();

    static {
        SERVICES_PACKAGES.add("net.zenoc.atom.database");
        SERVICES_PACKAGES.add("net.zenoc.atom.services");
    }
}
