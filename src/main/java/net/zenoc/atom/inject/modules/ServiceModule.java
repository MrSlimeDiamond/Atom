package net.zenoc.atom.inject.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import net.zenoc.atom.Atom;
import net.zenoc.atom.services.Database;
import net.zenoc.atom.services.system.ServiceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceModule extends AbstractModule {
    private ServiceContainer container;
    public ServiceModule(ServiceContainer container) {
        this.container = container;
    }

    @Provides
    public Logger getLogger() {
        return LoggerFactory.getLogger(container.getMetadata().value());
    }

    @Provides
    public Database getDatabase() {
        return (Database) Atom.getServiceManager().getServices().stream()
                .filter(service -> service.getMetadata().value().equals("database"))
                .findFirst().orElseThrow().getInstance();
    }
}
