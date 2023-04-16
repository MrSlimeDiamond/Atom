const { EmbedBuilder } = require('@discordjs/builders')
const modules = require('../modules')
const Discord = require('discord.js')
const ROOT = require('../index').__dirname
const path = require('node:path')

class LoggerModule {
    constructor(client) {
        this.client = client

        this.name = 'logger'
        this.description = 'Logs actions on Discord servers'
        this.configLocation = path.join(ROOT, 'module_configs/logger.json')
        this.enabledGuilds = []
    }

    onRegister() {
        const config = require(this.configLocation)

        this.client.on('messageDelete', async message => {
            if (!this.enabledGuilds.includes(message.guildId)) return
            if (!message.content) return
            // prevent weird shit from happening
            if (
                message.author.id == this.client.user.id ||
                message.author.id == null
            )
                return

            const channel = this.client.channels.cache.get(
                config[message.guildId].logChannel
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

        this.client.on('messageUpdate', (oldMsg, newMsg) => {
            if (!this.enabledGuilds.includes(newMsg.guildId)) return

            // Sometimes it can be weird
            if (oldMsg.content == newMsg.content) return

            const channel = this.client.channels.cache.get(
                config[newMsg.guild.id].logChannel
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

    onEnable() {}

    onDisable() {}
}

module.exports = LoggerModule
