package net.zenoc.atom.services.system;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.zenoc.atom.annotations.Service;
import net.zenoc.atom.inject.modules.AtomModule;
import net.zenoc.atom.inject.modules.ServiceModule;
import org.reflections.Reflections;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class ServiceManager {
    private Logger logger = LoggerFactory.getLogger("service manager");
    private ArrayList<ServiceContainer> services = new ArrayList<>();

    public void startAll() {
        Reflections reflections = new Reflections("net.zenoc.atom.services");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Service.class);
        List<Class<?>> sorted = new ArrayList<>(annotated);

        sorted.sort(Comparator.comparingInt(clazz -> {
            Service metadata = clazz.getAnnotation(Service.class);
            return -metadata.priority();
        }));

        for (Class<?> clazz : sorted) {
            Service metadata = clazz.getAnnotation(Service.class);
            if (metadata.enabled()) {
                ServiceContainer container = new ServiceContainer(clazz, metadata);
                Injector injector = Guice.createInjector(new ServiceModule(container));
                container.setInjector(injector);
                container.setInstance(injector.getInstance(clazz));
                services.add(container);
            }
        }

        services.forEach(service -> {
            logger.info("Starting service {}", service.getMetadata().value());
            service.start();
        });
    }
}
