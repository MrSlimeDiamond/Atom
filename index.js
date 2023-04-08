const config = require("./config.json");
const fs = require("node:fs");
const path = require("node:path");
const {
  Client,
  GatewayIntentBits,
} = require("discord.js");
const client = new Client({
  intents: [
    GatewayIntentBits.Guilds,
    GatewayIntentBits.Guilds,
    GatewayIntentBits.GuildMessages,
    GatewayIntentBits.MessageContent,
    GatewayIntentBits.GuildMembers,
    GatewayIntentBits.GuildMessageReactions
  ],
  partials: [
    "USER",
    "GUILD_MEMBER",
    "CHANNEL",
    "MESSAGE",
    "REACTION",
    "GUILD_MESSAGE_REACTIONS"
  ]
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
moduleHandler.registerModule(client, "irc")
moduleHandler.registerModule(client, "pinnerino")

moduleHandler.enableModule("826198598348701796", "logger");
moduleHandler.enableModule("1092297646602993704", "logger")
moduleHandler.enableModule("826198598348701796", "reachi_skyblock");
moduleHandler.enableModule("1092297646602993704", "pinnerino")
moduleHandler.enableModule("696218632618901504", "pinnerino")
moduleHandler.enableModule("1004897099017637979", "pinnerino")


commandHandler.registerDefaultCommands(client);

client.on("ready", () => {
  log.info(`Logged in as ${client.user.tag}!`);
});

client.on("interactionCreate", async (interaction) => {
  if (!interaction.isChatInputCommand()) return;

  commandHandler.handle(interaction);
});

client.login(config.bot.token);

module.exports.moduleHandler = moduleHandler;
module.exports.client = client;
module.exports.__dirname = __dirname;

