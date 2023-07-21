package net.zenoc.atom.inject.modules;

import com.google.inject.*;

public class AtomModule extends AbstractModule {
    @Override
    protected void configure() {
        // Provide this module to services if they want it
        // Not used anymore, but why not have it?
        bind(AtomModule.class).toInstance(this);
    }
}
