const { EmbedBuilder } = require('@discordjs/builders')
const modules = require('../modules')
const config = require('../config.json')
async function loggerModule(client) {
    client.on('messageDelete', async message => {
        if (!modules[0].enabledGuilds.includes(message.guildId)) return
        if (!message.content) return
        // prevent weird shit from happening
        if (message.author.id == client.user.id || message.author.id == null)
            return

        const channel = client.channels.cache.get(
            config.guilds[message.guildId].logChannel
        )

        const embed = new EmbedBuilder()
            .setColor(0x0076ff)
            .setAuthor({
                name: message.author.username,
                iconURL: message.author.avatarURL(),
            })
            .setTitle('Message Deleted [<#' + message.channel.id + '>]')
            .setDescription(message.content)
            .setTimestamp()

        channel.send({ embeds: [embed] })
    })

    client.on('messageUpdate', (oldMsg, newMsg) => {
        if (!modules[0].enabledGuilds.includes(newMsg.guildId)) return

        // Sometimes it can be weird
        if (oldMsg.content == newMsg.content) return

        const channel = client.channels.cache.get(
            config.guilds[newMsg.guild.id].logChannel
        )

        if (!oldMsg || !newMsg || oldMsg == null || newMsg == null) {
            return
        }

        const embed = new EmbedBuilder()
            .setColor(0x0076ff)
            .setAuthor({
                name: newMsg.author.username,
                iconURL: newMsg.author.avatarURL(),
            })
            .setTitle('Message Edited [<#' + oldMsg.channel.id + '>]')
            .addFields(
                { name: 'Original Message', value: oldMsg.content },
                { name: 'New Message', value: newMsg.content }
            )
            .setTimestamp()

        const jumpEmbed = new EmbedBuilder()
            .setColor(0xff0000)
            .setAuthor({ name: 'Jump', url: newMsg.url })

        channel.send({ embeds: [embed, jumpEmbed] })
    })
}

module.exports.module = loggerModule
