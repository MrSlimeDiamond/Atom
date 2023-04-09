const { SlashCommandBuilder, EmbedBuilder } = require("discord.js");
const os = require("os")
const axios = require("axios")
module.exports = {
  data: new SlashCommandBuilder()
    .setName("info")
    .setDescription("Show various stats about the bot"),
  async execute(interaction) {
    await interaction.deferReply()

    const hostname = os.hostname().toString()
    const release = os.release().toString()
    const platform = os.platform().toString()

    let ip

    await axios.get("http://ipinfo.io").then(function(result) {
        ip = result.data.ip
    })

    const embed = new EmbedBuilder()
      .setColor(0x4feb34)
      .setTitle("Host information")
      .addFields(
        { name: "Hostname", value: hostname },
        { name: "OS", value: release + " (" + platform + ")" },
        { name: "IP", value: ip }
      )

    await interaction.editReply({ embeds: [embed] });
  },
};
