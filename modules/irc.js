const irc = require("./irc/irc")

class IRCModule {
    constructor(client) {
        this.client = client

        this.name = 'irc'
        this.description = 'IRC Bot, includes basic commands for now'
        this.enabledGuilds = []
        this.scope = 'global'
    }

    onRegister() {
        irc.onRegister()
    }

    onEnable() {
        irc.onEnable()
    }

    onDisable() {
        irc.onDisable()
    }
}
module.exports = IRCModule