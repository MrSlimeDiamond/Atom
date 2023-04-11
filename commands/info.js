const { SlashCommandBuilder, EmbedBuilder } = require("discord.js");
const os = require("os");
const axios = require("axios");
const DateTime = require("../util/DateTime");
module.exports = {
  data: new SlashCommandBuilder()
    .setName("info")
    .setDescription("Show various stats about the bot")
    .addSubcommand((subcommand) =>
      subcommand.setName("host").setDescription("Show host information")
    )
    .addSubcommand((subcommand) =>
      subcommand.setName("bot").setDescription("Show bot information")
    ),
  async execute(interaction) {
    const client = require("../index").client;

    await interaction.deferReply();

    let embed = new EmbedBuilder()

      .setColor(0xff0000)
      .setTitle("Incorrect Usage")
      .setDescription("Please provide a subcommand");

    if (interaction.options.getSubcommand() == "host") {
      const hostname = os.hostname().toString();
      const release = os.release().toString();
      const platform = os.platform().toString();

      const hostUptime__ = os.uptime();
      const hostUptime_ = new DateTime(hostUptime__, "s");
      const hostUptime = hostUptime_.h();

      let ip;

      await axios.get("http://ipinfo.io").then(function (result) {
        ip = result.data.ip;
      });

      embed = new EmbedBuilder()
        .setColor(0x4feb34)
        .setTitle("Host information")
        .addFields(
          { name: "Hostname", value: hostname },
          { name: "OS", value: release + " (" + platform + ")" },
          { name: "IP", value: ip },
          { name: "Host uptime", value: hostUptime }
        );
    } else if (interaction.options.getSubcommand() == "bot") {
      const botUptime__ = process.uptime();
      const botUptime_ = new DateTime(botUptime__, "s");
      const botUptime = botUptime_.h();

      embed = new EmbedBuilder()
        .setColor(0x4feb34)
        .setTitle("Bot information")
        .addFields(
          { name: "Bot uptime", value: botUptime },
          { name: "Guild Count", value: client.guilds.cache.size.toString() }
        );
    }

    await interaction.editReply({ embeds: [embed] });
  },
};
