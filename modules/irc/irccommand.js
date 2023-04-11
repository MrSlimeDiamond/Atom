const config = require('../../module_configs/irc.json')
const Logger = require('../../logger')
const logger = new Logger('irc')
const irc = require('./irc')
let client = irc.client
class IRCCommand {
    constructor(irc) {
        this.irc = irc

        this.prefix = config.irc.prefix
    }

    getArgs(message, from) {
        let relayed = from == 'McObot'
        let distel = from == 'MCO_Discord' || from == 'MCO_Telegram'
        let newDiscord = from == 'Silly'
        let args
        let msg
        if (distel) {
            // Discord/Telegram message (both have the same format)
            msg = message.split(' ').slice(1, 2).join(' ')
            if (this.isHidden(message, from)) {
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

        if (this.isHidden(message, from)) {
            args = message
                .slice(this.prefix.length + 1)
                .trim()
                .split(/ +/)
        } else if (relayed) {
            msg = message.split(' ').slice(2).join(' ')
            args = msg.slice(this.prefix.length).trim().split(/ +/)
        } else if (newDiscord) {
            msg = message.split('> ')[1]
            args = msg.slice(this.prefix.length).trim().split(/ +/)
            if (this.isHidden(message, from)) {
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

        args.shift()

        return args
    }

    sendIRCMessage(to, message, hidden) {
        if (hidden) {
            client.say(to, '# ' + message)
        } else {
            client.say(to, message)
        }
    }

    isHidden(message, from) {
        return (
            message.startsWith('#') ||
            (from == 'MCO_Discord' && message.split(' ')[1].startsWith('#')) ||
            (from == 'MCO_Telegram' && message.split(' ')[1].startsWith('#')) ||
            (from == 'Silly' && message.split(' ')[2].startsWith('#'))
        )
    }

    getUser(message, from) {
        let args = this.getArgs(message, from)
        if ((args.length == 1) & (from == 'MCO_Discord')) {
            return args[0]
        } else if (args.length == 0 && from == 'Silly') {
            return message
                .split(' ')[1]
                .replace('<', '')
                .replace('>', '')
                .replace('​', '')
        } else if (args.length == 0 && from == 'MCO_Discord') {
            return message.split('>')[0].replace('<', '')
        } else if (args.length == 0 && from != 'McObot') {
            return from
        } else if (args.length >= 1 && from != 'McObot') {
            return args[0]
        } else if (args.length == 0 && from == 'McObot') {
            return message.split(' ')[1].replace('<', '').replace('>', '')
        } else if (args.length >= 1 && from == 'McObot') {
            return args[0]
        } else if (args.length >= 1 && from == 'Silly') {
            return args[0]
        }
    }

    getCommandHandler() {
        return require('./events/message').handler
    }
}

module.exports = IRCCommand
