package net.zenoc.atom.annotations;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@BindingAnnotation
public @interface Service {

    /**
     * Service name
     * @return Service name
     */
    String value();

    /**
     * Whether the service is enabled
     * @return Service enabled or disabled
     */
    boolean enabled() default true;

    /**
     * Startup order
     * @return Priority
     */
    int priority() default 0;

    @Retention(RetentionPolicy.RUNTIME)
    @interface Start {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Shutdown {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Reload {

    }
}
