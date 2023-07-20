package net.zenoc.atom.inject.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.zenoc.atom.annotations.Service;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ServiceModule extends AbstractModule {
    Logger logger = LoggerFactory.getLogger(ServiceModule.class);
    @Override
    protected void configure() {
        Reflections reflections = new Reflections("net.zenoc.atom.services");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Service.class);
        List<Class<?>> sorted = new ArrayList<>(annotated);

        sorted.sort(Comparator.comparingInt(clazz -> {
            Service metadata = clazz.getAnnotation(Service.class);
            return -metadata.priority();
        }));

        Injector injector = Guice.createInjector(new AtomModule());
        for (Class<?> clazz : sorted) {
            Service metadata = clazz.getAnnotation(Service.class);
            if (metadata.enabled()) {
                Object instance = injector.getInstance(clazz);
                Arrays.stream(instance.getClass().getMethods())
                        .filter(method -> method.isAnnotationPresent(Service.Start.class))
                        .forEach(method -> {
                            try {
                                logger.info("Enabling {} (invoking)", metadata.value());
                                method.invoke(instance);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
        }
    }
}
