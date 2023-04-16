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
            //console.log(config.guilds[message.guildId].reactions)
            //console.log(reaction._emoji.id, reaction._emoji.name)
            if (
                !config.guilds[message.guildId].reactions.includes(
                    reaction._emoji.name
                )
            )
                return
            //console.log(reaction._emoji.name)
            //console.log(message.reactions.cache)

            for (const emoji of config.guilds[message.guildId].reactions) {
                if (message.reactions.cache.get(emoji) == null) {
                    return
                }
                if (
                    message.reactions.cache.get(emoji).count >=
                    config.guilds[message.guildId].reactionCount
                ) {
                    pinMsg()
                    break
                }
            }

            async function pinMsg() {
                // Prevent re-pinning already pinned messages
                let query = await connection.query(
                    `SELECT * FROM ${config.database.database}.${config.database.table} WHERE OriginalMsg=${message.id}`
                )
                if (!query == undefined || !query.length == 0) {
                    return
                }

                // Don't pin messages in the pinnerino channel
                if (
                    message.channel.id ==
                    config.guilds[message.guildId].channelID
                )
                    return

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
                    // Embed, prob from bot

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
