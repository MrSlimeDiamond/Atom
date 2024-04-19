package net.slimediamond.atom.inject.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.dv8tion.jda.api.JDA;
import net.slimediamond.atom.inject.providers.JDAProvider;
import net.slimediamond.atom.services.system.ServiceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class ServiceModule extends AbstractModule {
    private ServiceContainer container;
    public ServiceModule(ServiceContainer container) {
        this.container = container;
    }

    @Override
    @Nullable
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
