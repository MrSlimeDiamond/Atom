package net.slimediamond.atom;

import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.launch.AppLaunch;
import net.slimediamond.atom.services.system.ServiceManager;
import net.slimediamond.atom.util.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Atom {
    private static final Logger log = LoggerFactory.getLogger("atom");
    public static Config config;

    private static ServiceManager serviceManager;

    static {
        try {
            config = new Config();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        log.info("Starting...");

        log.info("Checking config");
        if (Atom.config.discord().getProperty("token").equals("BOT_TOKEN_HERE")) {
            log.error("Configuration is not set up!");
            System.exit(1);
        }

        log.info("Got IP: {}", NetworkUtils.getIP());

        log.info("Validated everything -- proceeding with app launch");

        // AppLaunch will handle starting services and other tasks that should happen during startup
        AppLaunch.launch();
    }

    public static void shutdown(Class<?> clazz) throws Exception {
        log.info("Graceful shutdown called from: " + clazz.getSimpleName());
        log.info("Shutting down services...");
        serviceManager.shutdownAll();
        log.info("Bye!");
        System.exit(0);
    }

    public static ServiceManager getServiceManager() {
        return serviceManager;
    }

    protected static void setServiceManager(ServiceManager serviceManager) {
        Atom.serviceManager = serviceManager;
    }
}
