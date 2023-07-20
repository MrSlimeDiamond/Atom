package net.zenoc.atom;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.zenoc.atom.inject.modules.ServiceModule;
import net.zenoc.atom.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Atom {
    private static final Logger log = LoggerFactory.getLogger(Atom.class);
    public static Config config;
    public static String ip;
    public static DiscordBot discordBot;
    public static Database database;
    public static IRC irc;

    public static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

    static {
        try {
            config = new Config();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        log.info("Starting...");

        refreshIP();
        log.info("Got IP: " + ip);

        if (config.discord().getProperty("token").equals("BOT_TOKEN_HERE")) {
            log.error("Configuration is not set up!");
            System.exit(1);
        }

        log.info("Starting services");

        Injector injector = Guice.createInjector(new ServiceModule());

        log.info("Started all services");

    }

    public static void shutdown(Class<?> clazz) throws Exception {
        log.info("Graceful shutdown called from: " + clazz.getSimpleName());
        log.info("Shutting down services...");
//        for (Service service : services) {
//            log.info("Stopping service: " + service.getClass().getSimpleName());
//            service.shutdownService();
//        }
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
}
