const irc = require('./irc/irc')

class IRCModule {
    constructor(client) {
        this.client = client

        this.name = 'irc'
        this.description = 'IRC Bot, includes basic commands for now'
        this.enabledGuilds = []
        this.scope = 'global'
    }

    async onRegister() {
        irc.onRegister()
    }

    async onEnable() {
        irc.onEnable()
    }

    async onDisable() {
        irc.onDisable()
    }
}
module.exports = IRCModule
