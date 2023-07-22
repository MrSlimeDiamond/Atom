package net.slimediamond.atom.services.system;

import com.google.inject.Injector;
import net.dv8tion.jda.api.JDA;
import net.slimediamond.atom.reference.ServiceReference;
import net.slimediamond.atom.common.annotations.Service;
import net.slimediamond.atom.inject.modules.ServiceModule;
import net.slimediamond.atom.inject.providers.JDAProvider;
import net.slimediamond.atom.launch.AppLaunch;
import org.reflections.Reflections;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.*;

public class ServiceManager {
    private JDA jda = new JDAProvider().get();
    private Logger logger = LoggerFactory.getLogger("service manager");
    private ArrayList<ServiceContainer> services = new ArrayList<>();

    public void startAll() throws InterruptedException {
        jda.awaitReady();
        Reflections reflections = new Reflections(ServiceReference.SERVICES_PACKAGES.toArray());
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
                Injector injector = AppLaunch.getInjector().createChildInjector(new ServiceModule(container));
                Object instance = injector.getInstance(clazz);
                container.setInjector(injector);
                container.setInstance(instance);
                GetServiceProcessor.processAnnotations(instance);
                services.add(container);
            }
        }

        services.forEach(service -> {
            logger.info("Starting service {}", service.getMetadata().value());
            service.start();
        });
    }

    public void shutdownAll() {
        services.forEach(service -> {
            logger.info("Shutting down service {}", service.getMetadata().value());
            service.shutdown();
        });
    }

    public ArrayList<ServiceContainer> getServices() {
        return services;
    }

    public <T> T getInstance(Class<T> clazz) {
        return (T) services.stream()
                .filter(service -> service.getClazz() == clazz)
                .findFirst()
                .map(ServiceContainer::getInstance)
                .orElse(null);
    }

    public <T> T getInstance(String name) {
        return (T) services.stream()
                .filter(service -> service.getMetadata().value().equals(name))
                .findFirst()
                .map(ServiceContainer::getInstance)
                .orElse(null);
    }
}
