const config = require('./config.json')
const fs = require('node:fs')
const path = require('node:path')
const {
    Client,
    GatewayIntentBits,
    EmbedBuilder,
    Partials,
} = require('discord.js')
const client = new Client({
    intents: [
        GatewayIntentBits.Guilds,
        GatewayIntentBits.GuildMessages,
        GatewayIntentBits.MessageContent,
        GatewayIntentBits.GuildMembers,
        GatewayIntentBits.GuildMessageReactions,
    ],
    partials: [Partials.Message, Partials.Reaction, Partials.Guilds],
})
const logger = require('./logger')
const log = new logger('Bot')

log.info('Starting bot')

const ModuleHandler = require('./modulehandler')
const moduleHandler = new ModuleHandler(client)
const CommandHandler = require('./commandhandler')
const commandHandler = new CommandHandler()

client.on('ready', async () => {
    module.exports.client = client
    await commandHandler.registerDefaultCommands(client)
    await moduleHandler.registerModules()
    await moduleHandler.enablePersistentModules()
    log.info(`Logged in as ${client.user.tag}!`)
})

client.on('interactionCreate', async interaction => {
    if (!interaction.isChatInputCommand()) return

    commandHandler.handle(interaction)
})

client.on('messageCreate', async message => {
    if (message.content.startsWith(config.bot.prefix + 'admin')) {
        if (!config.bot.admins.includes(message.author.id)) {
            const embed = new Discord.EmbedBuilder()
                .setColor(0xff0000)
                .setTitle('Permission Denied')
                .setDescription(
                    'You do not have permission to run this command'
                )
            message.reply({ embeds: [embed] })

            return
        }

        // Atom Config command thing
        args = message.content.split(' ')
        args.shift()

        if (args == null || args.length == 0) {
            await message.reply('My options are: stop, module')
            return
        }

        // !a-admin config
        // TODO
        /*
    if (args[0] == "config") {
      args.shift()
      if (args[0] == null) {
        message.reply("My options are: module, global")
        return
      }
      if (args[0] == "module") {
        if (args[1] == null) {
          message.reply("You need to provide a module to change the config for")
          return
        }
      }
      // !a-config global
      if (args[1] == "global") {
        args.shift()

        value = args[0]
        
      }
    }
    */

        // !a-admin stop
        if (args[0] == 'stop') {
            log.info('Bot admin requested stop')
            await message.reply('Stopping bot...')
            client.destroy()
            process.exit()
        }

        if (args[0] == 'module') {
            if (args[1] == null || !args[1]) {
                const embed = new EmbedBuilder()
                    .setColor(0xff0000)
                    .setTitle('Admin / Incorrect Usage')
                    .setDescription(
                        'Usage: !a-admin module <module name> <thing to do with it>'
                    )

                await message.reply({ embeds: [embed] })
                return
            } else {
                args.shift()
                const moduleName = args[0]

                if (moduleName == 'list') {

                  let modulesList
                  let modules = []

                  client.modules.forEach(module => {
                    modules.push(module.name)
                  })

                  if (modules.length == 0) {
                    modulesList = "No modules registered"
                  } else {
                    modulesList = modules.join(", ")
                  }

                  const embed = new EmbedBuilder()
                  .setColor(0x4feb34)
                  .setTitle('Admin / Module List')
                  .setDescription(modulesList)

                  await message.reply({ embeds: [embed] })
                  return
                }

                if (moduleHandler.moduleExists(moduleName)) {
                    if (args[1] == null || !args[1]) {
                        const embed = new EmbedBuilder()
                            .setColor(0xff0000)
                            .setTitle('Admin / Incorrect Usage')
                            .setDescription(
                                'Available options: enable, disable, config'
                            )

                        await message.reply({ embeds: [embed] })
                        return
                    }

                    if (args[1] == 'enable') {
                        moduleHandler.enableModule(message.guildId, moduleName)
                        const embed = new EmbedBuilder()
                            .setColor(0x4feb34)
                            .setTitle('Admin / Module Enabled')
                            .setDescription(
                                'Module has been successfully enabled.'
                            )

                        await message.reply({ embeds: [embed] })
                        return
                    } else if (args[1] == 'disable') {
                        moduleHandler.disableModule(message.guildId, moduleName)
                        const embed = new EmbedBuilder()
                            .setColor(0x4feb34)
                            .setTitle('Admin / Module Disabled')
                            .setDescription(
                                'Module has been successfully disabled.'
                            )

                        await message.reply({ embeds: [embed] })
                        return
                    } else if (args[1] == 'config') {
                        // TODO
                    } else if (args[1] == 'info') {
                        const module = client.modules.find(
                            module => module.name == moduleName
                        )

                        let enabledGuilds
                        if (module.enabledGuilds.length == 0) {
                            enabledGuilds = 'None'
                        } else {
                            enabledGuilds = module.enabledGuilds.join(', ')
                        }

                        const embed = new EmbedBuilder()
                            .setColor(0x4feb34)
                            .setTitle('Name: ' + module.name)
                            .setDescription(module.description)
                            .addFields({
                                name: 'Enabled Guilds',
                                value: enabledGuilds,
                            })

                        message.reply({ embeds: [embed] })
                    }
                } else {
                    const embed = new EmbedBuilder()
                        .setColor(0xff0000)
                        .setTitle('Admin / Incorrect Usage')
                        .setDescription('That module does not exist.')

                    await message.reply({ embeds: [embed] })
                    return
                }
            }
        }
    }

    if (message.content.startsWith(config.bot.prefix + 'fetch')) {
        let msgs = 0
        await message.channel.messages
            .fetch({ cache: true })
            .then(async messages => {
                for (const message of messages) {
                    msgs++
                }
            })
        const embed = new EmbedBuilder()
            .setColor(0x4feb34)
            .setTitle('Cached ' + msgs + ' messages')
            .setDescription(
                msgs +
                    ' messages were successfully cached from <#' +
                    message.channel.id +
                    '>'
            )

        message.reply({ embeds: [embed] })
    }
})

client.login(config.bot.token)

module.exports.moduleHandler = moduleHandler
module.exports.__dirname = __dirname
