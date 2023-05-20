package net.zenoc.atom;

import net.zenoc.atom.services.*;
import org.kitteh.irc.client.library.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Atom {
    private static final Logger log = LoggerFactory.getLogger(Atom.class);
    public static Config config;
    public static String ip;
    public static DiscordBot discordBot;
    public static AtomDatabase database;
    public static IRC irc;

    public static final List<Service> services;

    static {
        services = new ArrayList<>();

        services.add(database = new AtomDatabase());
        services.add(discordBot = new DiscordBot());
        services.add(new DiscordLoggerService());
        services.add(new API());
        services.add(new MessageCacheService());
        services.add(new Pinnerino());
        services.add(irc = new IRC());
        services.add(new ChatBridgeService());
        services.add(new ReactionRoleService());
        services.add(new TwitchNotifier());

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

        try {
            for (Service service : services) {
                log.info("Starting service: " + service.getClass().getSimpleName());
                service.startService();
            }
        } catch (Exception e) {
            log.error("Exception when starting, throwing...");
            throw new RuntimeException(e);
        }

        log.info("Started all services");

    }

    public static void shutdown(Class<?> clazz) throws Exception {
        log.info("Graceful shutdown called from: " + clazz.getSimpleName());
        log.info("Shutting down services...");
        for (Service service : services) {
            log.info("Stopping service: " + service.getClass().getSimpleName());
            service.shutdownService();
        }
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
