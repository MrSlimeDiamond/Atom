package net.zenoc.atom.inject.modules;

import com.google.inject.AbstractModule;
import net.zenoc.atom.annotations.Service;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

public class AtomModule extends AbstractModule {
    @Override
    protected void configure() {
        // Create a ClasspathScanner to scan for classes with the @Service annotation
        ClasspathScanner.create(Service.class).scanModulesAndInstall(binder());

        // Automatically bind all classes annotated with @Service to the Multibinder
        // Optionally, call the startup methods for enabled services
        for (Class<?> serviceClass : getClassesWithAnnotation(Service.class)) {
            Service serviceAnnotation = serviceClass.getAnnotation(Service.class);
            if (serviceAnnotation.enabled()) {
                bind((Class<Object>) serviceClass).annotatedWith(Service.class);
                invokeStartupMethods(serviceClass);
            }
        }
    }

    // Helper method to get all classes with a specific annotation using Reflections
    private Set<Class<?>> getClassesWithAnnotation(Class<? extends Annotation> annotation) {
        Reflections reflections = new Reflections("net.zenoc.atom.services"); // Specify the package to scan
        return reflections.getTypesAnnotatedWith(annotation);
    }

    // Helper method to invoke the startup methods for enabled services
    private void invokeStartupMethods(Class<?> serviceClass) {
        Method[] methods = serviceClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Service.Start.class)) {
                try {
                    Object serviceInstance = injector.getInstance(serviceClass);
                    method.invoke(serviceInstance);
                } catch (Exception e) {
                    // Handle any exceptions that may occur during reflection
                    e.printStackTrace();
                }
            }
        }
    }
}
