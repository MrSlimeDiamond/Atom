const {
  SlashCommandBuilder,
  PermissionFlagsBits,
  EmbedBuilder,
} = require("discord.js");
const bot = require("../index");
const config = require("../config.json");
const modules = require("../modules");

module.exports = {
  data: new SlashCommandBuilder()
    .setName("module-enable")
    .setDescription("(Admin only) Enable a module")
    //.setDefaultMemberPermissions(PermissionFlagsBits.Administrator)
    .addStringOption((option) =>
      option
        .setName("module")
        .setDescription("The module to enable")
        .setRequired(true)
    ),
  async execute(interaction) {
    if (config.bot.admins.includes(interaction.user.id)) {
      const moduleToEnable = interaction.options.getString("module");
      /*
      if (modules[moduleToEnable].enabledGuilds.includes(interaction.guildId)) {
        const embed = new EmbedBuilder()
        .setColor(0xff0000)
        .setTitle("Module already enabled")
        .setDescription("Module " + moduleToEnable + " is already enabled for this guild");

        interaction.reply({ embeds: [embed] })
        return
      }
      */
      module = bot.moduleHandler.enableModule(
        interaction.guildId,
        moduleToEnable
      );
      if (module) {
        const embed = new EmbedBuilder()
          .setColor(0x4feb34)
          .setTitle("Module enabled")
          .setDescription(
            "Module " +
              moduleToEnable +
              " was successfully enabled for this guild"
          );
        interaction.reply({ embeds: [embed] });
      } else {
        const embed = new EmbedBuilder()
          .setColor(0xff0000)
          .setTitle("Module does not exist")
          .setDescription("Module " + moduleToEnable + " does not exist");

        interaction.reply({ embeds: [embed] });
      }
    } else {
      const embed = new EmbedBuilder()
        .setColor(0xff0000)
        .setTitle("No permission")
        .setDescription("You do not have permission to use this command");

      interaction.reply({ embeds: [embed] });
    }
  },
};
