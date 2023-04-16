const irc = require('irc')
const registeredEvent = require('./events/registered')
const messageEvent = require('./events/message')
const config = require('../../module_configs/irc.json')
let client // Will be initialized later
let isDisabled = true
const logger = require('../../logger')
const log = new logger('IRC')

async function onEnable() {
    log.info('Starting IRC bot')
    client = new irc.Client(config.irc.server, config.irc.nick, {
        realName: config.irc.realName,
        userName: config.irc.username,
    })

    module.exports.client = client

    isDisabled = false

    ircModule()
}

async function onDisable() {
    log.info("Disabling module")
    log.info("Disconnecting bot")
    client.disconnect('Module disabled')
    isDisabled = true
}

async function ircModule() {
    if (isDisabled) return

    client.addListener('registered', function () {
        registeredEvent.handleEvent(client)
    })

    client.addListener('message', function (from, to, message) {
        messageEvent.handleEvent(client, from, to, message)
    })
}

async function onRegister() {
    // does nothing atm lol
}

module.exports.onEnable = onEnable
module.exports.onDisable = onDisable
module.exports.onRegister = onRegister
module.exports.module = ircModule
