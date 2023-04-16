/*
 * This won't have anything inside it really, it'll just
 * be there for the commands. Maybe more things in the
 * future.
 */
class ModerationModule {
    constructor(client) {
        this.client = client

        this.name = 'moderation'
        this.description =
            'Commands for moderating servers i.e purge, bans, warns'
        this.enabledGuilds = []
    }

    onEnable() {}

    onDisable() {}

    onRegister() {}
}

module.exports = ModerationModule
