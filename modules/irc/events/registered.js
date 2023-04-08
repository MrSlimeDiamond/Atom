const config = require("../../../module_configs/irc.json")
const logger = require("../../../logger")
const log = new logger("IRC (Register Event)")

async function handleEvent(client) {
    try {
        client.say(
            'NickServ',
            'IDENTIFY ' + config.irc.nickserv_name + ' ' + config.irc.nickserv_password
        )
        await new Promise(r => setTimeout(r, 2000)) // Wait a bit, give NickServ some time
        log.info('Bot identified')
    } catch (error) {
        log.warn('IRC Bot could not authenticate with NickServ')
    }
    log.info('IRC bot is joining channels')
    for (const channel of config.irc.channels) {
        client.join(channel)
    }
    log.info('IRC bot ready!')
}

module.exports.handleEvent = handleEvent