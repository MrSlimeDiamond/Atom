const Discord = require('discord.js')
const modules = require('../modules')
const moduleHandler = require('../index').moduleHandler

module.exports = {
    data: new Discord.SlashCommandBuilder()
        .setName('moderation')
        .setDescription('Commands for the moderation module')
        .addSubcommand(subcommand =>
            subcommand
                .setName('purge')
                .setDescription(
                    'Remove a certian amount of messages from a channel'
                )
                .addIntegerOption(option =>
                    option
                        .setName('amount')
                        .setDescription('The amount of messages to remove')
                        .setRequired(true)
                )
        )
        .addSubcommand(subcommand => 
            subcommand
            .setName('ban')
            .setDescription('Ban a member from the server')
            .addUserOption(option =>
                option
                    .setName('user')
                    .setDescription('User to ban')
                    .setRequired(true)
                )
            .addStringOption(option => 
                option
                .setName('reason')
                .setDescription("The reason for banning the user")
                .setRequired(false)
                )
            )
        .setDefaultMemberPermissions(
            Discord.PermissionFlagsBits.ManageMessages
        ),
    async execute(interaction) {
        guildId = await interaction.guildId
        const enabledGuilds = await moduleHandler.getEnabledGuilds('moderation')

        if (!enabledGuilds.includes(guildId)) {
            const embed = new Discord.EmbedBuilder()
                .setColor(0xff0000)
                .setTitle('Module is not enabled')
                .setDescription(
                    'This module is not enabled for this server. Please ask a bot admin if you want it enabled.'
                )

            interaction.reply({ embeds: [embed] })
            return
        }

        // /moderation purge
        if (interaction.options.getSubcommand() == 'purge') {
            interaction.deferReply({ ephemeral: true })

            const amount = interaction.options.getInteger('amount')

            await interaction.channel.messages
                .fetch({ limit: amount })
                .then(async messages => {
                    await interaction.channel.bulkDelete(messages)
                })

            const embed = new Discord.EmbedBuilder()
                .setColor(0x4feb34)
                .setTitle('Moderation / Purge')
                .setDescription(
                    'Deleted ' +
                        amount +
                        ' messages from <#' +
                        interaction.channel.id +
                        '>'
                )

            await interaction.editReply({ embeds: [embed], ephemeral: true })
        }

        // /moderation ban
        if (interaction.options.getSubcommand() == 'ban') {
            const user = interaction.options.getUser('user')
            const member = await interaction.guild.members.cache.get(user.id)
            const reason_ = interaction.options.getString('reason')
            let reason

            if (!reason_) {
                reason = 'No reason given'
            } else {
                reason = reason_
            }

            const embed = new Discord.EmbedBuilder()
            .setColor(0xff0000)
            .setAuthor({ name: "Banned from " + interaction.guild.name })
            .setDescription(reason)
            .setTimestamp()

            const logEmbed = new Discord.EmbedBuilder()
            .setColor(0x4feb34)
            .setAuthor({ name: interaction.member.user.tag, iconURL: interaction.member.user.avatarURL() })
            .setTitle(member.user.tag + " was banned")
            .setDescription(reason)
            .setTimestamp()

            interaction.reply({ embeds: [logEmbed] })

            member.createDM()
            await member.send({ embeds: [embed] })
            .catch(error => {
                const embed = new Discord.EmbedBuilder()
                .setColor(0xff0000)
                .setAuthor({ name: "Member not accepting DMs" })
                .setDescription("This member is not accepting DMs")

                interaction.followUp({ embeds: [embed] })
            })
            
            const enabledGuilds = await moduleHandler.getEnabledGuilds('logger')
            if (!moduleHandler.moduleExists('logger')) {
                await member.ban({ reason: reason + " banned by " + interaction.member.user.tag})
                return
            }
            if (!enabledGuilds || !enabledGuilds.includes(interaction.guildId)) {
                await member.ban({ reason: reason + " banned by " + interaction.member.user.tag})
                return
            }

            const client = require('../index').client

            const loggerModule = await client.modules.find(mod => mod.name == 'logger')
            const loggerConfig = require(loggerModule.configLocation)

            const channel = client.channels.cache.get(
                loggerConfig[interaction.guild.id].logChannel
            )

            channel.send({ embeds: [logEmbed] })

            // Banning the user should be the last thing we do so that we can DM them first
            await member.ban({ reason: reason + " banned by " + interaction.member.user.tag})
        }
    },
}
