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
    .setName("module-disable")
    .setDescription("(Admin only) Disable a module")
    //.setDefaultMemberPermissions(PermissionFlagsBits.Administrator)
    .addStringOption((option) =>
      option
        .setName("module")
        .setDescription("The module to disable")
        .setRequired(true)
    ),
  async execute(interaction) {
    if (config.bot.admins.includes(interaction.user.id)) {
      const moduleToDisable = interaction.options.getString("module");
      /*
        if (!modules[moduleToDisable].enabledGuilds.includes(interaction.guildId)) {
          const embed = new EmbedBuilder()
          .setColor(0xff0000)
          .setTitle("Module already disabled")
          .setDescription("Module " + moduleToDisable + " is already disabled for this guild");
  
          interaction.reply({ embeds: [embed] })
          return
        }
        */
      module = bot.moduleHandler.disableModule(
        interaction.guildId,
        moduleToDisable
      );
      if (module) {
        const embed = new EmbedBuilder()
          .setColor(0x4feb34)
          .setTitle("Module disabled")
          .setDescription(
            "Module " +
              moduleToDisable +
              " was successfully disabled for this guild"
          );
        interaction.reply({ embeds: [embed] });
      } else {
        const embed = new EmbedBuilder()
          .setColor(0xff0000)
          .setTitle("Module does not exist")
          .setDescription("Module " + moduleToDisable + " does not exist");

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
