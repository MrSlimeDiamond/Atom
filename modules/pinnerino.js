const Discord = require('discord.js')
const mariadb = require('mariadb')
const modules = require('../modules')
const config = require('../module_configs/pinnerino.json')
const Logger = require('../logger')
const logger = new Logger('Pinnerino Module')

class PinnerinoModule {
    constructor(client) {
        this.client = client

        this.name = 'pinnerino'
        this.description = 'Pin messages with reactions'
        this.enabledGuilds = []
    }

    async onRegister() {
        let pool = mariadb.createPool({
            host: config.database.host,
            user: config.database.username,
            password: config.database.password,
        })

        const connection = await pool.getConnection()

        //connection.query(`USE ${config.database.database}`)

        this.client.on('messageReactionAdd', async reaction => {
            const message = reaction.message
            if (!this.enabledGuilds.includes(message.guildId)) return
            if (reaction.partial) {
                try {
                    await reaction.fetch()
                } catch (error) {
                    logger.error(
                        'Something went wrong when fetching message reactions: ' +
                            error
                    )
                    return
                }
            }

            if (!config.guilds[message.guildId].reaction == reaction._emoji.name) return
            if (config.guilds[message.guildId].blacklist.includes(message.channel.id)) return

            const emojiRegex = /\p{Emoji}/u
            const _emoji = reaction._emoji.name
            let emoji

            if (emojiRegex.test(_emoji)) {
                // It's an emoji
                emoji = reaction._emoji.name
            } else {
                // Custom emoji
                emoji = reaction._emoji.id
            }

            if (!message.reactions.cache.get(emoji)) return

            if (
                message.reactions.cache.get(emoji).count >=
                config.guilds[message.guildId].reactionCount
            ) {
                pinMsg()
            }

            async function pinMsg() {
                // Prevent re-pinning already pinned messages
                let query = await connection.query(
                    `SELECT * FROM ${config.database.database}.${config.database.table} WHERE OriginalMsg=${message.id}`
                )
                if (!query == undefined || !query.length == 0) {
                    return
                }

                // Don't pin messages that are blacklisted
                if (config.guilds[message.guildId].blacklist.includes(message.channel.id)) return

                const webhookClient = new Discord.WebhookClient({
                    id: config.guilds[message.guildId].webhook.id,
                    token: config.guilds[message.guildId].webhook.token,
                })

                const embed = new Discord.EmbedBuilder()
                    .setColor(0xff0000)
                    .setAuthor({
                        name: 'Jump [#' + message.channel.name + ']',
                        url: message.url,
                    })

                let msg

                if (message.embeds.length != 0) {

                    if (message.attachments.first() != null) {
                        msg = await webhookClient.send({
                            content: message.content,
                            username: message.author.username,
                            avatarURL: message.author.displayAvatarURL(),
                            files: [message.attachments.first()],
                            embeds: [message.embeds[0], embed],
                        })
                    } else {
                        msg = await webhookClient.send({
                            content: message.content,
                            username: message.author.username,
                            avatarURL: message.author.displayAvatarURL(),
                            embeds: [message.embeds[0], embed],
                        })
                    }

                    await connection.query(
                        `INSERT INTO ${config.database.database}.${config.database.table}  (OriginalMsg, WebhookMessageID) VALUES (${message.id}, ${msg.id});`
                    )

                    return
                }

                if (message.attachments.first() != null) {
                    msg = await webhookClient.send({
                        content: message.content,
                        username: message.author.username,
                        avatarURL: message.author.displayAvatarURL(),
                        files: [message.attachments.first()],
                        embeds: [embed],
                    })
                } else {
                    msg = await webhookClient.send({
                        content: message.content,
                        username: message.author.username,
                        avatarURL: message.author.displayAvatarURL(),
                        embeds: [embed],
                    })

                    await connection.query(
                        `INSERT INTO ${config.database.database}.${config.database.table}  (OriginalMsg, WebhookMessageID) VALUES (${message.id}, ${msg.id});`
                    )

                    return
                }
            }
        })
    }

    onEnable() {}

    onDisable() {}
}
module.exports = PinnerinoModule
