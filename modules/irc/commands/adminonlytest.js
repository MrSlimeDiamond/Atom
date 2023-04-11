const IRCCommand = require('../irccommand')

class AdminOnlyCommand extends IRCCommand {
    constructor(irc) {
        super(irc)

        this.name = 'admin_only'
        this.aliases = []
        this.description = 'Admin only command!!'
        this.adminonly = true
    }

    onCommand(client, from, to, message) {
        this.sendIRCMessage(to, 'yeeah', this.isHidden(from, message))
    }
}

module.exports = AdminOnlyCommand
