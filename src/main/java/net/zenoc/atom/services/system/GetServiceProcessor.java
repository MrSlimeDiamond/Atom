package net.zenoc.atom.services.system;

import net.zenoc.atom.Atom;
import net.zenoc.atom.annotations.GetService;
import net.zenoc.atom.annotations.Service;

import java.lang.reflect.Field;

// ChatGPT wrote this and it worked first try
public class GetServiceProcessor {
    public static void processAnnotations(Object instance) {
        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(GetService.class)) {
                String serviceName = field.getType().getAnnotation(Service.class).value();
                Object serviceInstance = Atom.getServiceManager().getInstance(serviceName);
                if (serviceInstance != null) {
                    field.setAccessible(true);
                    try {
                        field.set(instance, serviceInstance);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}