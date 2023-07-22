package net.zenoc.atom.services.system;

import com.google.inject.Injector;
import net.zenoc.atom.annotations.Service;

import java.util.Arrays;

public class ServiceContainer {
    private Object instance;
    private Injector injector;
    private Class<?> clazz;
    private Service metadata;

    public ServiceContainer(Class<?> clazz, Service metadata) {
        this.clazz = clazz;
        this.metadata = metadata;
    }

    public void setInjector(Injector injector) {
        this.injector = injector;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Object getInstance() {
        return instance;
    }

    public Injector getInjector() {
        return injector;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Service getMetadata() {
        return metadata;
    }

    public void start() {
        Arrays.stream(clazz.getMethods())
                .filter(method -> method.isAnnotationPresent(Service.Start.class))
                .forEach(method -> {
                    try {
                        method.invoke(instance);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public void shutdown() {
        Arrays.stream(clazz.getMethods())
                .filter(method -> method.isAnnotationPresent(Service.Shutdown.class))
                .forEach(method -> {
                    try {
                        method.invoke(instance);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public void reload() {
        Arrays.stream(clazz.getMethods())
                .filter(method -> method.isAnnotationPresent(Service.Reload.class))
                .forEach(method -> {
                    try {
                        method.invoke(instance);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
