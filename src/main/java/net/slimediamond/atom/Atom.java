package net.slimediamond.atom;

import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.launch.AppLaunch;
import net.slimediamond.atom.services.system.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Atom {
    private static final Logger log = LoggerFactory.getLogger("atom");
    public static Config config;
    public static String ip;
    public static Database database;

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
        refreshIP();

        log.info("Got IP: " + ip);

        log.info("Validated everything -- proceeding with app launch");

        // AppLaunch will handle starting services and other tasks that should happen during startup
        AppLaunch.launch();

    }

    public static void shutdown(Class<?> clazz) throws Exception {
        log.info("Graceful shutdown called from: " + clazz.getSimpleName());
        log.info("Shutting down services...");
        serviceManager.shutdownAll();
        log.info("Bye!");
        System.exit(1);
    }

    public static void refreshIP() {
        try {
            URL url = new URL("https://checkip.amazonaws.com");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            ip = br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ServiceManager getServiceManager() {
        return serviceManager;
    }

    protected static void setServiceManager(ServiceManager serviceManager) {
        Atom.serviceManager = serviceManager;
    }
}
