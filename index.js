const config = require("./config.json");
const fs = require("node:fs");
const path = require("node:path");
const {
  Client,
  GatewayIntentBits,
  EmbedBuilder,
  Partials,
} = require("discord.js");
const client = new Client({
  intents: [
    GatewayIntentBits.Guilds,
    GatewayIntentBits.GuildMessages,
    GatewayIntentBits.MessageContent,
    GatewayIntentBits.GuildMembers,
    GatewayIntentBits.GuildMessageReactions,
  ],
  partials: [Partials.Message, Partials.Reaction],
});
const logger = require("./logger");
const log = new logger("Bot");

log.info("Starting bot");

const ModuleHandler = require("./modulehandler");
const moduleHandler = new ModuleHandler();
const CommandHandler = require("./commandhandler");
const commandHandler = new CommandHandler();

moduleHandler.registerModule(client, "logger");
moduleHandler.registerModule(client, "reachi_skyblock");
moduleHandler.registerModule(client, "portal2");
moduleHandler.registerModule(client, "irc");
moduleHandler.registerModule(client, "pinnerino");
moduleHandler.registerModule(client, "moderation")

moduleHandler.enableModule("826198598348701796", "logger");
moduleHandler.enableModule("1092297646602993704", "logger");
moduleHandler.enableModule("826198598348701796", "reachi_skyblock");
moduleHandler.enableModule("1092297646602993704", "pinnerino");
moduleHandler.enableModule("696218632618901504", "pinnerino");
moduleHandler.enableModule("1004897099017637979", "pinnerino");
moduleHandler.enableModule("826198598348701796", "moderation")

client.on("ready", () => {
  commandHandler.registerDefaultCommands(client);
  log.info(`Logged in as ${client.user.tag}!`);
});

client.on("interactionCreate", async (interaction) => {
  if (!interaction.isChatInputCommand()) return;

  commandHandler.handle(interaction);
});

client.on("messageCreate", async (message) => {
  if (message.content.startsWith("!a-admin")) {
    if (!config.bot.admins.includes(message.author.id)) {
      const embed = new Discord.EmbedBuilder()
        .setColor(0xff0000)
        .setTitle("Permission Denied")
        .setDescription("You do not have permission to run this command");
      message.reply(embed);

      return;
    }

    // Atom Config command thing
    args = message.content.split(" ")
    args.shift()

    if (args == null || args.length == 0) {
      message.reply("My options are: config, stop");
    }

    // !a-admin config
    // TODO
    /*
    if (args[0] == "config") {
      args.shift()
      if (args[0] == null) {
        message.reply("My options are: module, global")
        return
      }
      if (args[0] == "module") {
        if (args[1] == null) {
          message.reply("You need to provide a module to change the config for")
          return
        }
      }
      // !a-config global
      if (args[1] == "global") {
        args.shift()

        value = args[0]
        
      }
    }
    */

    // !a-admin stop
    if (args[0] == "stop") {
      log.info("Bot admin requested stop")
      await message.reply("Stopping bot...");
      client.destroy()
      process.exit();
    }
  }

  if (message.content.startsWith("!fetch")) {
    let msgs = 0;
    await message.channel.messages
      .fetch({ cache: true })
      .then(async (messages) => {
        for (const message of messages) {
          msgs++;
        }
      });
    const embed = new EmbedBuilder()
      .setColor(0x4feb34)
      .setTitle("Cached " + msgs + " messages")
      .setDescription(
        msgs +
          " messages were successfully cached from <#" +
          message.channel.id +
          ">"
      );

    message.reply({ embeds: [embed] });
  }
});

client.login(config.bot.token);

module.exports.moduleHandler = moduleHandler;
module.exports.client = client;
module.exports.__dirname = __dirname;
