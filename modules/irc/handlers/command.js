const message = require('../events/message')
const config = require("../../../module_configs/irc")
const Logger = require('../../../logger')
const logger = new Logger('IRC (Command Handler)')
const Discord = require("discord.js")
const fs = require("node:fs")

class IRCCommandHandler {
    constructor(irc, client) {
        this.irc = irc
        this.prefix = config.irc.prefix

        this.commands = new Discord.Collection()

        let commandFiles = fs
            .readdirSync('./modules/irc/commands')
            .filter(file => file.endsWith('.js'))
        for (const file of commandFiles) {
            const commd = new (require("../commands/" + file))(irc, client)
            this.commands.set(commd.name, commd)
        }


        module.exports.commands = this.commands
    }

    handle(ircclient, from, to, message) {
        try {
            if (
                message.startsWith('#' + this.prefix) ||
                message.startsWith(this.prefix) ||
                (message.split('> ')[1].startsWith(this.prefix) &&
                    from == 'MCO_Discord') ||
                (message.split('> ')[1].startsWith('#' + this.prefix) &&
                    from == 'MCO_Discord') ||
                (message.split('> ')[1].startsWith(this.prefix) &&
                    from == 'MCO_Telegram') ||
                (message.split('> ')[1].startsWith('#' + this.prefix) &&
                    from == 'MCO_Telegram') ||
                (message.split(' ')[2].startsWith(this.prefix) &&
                    from == 'McObot') ||
                (message.split(' ')[2].startsWith(this.prefix) &&
                    from == 'Silly') ||
                (message.split(' ')[2].startsWith('#' + this.prefix) &&
                    from == 'Silly')
            ) {
                let relayed = from == 'McObot'
                let distel = from == 'MCO_Discord' || from == 'MCO_Telegram'
                let newDiscord = from == 'Silly'
                let hidden =
                    message.startsWith('#') ||
                    (distel && message.split(' ')[1].startsWith('#')) ||
                    (newDiscord && message.split(' ')[2].startsWith('#'))

                let args
                let msg

                if (hidden) {
                    args = message
                        .slice(this.prefix.length + 1)
                        .trim()
                        .split(/ +/)
                } else if (distel) {
                    // Discord/Telegram message (both have the same format)
                    msg = message.split(' ').slice(1, 2).join(' ')
                    if (hidden) {
                        args = message
                            .split('> ')[1]
                            .slice(this.prefix.length + 1)
                            .trim()
                            .split(/ +/)
                    } else {
                        args = message
                            .split('> ')[1]
                            .slice(this.prefix.length)
                            .trim()
                            .split(/ +/)
                    }
                } else {
                    args = message.slice(this.prefix.length).trim().split(/ +/)
                }

                if (relayed) {
                    // msg = message.split(' ').slice(2, 3).join(' ')
                    msg = message.split('> ')[1]
                    args = msg.slice(this.prefix.length).trim().split(/ +/)
                }
                if (newDiscord) {
                    msg = message.split('> ')[1]
                    args = msg.slice(this.prefix.length).trim().split(/ +/)
                    if (hidden) {
                        args = message
                            .split('> ')[1]
                            .slice(this.prefix.length + 1)
                            .trim()
                            .split(/ +/)
                    } else {
                        args = message
                            .split('> ')[1]
                            .slice(this.prefix.length)
                            .trim()
                            .split(/ +/)
                    }
                }
                let commandName = args.shift().toLowerCase()

                let command =
                    this.commands.get(commandName) ||
                    this.commands.find(
                        cmd => cmd.aliases && cmd.aliases.includes(commandName)
                    )

                if (!command) {
                    return false
                }

                function getCommandSender() {
                    let a = message.split(' ')
                    let sender
                    if (a.length >= 3 && from == 'McObot') {
                        sender = a[1].replace('<', '').replace('>', '')
                    } else if (from == 'MCO_Discord') {
                        var b = message.split('>')
                        sender = b[0].replace('<', '')
                    } else if (a.length >= 3 && from == 'Silly') {
                        sender = a[1].replace('<', '').replace('>', '')
                    } else {
                        sender = from
                    }
                    return sender
                }

                logger.info(`[COMMAND]: ${getCommandSender()}: ${commandName}`)

                if (!config.admins.includes(getCommandSender()) && command.adminonly) {
                    logger.warn(`${getCommandSender()} tried using admin only command ${commandName}`)
                    ircclient.say(to, "You do not have permission to use this command!")
                    return
                }

                command.onCommand(ircclient, from, to, message)
                return true
            } else {
                logger.info("did not handle command")
                return false
            }
        } catch (error) {
            // really cursed workaround to console spam that shouldn't even happen in the first place
            if (error == "TypeError: Cannot read properties of undefined (reading 'startsWith')") return
            if (error == "TypeError: Cannot read property 'startsWith' of undefined") return
            console.error(error)
        }
    }
}


module.exports = IRCCommandHandler