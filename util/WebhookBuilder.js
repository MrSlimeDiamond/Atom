const Discord = require('discord.js')
class WebhookBuilder {
    constructor(id, token) {
        this.webhookClient = new Discord.WebhookClient({ id: id, token: token })

        this.username = null
        this.avatar = null
        this.message = null
        this.embeds = []
        this.files = []
    }

    /**
     * Sets the username of the webhook
     * @param username - the username to use
     */
    setUsername(username) {
        this.username = username
        return this
    }

    /**
     * Sets the avatar for the webhook
     * @param avatar - URL of the avatar to use
     */
    setAvatar(avatar) {
        this.avatar = avatar
        return this
    }

    /**
     * Set the message content
     * @param message - Message to use
     */
    setMessage(message) {
        this.message = message
        return this
    }

    /**
     * Adds and embed to send
     * @param embed - Embed to send
     */
    addEmbed(embed) {
        this.embeds.push(embed)
        return this
    }

    /**
     * Add a file to send
     * @param file - File to send
     */
    addFile(file) {
        this.files.push(file)
        return this
    }

    /**
     * Send the webhook
     * @returns WebhookClient
     */
    async send() {
        return this.webhookClient.send({
            username: this.username,
            avatarURL: this.avatar,
            content: this.message,
            files: this.files,
            embeds: this.embeds,
        })
    }
}

module.exports = WebhookBuilder
