const { EmbedBuilder } = require("@discordjs/builders");
const modules = require("../modules");
const config = require("../config.json");
async function loggerModule(client) {
  client.on("messageDelete", async (message) => {
    if (!modules[0].enabledGuilds.includes(message.guildId)) return;
    // prevent weird shit from happening
    if (message.author.id == "1078475641516740608") return
    
    const channel = client.channels.cache.get(
      config.guilds[message.guildId].logChannel
    );

    const embed = new EmbedBuilder()
      .setColor(0x0076ff)
      .setAuthor({ name: message.author.username, iconURL: message.author.avatarURL() })
      .setTitle("Message Deleted")
      .setDescription(message.content)
      .setTimestamp()

    channel.send({ embeds: [embed] });
  });

  client.on("messageUpdate", (oldMsg, newMsg) => {
    if (!modules[0].enabledGuilds.includes(newMsg.guildId)) return;

    // Sometimes it can be weird
    if (oldMsg.content == newMsg.content) return

    const channel = client.channels.cache.get(
      config.guilds[newMsg.guild.id].logChannel
    );

    const embed = new EmbedBuilder()
    .setColor(0x0076ff)
    .setAuthor({ name: newMsg.author.username, iconURL: newMsg.author.avatarURL() })
    .setTitle("Message Edited")
    .addFields(
      { name: "Original Message", value: oldMsg.content },
      { name: "New Message", value: newMsg.content }
    )
    .setTimestamp()

  channel.send({ embeds: [embed] });


  })

}

module.exports.module = loggerModule;
