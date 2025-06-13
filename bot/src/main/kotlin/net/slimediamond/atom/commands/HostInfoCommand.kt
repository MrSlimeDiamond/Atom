package net.slimediamond.atom.commands

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.api.command.platforms.discord.DiscordCommandNodeContext
import net.slimediamond.atom.api.discord.embed.description
import net.slimediamond.atom.api.messaging.RichText
import net.slimediamond.atom.utils.Embeds
import org.apache.commons.lang3.time.DurationFormatUtils
import oshi.SystemInfo
import java.net.InetAddress
import java.time.Duration

class HostInfoCommand : CommandNode("Check information about the host machine", "hostinfo") {

    override suspend fun execute(context: CommandNodeContext): CommandResult {
        val systemInfo = SystemInfo()
        val hostname = withContext(Dispatchers.IO) {
            InetAddress.getLocalHost()
        }.hostName
        val uptime = Duration.ofSeconds(systemInfo.operatingSystem.systemUptime)
        val uptimeDisplay = DurationFormatUtils.formatDurationWords(uptime.toMillis(), true, true)
        val osFamily = systemInfo.operatingSystem.family
        val model = systemInfo.hardware.computerSystem.model

        val message = listOf(
            RichText.of()
                .append(RichText.of("Hostname").bold())
                .append(RichText.of(": $hostname")),
            RichText.of()
                .append(RichText.of("Uptime").bold())
                .append(RichText.of(": $uptimeDisplay")),
            RichText.of()
                .append(RichText.of("OS Family").bold())
                .append(RichText.of(": $osFamily")),
            RichText.of()
                .append(RichText.of("System Model").bold())
                .append(RichText.of(": $model")),
        )

        if (context is DiscordCommandNodeContext) {
            // embeds!
            context.sendEmbed {
                color = Embeds.THEME_COLOR
                title = "Host Info"
                description(RichText.join(RichText.newline(), message))
            }
        } else {
            context.sendMessage(RichText.join(RichText.of(". "), message))
        }
        return CommandResult.success
    }

}