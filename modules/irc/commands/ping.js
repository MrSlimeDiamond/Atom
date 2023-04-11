const IRCCommand = require('../irccommand')

class PingCommand extends IRCCommand {
    constructor(irc) {
        super(irc)

        this.name = 'ping'
        this.aliases = ['p']
        this.description = 'simple command'
    }

    onCommand(client, from, to, message) {
        this.sendIRCMessage(to, 'Pong!', this.isHidden(message, from))
    }
}

module.exports = PingCommand
