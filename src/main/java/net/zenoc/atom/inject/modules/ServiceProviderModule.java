package net.zenoc.atom.inject.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import net.dv8tion.jda.api.JDA;
import net.zenoc.atom.inject.providers.JDAProvider;

public class ServiceProviderModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(JDA.class).toProvider(new JDAProvider()).in(Singleton.class);
    }
}
