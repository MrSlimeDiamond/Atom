package net.zenoc.atom.inject.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.dv8tion.jda.api.JDA;
import net.zenoc.atom.Atom;
import net.zenoc.atom.inject.providers.JDAProvider;
import net.zenoc.atom.services.system.ServiceContainer;
import net.zenoc.atom.services.system.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceModule extends AbstractModule {
    private ServiceContainer container;
    public ServiceModule(ServiceContainer container) {
        this.container = container;
    }

    @Override
    protected void configure() {
        super.configure();
        bind(JDA.class).toProvider(new JDAProvider()).in(Singleton.class);
    }

    @Provides
    @Singleton
    public Logger getLogger() {
        return LoggerFactory.getLogger(container.getMetadata().value());
    }

    @Provides
    @Singleton
    public ServiceContainer getContainer() {
        return container;
    }
}
