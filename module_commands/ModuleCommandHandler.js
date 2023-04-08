const config = require("../config.json");
const logger = require("../logger");
const log = new logger("Module Command Handler");

class ModuleCommandHandler {
  async registerModuleCommands() {
    let commands = [];
    client.commands = new Collection();

    const commandsPath = path.join("");
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
        log.info("Started refreshing application (/) commands.");

        for (let i = 0; i < modules.length; i++) {
          for (let x = 0; x < modules[i].enabledGuilds.length; x++) {
            let guildid = modules[i].enabledGuilds[x];
            await rest.put(
              Routes.applicationCommands(guildid, "1078475641516740608"),
              {
                body: commands,
              }
            );
          }
        }

        log.info("Successfully reloaded application (/) commands.");
      } catch (error) {
        log.error(error);
      }
    })();
  }

  async handle(interaction) {
    const command = interaction.client.commands.get(interaction.commandName);

    if (!command) {
      console.error(
        `No command matching ${interaction.commandName} was found.`
      );
      return;
    }

    try {
      await command.execute(interaction);
    } catch (error) {
      console.error(error);
      if (interaction.replied || interaction.deferred) {
        await interaction.followUp({
          content: "There was an error while executing this command!",
          ephemeral: true,
        });
      } else {
        await interaction.reply({
          content: "There was an error while executing this command!",
          ephemeral: true,
        });
      }
    }
  }
}

module.exports = ModuleCommandHandler;
