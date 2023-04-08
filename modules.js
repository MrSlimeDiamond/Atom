let modules = [
  {
    name: "logger",
    description: "Logs actions that happen on a Discord server",
    moduleLocation: "./modules/logger.js",
    enabledGuilds: [],
    scope: "guild"
  },
  {
    name: "reachi_skyblock",
    description: "When Reachi logs off skyblock it go bam",
    moduleLocation: "./modules/reachi_skyblock_pinger.js",
    enabledGuilds: [],
    scope: "guild"
  },
  {
    name: "portal2",
    description: "autorender v2 ig?",
    moduleLocation: "./modules/portal2.js",
    enabledGuilds: [],
    scope: "guild"
  },
  {
    name: "irc",
    description: "IRC bot",
    moduleLocation: "./modules/irc/irc.js",
    configLocation: "./module_configs/irc.json",
    enabledGuilds: [],
    scope: "global"
  },
  {
    name: "pinnerino",
    description: "pin messages with reactions",
    moduleLocation: "./modules/pinnerino.js",
    configLocation: "./module_configs/pinnerino.js",
    enabledGuilds: [],
    scope: "guild"
  }
];

module.exports = modules;
