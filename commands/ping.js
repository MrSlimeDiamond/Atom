const { SlashCommandBuilder, EmbedBuilder } = require("discord.js");
module.exports = {
  data: new SlashCommandBuilder()
    .setName("ping")
    .setDescription("Replies with Pong!"),
  async execute(interaction) {
    const embed = new EmbedBuilder()
      .setColor(0x4feb34)
      .setTitle("Pong!")
      .setDescription("Yeah, the bot works");
    await interaction.reply({ embeds: [embed] });
  },
};
