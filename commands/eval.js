const {
    SlashCommandBuilder,
    EmbedBuilder,
    PermissionFlagsBits,
} = require('discord.js')
const config = require('../config.json')
module.exports = {
    data: new SlashCommandBuilder()
        .setName('eval')
        .setDescription('(Admin only) run JavaScript code on the bot')
        .setDefaultMemberPermissions(PermissionFlagsBits.Administrator)
        .addStringOption(option =>
            option
                .setName('code')
                .setDescription('The code to run')
                .setRequired(true)
        ),
    async execute(interaction) {
        if (config.bot.admins.includes(interaction.user.id)) {
            try {
                code = await interaction.options.getString('code').toString()
                evaled = eval(code)

                if (typeof evaled !== 'string')
                    evaled = require('util').inspect(evaled)
                const evalLog = new EmbedBuilder()
                    .setColor(0x41f474)
                    .setTitle('Eval / Success')
                    .addFields(
                        { name: 'Input', value: '```js\n' + code + '\n```' },
                        { name: 'Output', value: '```xl\n' + evaled + '\n```' }
                    )
                    .setTimestamp()
                await interaction.reply({ embeds: [evalLog] })
            } catch (err) {
                const evalLogF = new EmbedBuilder()
                    .setColor(0xf44141)
                    .setTitle('Eval / Fail')
                    .addFields(
                        { name: 'Input', value: '```js\n' + code + '\n```' },
                        { name: 'Output', value: '```xl\n' + evaled + '\n```' }
                    )
                    .setTimestamp()
                await interaction.reply({ embeds: [evalLogF] })
            }
        } else {
            let embed = new EmbedBuilder()
                .setColor(0xf44141)
                .setTitle('No permission')
                .setDescription(
                    'You do not have permission to use this command'
                )
            await interaction.reply({ embeds: [embed] })
        }
    },
}
