package net.slimediamond.atom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Config {
    private static final Logger log = LoggerFactory.getLogger(Config.class);
    private Properties prop;
    public Config() throws IOException {
        File botConfig = new File("./config/discordbot.cfg");
        File dbConfig = new File("./config/database.cfg");
        File ircConfig = new File("./config/irc.cfg");
        File twitchConfig = new File("./config/twitch.cfg");
        Files.createDirectories(Paths.get("config/")); // Generate config directory, does nothing if it doesn't exist
        if (!botConfig.exists()) {
            log.info("Generating discord bot config");
            try (OutputStream output = Files.newOutputStream(Paths.get("./config/discordbot.cfg"))) {

                prop = new Properties();

                prop.setProperty("token", "BOT_TOKEN_HERE");
                prop.setProperty("prefix", "!a ");

                prop.store(output, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!dbConfig.exists()) {
            log.info("Generating database config");
            try (OutputStream output = Files.newOutputStream(Paths.get("./config/database.cfg"))) {
                prop = new Properties();

                prop.setProperty("host", "127.0.0.1");
                prop.setProperty("port", "3306");
                prop.setProperty("database", "atom");
                prop.setProperty("username", "atom-bot");
                prop.setProperty("password", "abc123");

                prop.store(output, null);
            } catch(IOException e) {
                log.error("Stack trace");
                e.printStackTrace();
            }
        } if (!ircConfig.exists()) {
            try (OutputStream output = Files.newOutputStream(Paths.get("./config/irc.cfg"))) {
                prop = new Properties();

                prop.setProperty("host", "irc.esper.net");
                prop.setProperty("port", "6697");
                prop.setProperty("ssl", "true");
                prop.setProperty("nickname", "Atom");
                prop.setProperty("realname", "SlimeDiamond's bot");
                prop.setProperty("username", "atom-bot");
                prop.setProperty("nickserv-username", "atom");
                prop.setProperty("nickserv-password", "abc1234");
                prop.setProperty("prefix", "!a ");

                prop.store(output, null);
            } catch(IOException e) {
                e.printStackTrace();
            }
        } if (!twitchConfig.exists()) {
            try (OutputStream output = Files.newOutputStream(Paths.get("./config/twitch.cfg"))) {
                prop = new Properties();

                prop.setProperty("token", "AUTH_TOKEN_GOES_HERE");

                prop.store(output, null);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Properties getPropertiesFile(String file) throws IOException {
        FileReader reader = new FileReader("./config/" + file + ".cfg");
        prop = new Properties();
        prop.load(reader);
        return prop;
    }
    public Properties discord() throws IOException {
        return getPropertiesFile("discordbot");
    }

    public Properties database() throws IOException {
        return getPropertiesFile("database");
    }

    public Properties irc() throws IOException {
        return getPropertiesFile("irc");
    }

    public Properties twitch() throws IOException {
        return getPropertiesFile("twitch");
    }
}
