const modules = require("./modules");
const logger = require("./logger");

const log = new logger("Module Handler");

class ModuleHandler {
  /**
   *
   * @param {*} client
   * @param {*} moduleName
   *
   * Assumes module exists
   */
  registerModule(client, moduleName) {
    for (let i = 0; i < modules.length; i++) {
      if (modules[i].name == moduleName) {
        const moduleFile = require(modules[i].moduleLocation);
        if (modules[i].scope == "guild") {
          // guild only module
          moduleFile.module(client);
        } else {
          // global module
          moduleFile.onRegister();
        }

        log.info("Registered module: " + moduleName);
        return true;
      }
    }
    log.error("Could not register module " + moduleName + "! does it exist?");
    return false;
  }
  /**
   *
   * @param {*} guildid
   * @param {*} moduleName
   *
   * Assumes module exists and is registered and disabled
   */
  enableModule(guildid, moduleName) {
    for (let i = 0; i < modules.length; i++) {
      if (modules[i].name == moduleName) {
        modules[i].enabledGuilds.push(guildid);
        if (modules[i].scope == "global") {
          const moduleFile = require(modules[i].moduleLocation);
          moduleFile.onEnable();
        }
        log.info("Enabled module " + moduleName + " for guild " + guildid);
        return true;
      }
    }
    log.error("Could not enable module " + moduleName + "! does it exist?");
    return false;
  }

  /**
   *
   * @param {*} guildid
   * @param {*} moduleName
   *
   * Assumes module exists and is enabled
   */
  disableModule(guildid, moduleName) {
    for (let i = 0; i < modules.length; i++) {
      if (modules[i].name == moduleName) {
        let index = modules[i].enabledGuilds.indexOf(guildid);
        modules[i].enabledGuilds.splice(index, 1);
        if (modules[i].scope == "global") {
          const moduleFile = require(modules[i].moduleLocation);
          moduleFile.onDisable();
        }
        log.info("Disabled module " + moduleName + " for guild " + guildid);
        return true;
      }
    }
    log.error("Could not disable module " + moduleName + "! does it exist?");
    return false;
  }

  registerModuleCommands(guildid, moduleName) {
    let commands = [];
    client.commands = new Collection();
    const commandsPath = path.join("./module_commands/" + moduleName);
    const commandFiles = fs
      .readdirSync(commandsPath)
      .filter((file) => file.endsWith(".js"));

    for (const file of commandFiles) {
      const filePath = path.join(commandsPath, file);
      const command = require(filePath);
      // Set a new item in the Collection with the key as the command name and the value as the exported module
      if ("data" in command && "execute" in command) {
        client.commands.set(command.data.name, command);
        commands.push(command.data.toJSON());
      } else {
        log.warn(
          `The command at ${filePath} is missing a required "data" or "execute" property.`
        );
      }
    }

    const rest = new REST({ version: "10" }).setToken(config.bot.token);

    (async () => {
      try {
        log.info(
          "Started refreshing application (/) commands for module " + moduleName
        );

        await rest.put(
          Routes.applicationCommands("1078475641516740608", guildid),
          { body: commands }
        );

        log.info(
          "Successfully reloaded application (/) commands for module " +
            moduleName
        );
      } catch (error) {
        log.error(error);
      }
    })();
  }
}

module.exports = ModuleHandler;
