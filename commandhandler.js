const config = require("./config.json");
const fs = require("node:fs");
const path = require("node:path");
const { Collection, REST, Routes } = require("discord.js");
const logger = require("./logger");
const log = new logger("Command Handler");

class CommandHandler {
  async registerDefaultCommands(client) {
    let commands = [];
    client.commands = new Collection();
    const commandsPath = path.join(__dirname, "commands");
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

        await rest.put(Routes.applicationCommands(client.user.id), {
          body: commands,
        });

        log.info("Successfully reloaded application (/) commands.");
      } catch (error) {
        log.error(error);
      }
    })();
  }

  async handle(interaction) {
    const command = interaction.client.commands.get(interaction.commandName);

    if (!command) {
      log.error(`No command matching ${interaction.commandName} was found.`);
      return;
    }

    try {
      await command.execute(interaction);
    } catch (error) {
      console.error(error);
      try {
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
      } catch (error) {
        // bruh ssomething really broke
        console.error(error);
      }
    }
  }
}

module.exports = CommandHandler;
