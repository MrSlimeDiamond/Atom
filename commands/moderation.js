const Discord = require("discord.js");
const modules = require("../modules");

module.exports = {
  data: new Discord.SlashCommandBuilder()
    .setName("moderation")
    .setDescription("Commands for the moderation module")
    .addSubcommand((subcommand) =>
      subcommand
        .setName("purge")
        .setDescription("Remove a certian amount of messages from a channel")
        .addIntegerOption((option) =>
          option
            .setName("amount")
            .setDescription("The amount of messages to remove")
            .setRequired(true)
        )
    )
    .setDefaultMemberPermissions(Discord.PermissionFlagsBits.ManageMessages),
  async execute(interaction) {
    guildId = await interaction.guildId;
    if (!modules[5].enabledGuilds.includes(guildId)) {
      const embed = new Discord.EmbedBuilder()
        .setColor(0xff0000)
        .setTitle("Module is not enabled")
        .setDescription(
          "This module is not enabled for this server. Please ask a bot admin if you want it enabled."
        );

      interaction.reply({ embeds: [embed] });
      return;
    }

    // /moderation purge
    if (interaction.options.getSubcommand() == "purge") {
      interaction.deferReply({ ephemeral: true });

      const amount = interaction.options.getInteger("amount");

      await interaction.channel.messages
        .fetch({ limit: amount })
        .then(async (messages) => {
          await interaction.channel.bulkDelete(messages);
        });

      const embed = new Discord.EmbedBuilder()
        .setColor(0x4feb34)
        .setTitle("Moderation / Purge")
        .setDescription(
          "Deleted " +
            amount +
            " messages from <#" +
            interaction.channel.id +
            ">"
        );

      await interaction.editReply({ embeds: [embed], ephemeral: true });
    }
  },
};
