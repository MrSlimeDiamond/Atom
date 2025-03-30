package net.slimediamond.atom;

import net.slimediamond.atom.discordbot.DiscordBot;
import net.slimediamond.atom.irc.IRC;
import net.slimediamond.atom.launch.AppLaunch;
import net.slimediamond.atom.services.ChatBridgeService;
import net.slimediamond.atom.services.system.ServiceManager;
import net.slimediamond.data.Key;
import net.slimediamond.data.identification.ResourceKeyable;
import net.slimediamond.data.registry.BasicRegistry;
import net.slimediamond.data.registry.Registry;
import net.slimediamond.util.network.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Atom {
    private static final Logger log = LoggerFactory.getLogger("atom");
    public static Config config;

    private static ServiceManager serviceManager;
    private static List<Registry<?>> registries = new ArrayList<>();

    static {
        try {
            config = new Config();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        registries.add(new BasicRegistry<>(Key.class));
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

        // Shutdown logic
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Received SIGINT request, shutting down gracefully");
            // graceful shutdown
            shutdown(Atom.class);
        }));
    }

    public static void shutdown(Class<?> clazz) {
        log.info("Graceful shutdown called from: " + clazz.getSimpleName());
        log.info("Shutting down services...");

        // shut down some stuff in a specific order
        serviceManager.getInstance(ChatBridgeService.class).shutdownService();
        serviceManager.getInstance(DiscordBot.class).shutdownService();
        serviceManager.getInstance(IRC.class).shutdownService();

        //serviceManager.shutdownAll();
        log.info("Bye!");
        System.exit(0);
    }

    public static ServiceManager getServiceManager() {
        return serviceManager;
    }

    protected static void setServiceManager(ServiceManager serviceManager) {
        Atom.serviceManager = serviceManager;
    }

    @SuppressWarnings("unchecked")
    public static <T extends ResourceKeyable> Registry<T> getRegistry(Class<T> type) {
        return registries.stream()
                .filter(registry -> registry.getType().equals(type))
                .map(registry -> (Registry<T>)registry)
                .findAny().orElseThrow();
    }
}
