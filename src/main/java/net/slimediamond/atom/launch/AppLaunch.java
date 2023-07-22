package net.slimediamond.atom.launch;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.slimediamond.atom.services.system.ServiceManager;
import net.slimediamond.atom.Atom;
import net.slimediamond.atom.inject.modules.AtomModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppLaunch extends Atom {
    private static Injector injector;
    private static Logger logger = LoggerFactory.getLogger("launch");

    /*
     * For handling launch logic
     * so that it can be separate from Atom
     */
    public static void launch() throws InterruptedException {
        logger.info("Target launch reached");

        logger.info("Creating injector");

        setServiceManager(new ServiceManager());

        // Parent injector for most things
        injector = Guice.createInjector(new AtomModule());

        logger.info("Starting all services");
        Atom.getServiceManager().startAll();

        logger.info("Started all services");
    }

    public static Injector getInjector() {
        return injector;
    }
}
