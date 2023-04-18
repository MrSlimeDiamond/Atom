const { SlashCommandBuilder, EmbedBuilder } = require('discord.js')
const P2Data = require("../util/P2Data")
const p2data = new P2Data()

module.exports = {
    data: new SlashCommandBuilder()
        .setName('portal2')
        .setDescription('Portal 2 commands')
        .addSubcommand(subcommand =>
            subcommand 
                .setName('cm')
                .setDescription('Show CM stats for a user')
                .addStringOption(option =>
                    option
                        .setName('username')
                        .setDescription('The user to get statistics for')
                        .setRequired(true)
                    )
            ),
    async execute(interaction) {
        if (interaction.options.getSubcommand() == 'cm') {
            interaction.deferReply()
            const axios = require('axios')
            let username = interaction.options.getString('username')

            const request = await axios.get("https://board.portal2.sr/profile/" + username + "/json")
            const data = request.data

            if (!data.profileNumber) {
                const embed = new EmbedBuilder()
                .setColor(0xff0000)
                .setTitle("User does not exist")
                .setDescription("User " + username + " does not exist on board.portal.sr")

                interaction.editReply({ embeds: [embed] })
                return
            } else {
                // user exists

                // Util

                function mapFromSteamID(id) {
                    let mapName = 'Unknown'
                    if (id == 'several chambers') return 'Several Chambers'
                    for (let i = 0; i < p2data.maps.length; i++) {
                        if (p2data.maps[i].chamberID == id) {
                            mapName = p2data.maps[i].splitname
                            break
                        }
                    }
                    return mapName
                }
                
                let displayName = data.userData.displayName

                let WRsSP = data.times.SP.chambers.numWRs
                let WRsCOOP = data.times.COOP.chambers.numWRs
                let WRs = WRsSP + WRsCOOP

                const embed = new EmbedBuilder()
                .setColor(0x4feb34)
                .setAuthor({ name: displayName, iconURL: data.userData.avatar })
                .setTitle(displayName + '\'s Portal 2 CM Stats')
                .addFields(
                    { name: 'Singleplayer', value: `Points: ${data.points.SP.score}\nRank: ${data.points.SP.playerRank}`, inline: true },
                    { name: 'Cooperative', value: `Points: ${data.points.COOP.score}\nRank: ${data.points.COOP.playerRank}`, inline: true },
                    { name: 'Overall', value: `Points: ${data.points.global.score}\nRank: ${data.points.global.playerRank}`, inline: true },

                    { name: 'Best Rank', value: mapFromSteamID(data.times.bestRank.map) + ' - ' + data.times.bestRank.scoreData.playerRank, inline: true },
                    { name: 'Worst Rank', value: mapFromSteamID(data.times.worstRank.map) + ' - ' + data.times.worstRank.scoreData.playerRank, inline: true },
                    { name: 'World Records', value: `Singleplayer: ${WRsSP}\nCooperative: ${WRsCOOP}\nOverall: ${WRs}`, inline: true}
                )
                .setThumbnail(data.userData.avatar)

                interaction.editReply({ embeds: [embed] })
            }
        }
    },
}
