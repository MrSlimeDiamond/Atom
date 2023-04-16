const modules = require('./modules')
const logger = require('./logger')
const Discord = require('discord.js')
const fs = require('node:fs')
const path = require('node:path')

const log = new logger('Module Handler')

class ModuleHandler {
    constructor(client) {
        this.client = client
    }

    /**
     *
     * @param {*} client
     * @param {*} moduleName
     *
     * Assumes module exists
     *
     * @deprecated since 0.8
     */
    registerModule(client, moduleName) {
        for (let i = 0; i < modules.length; i++) {
            if (modules[i].name == moduleName) {
                const moduleFile = require(modules[i].moduleLocation)
                if (modules[i].scope == 'guild') {
                    // guild only module
                    moduleFile.module(client)
                } else {
                    // global module
                    moduleFile.onRegister()
                }

                log.info('Registered module: ' + moduleName)
                return true
            }
        }
        log.error(
            'Could not register module ' + moduleName + '! does it exist?'
        )
        return false
    }

    /**
     *
     * @param {string} module
     *
     * Checks if a module is registered
     */
    moduleExists(moduleName) {
        const module = this.client.modules.find(
            module => module.name == moduleName
        )

        if (!module || module == null || module == undefined) {
            return false
        } else {
            return true
        }
    }

    /**
     *
     * @param {*} guildid
     * @param {*} moduleName
     *
     * Assumes module exists and is registered and disabled
     */
    enableModule(guildid, moduleName) {
        if (!this.moduleExists(moduleName)) return false
        const module = this.client.modules.find(
            module => module.name == moduleName
        )
        try {
            module.onEnable()
        } catch (error) {
            log.error(error)
            return false
        }
        if (!module.scope == 'global' || !module.scope)
            module.enabledGuilds.push(guildid)
        return true
    }

    /**
     *
     * @param {*} guildid
     * @param {*} moduleName
     *
     * Assumes module exists and is enabled
     */
    disableModule(guildid, moduleName) {
        if (!this.moduleExists(moduleName)) return false

        const module = this.client.modules.find(
            module => module.name == moduleName
        )

        // Already disabled
        // TODO: return some error thingy
        if (!module.enabledGuilds.includes(guildid) && module.scope != 'global')
            return false

        try {
            module.onDisable()
        } catch (error) {
            log.error(error)
            return false
        }
        if (module.scope == 'global') return true
        const index = module.enabledGuilds.indexOf(guildid)
        module.enabledGuilds.splice(index, 1)

        return true
    }

    registerModuleCommands(guildid, moduleName) {
        let commands = []
        client.commands = new Collection()
        const commandsPath = path.join('./module_commands/' + moduleName)
        const commandFiles = fs
            .readdirSync(commandsPath)
            .filter(file => file.endsWith('.js'))

        for (const file of commandFiles) {
            const filePath = path.join(commandsPath, file)
            const command = require(filePath)
            // Set a new item in the Collection with the key as the command name and the value as the exported module
            if ('data' in command && 'execute' in command) {
                client.commands.set(command.data.name, command)
                commands.push(command.data.toJSON())
            } else {
                log.warn(
                    `The command at ${filePath} is missing a required "data" or "execute" property.`
                )
            }
        }

        const rest = new REST({ version: '10' }).setToken(config.bot.token)

        ;(async () => {
            try {
                log.info(
                    'Started refreshing application (/) commands for module ' +
                        moduleName
                )

                await rest.put(
                    Routes.applicationCommands('1078475641516740608', guildid),
                    { body: commands }
                )

                log.info(
                    'Successfully reloaded application (/) commands for module ' +
                        moduleName
                )
            } catch (error) {
                log.error(error)
            }
        })()
    }

    /**
     * Register all modules in the modules directory
     */
    async registerModules() {
        let modules = []
        this.client.modules = new Discord.Collection()
        const modulePath = path.join(__dirname, 'modules')
        const files = fs
            .readdirSync(modulePath)
            .filter(file => file.endsWith('.js'))

        for (const file of files) {
            const filePath = path.join(modulePath, file)
            const moduleFile = require(filePath)
            const module = new moduleFile(this.client)

            this.client.modules.set(module.name, module)
            modules.push(module.name)

            module.onRegister()
        }

        log.info(
            'Registered ' + modules.length + ' module(s): ' + modules.join(', ')
        )
    }

    async enablePersistentModules() {
        const persistentModules = require('./modules.json')
        // Super scuffed but ig it'll work
        // TODO: make not scuffed

        this.client.guilds.cache.forEach(guild => {
            if (!persistentModules[guild.id]) {
                return
            } else {
                for (let i = 0; i < persistentModules[guild.id].length; i++) {
                    if (!this.moduleExists(persistentModules[guild.id][i])) {
                        log.warn(
                            'Module ' +
                                persistentModules[guild.id][i] +
                                ' does not exist, but is in modules.json'
                        )
                    } else {
                        this.enableModule(
                            guild.id,
                            persistentModules[guild.id][i]
                        )
                    }
                }
            }
        })
    }

    /**
     *
     * @param {string} moduleName
     * @returns array of enabled guilds
     */
    async getEnabledGuilds(moduleName) {
        if (!this.moduleExists(moduleName)) {
            return false
        }

        const module = this.client.modules.find(
            module => module.name == moduleName
        )
        return module.enabledGuilds
    }
}

module.exports = ModuleHandler
