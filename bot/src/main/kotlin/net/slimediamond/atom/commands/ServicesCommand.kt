package net.slimediamond.atom.commands

import dev.kord.rest.builder.message.EmbedBuilder
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.api.command.RootOnlyCommandNode
import net.slimediamond.atom.api.command.platforms.discord.DiscordCommandNodeContext
import net.slimediamond.atom.api.event.Listener
import net.slimediamond.atom.api.service.events.ServiceStopEvent
import net.slimediamond.atom.commands.parameters.Parameters
import net.slimediamond.atom.utils.Embeds

class ServicesCommand : RootOnlyCommandNode("services") {

    init {
        addChild(ListCommand())
        addChild(InfoCommand())
        addChild(RestartCommand())
    }

    class ListCommand : CommandNode("list") {

        @OptIn(DelicateCoroutinesApi::class)
        override fun execute(context: CommandNodeContext): CommandResult {
            val services = Atom.instance.serviceManager.services
            if (context is DiscordCommandNodeContext) {
                val embed = EmbedBuilder().apply {
                    color = Embeds.THEME_COLOR
                    title = "Services"
                    description = services.map { service -> "* ${service.value.name}" }.joinToString("\n")
                    footer { text = "${services.size} total" }
                }
                GlobalScope.launch {
                    context.sendEmbeds(embed)
                }
            } else {
                context.sendMessage("Services (${services.size}): ${services.map { it.value.name }.joinToString(", ")}")
            }
            return CommandResult.success
        }

    }

    class InfoCommand : CommandNode("info") {

        init {
            parameters.add(Parameters.SERVICE)
        }

        override fun execute(context: CommandNodeContext): CommandResult {
            // context.sendMessage(context.requireOne(Parameters.SERVICE).name)
            TODO("Not yet implemented")
        }

    }

    class RestartCommand : CommandNode("restart") {

        init {
            parameters.add(Parameters.SERVICE)
            permission = "atom.command.services.restart"
        }

        override fun execute(context: CommandNodeContext): CommandResult {
            val service = context.requireOne(Parameters.SERVICE)

//            if (!service.instance.javaClass.methods
//                .filter { method -> method.isAnnotationPresent(Listener::class.java) }
//                .filter { method -> method.parameterCount == 1 }
//                .any { method -> method.parameters.first().javaClass.isAssignableFrom(ServiceStopEvent::class.java) }
//                ) {
//                context.sendMessage("Service ${service.name} does not have stopping logic, so it probably can't be restarted")
//                return CommandResult.success
//            }

            context.replySuccess("Restarting service ${service.name}")
            service.restart()
            return CommandResult.success
        }

    }

}