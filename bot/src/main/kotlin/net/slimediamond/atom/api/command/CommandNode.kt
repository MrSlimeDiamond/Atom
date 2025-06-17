package net.slimediamond.atom.api.command

import net.slimediamond.atom.api.command.exceptions.ArgumentParseException
import net.slimediamond.atom.api.command.exceptions.CommandException
import net.slimediamond.atom.api.command.parameter.Parameter
import net.slimediamond.atom.api.command.platforms.CommandPlatform
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.messaging.Audience
import org.apache.logging.log4j.LogManager
import java.util.*

abstract class CommandNode(val description: String, val aliases: List<String>) : Command {

    constructor(description: String, vararg aliases: String) : this(description, aliases.toList())

    private val logger = LogManager.getLogger("command node: ${aliases.first()}")
    private val _children: MutableList<CommandNode> = LinkedList()
    val children: List<CommandNode> get() = _children
    val platforms: MutableList<CommandPlatform> = LinkedList()
    val parameters: MutableList<Parameter> = LinkedList()
    @Volatile
    var permission: String? = null
    @Volatile
    var parent: CommandNode? = null

    init {
        if (this !is HelpCommandNode) {
            addChild(HelpCommandNode())
        }
    }

    open val usage: String
        get() {
            return buildString {
                append(aliases.first())
                parameters.forEach { parameter ->
                    append(if (parameter.optional) " [" else " <")
                    append(parameter.key)
                    if (parameter.greedy) {
                        append("...")
                    }
                    append(if (parameter.optional) "]" else ">")
                }
            }
        }

    companion object {
        fun tokenizeInput(input: String): List<String> {
            val regex = Regex("""("([^"]*)"|\S+)""")
            return regex.findAll(input).map {
                val match = it.groupValues[2]
                match.ifEmpty { it.value.trim('"') }
            }.toList()
        }
    }

    @Throws(CommandException::class)
    abstract suspend fun execute(context: CommandNodeContext): CommandResult

    override suspend fun execute(sender: CommandSender, input: String, platform: CommandPlatform, audience: Audience, cause: Cause): CommandResult {
        var actualInput = input
        var command = this

        while (command._children.isNotEmpty()) {
            val maybe = actualInput.split(" ")[0]
            val cmd = command._children.stream().filter { cmd -> cmd.aliases.contains(maybe) }.findFirst()
            if (cmd.isPresent) {
                // this is silly
                actualInput = actualInput.split(" ").drop(1).joinToString(" ")
                command = cmd.get()
            } else {
                break
            }
        }

        if (command.permission != null && !sender.hasPermission(command.permission!!)) {
            logger.warn("{} tried to use command '{}' without permission '{}'", sender.name, command.aliases.first(), command.permission)
            return CommandResult.empty
        }


        val parsed = tokenizeInput(actualInput)
        var index = 0

        val parameterKeyMap = HashMap<String, String>()

        if (command.parameters.isNotEmpty()) {
            command.parameters.forEachIndexed { i, parameter ->
                if (parameter.greedy) {
                    val remaining = actualInput.split("\\s+".toRegex(), limit = i + 1).getOrNull(i) ?: ""
                    parameterKeyMap[parameter.key] = remaining
                    return@forEachIndexed
                }

                if (index >= parsed.size) {
                    if (!parameter.optional) {
                        return CommandResult.error(platform.renderNotEnoughArguments(command, i))
                    }
                } else {
                    parameterKeyMap[parameter.key] = parsed[index]
                    index++
                }
            }

            val totalInputArgs = actualInput.trim().split("\\s+".toRegex())
            val nonGreedyParams = command.parameters.filter { !it.greedy }
            val requiredCount = nonGreedyParams.count { !it.optional }
            val maxCount = nonGreedyParams.size

            if (totalInputArgs.size < requiredCount || totalInputArgs.size > maxCount) {
                return CommandResult.error(platform.renderTooManyArguments(command, index, actualInput))
            }
        } else if (actualInput.isNotEmpty()) {
            return CommandResult.error(platform.renderTooManyArguments(command, index, actualInput))
        }

        return try {
            val c = cause.toMutable()
            c.push(sender)
            c.push(command)
            c.push(this)
            c.push(platform)
            val context = platform.createContext(command, c, sender, actualInput, audience, parameterKeyMap)
            command.execute(context)
        } catch (e: ArgumentParseException) {
            logger.error(e)
            CommandResult.error(platform.renderArgumentParseException(e))
        } catch (e: CommandException) {
            logger.error(e)
            CommandResult.error(e.msg)
        } catch (e: Throwable) {
            // this doesn't always trigger for some reason
            logger.error(e)
            CommandResult.error(e.javaClass.name + ": " + (e.message ?: "An error occurred when executing this command"))
        }
    }

    protected fun addChild(child: CommandNode) {
        child.parent = this
        _children.add(child)
        // println("Add child: ${child.aliases.first()} parent: ${child.parent?.aliases?.first()}")
    }

    protected fun addChild(config: Builder.() -> Unit) {
        addChild(Builder().apply(config).build())
    }

    class Builder {
        var aliases: List<String>? = null
        var description: String? = null
        private var executor: (suspend (CommandNodeContext) -> CommandResult)? = null

        fun executor(executor: suspend (CommandNodeContext) -> CommandResult) {
            this.executor = executor
        }

        fun build(): CommandNode {
            if (aliases == null) {
                throw IllegalStateException("Command aliases must not be null")
            }
            if (description == null) {
                throw IllegalStateException("Command description must not be null")
            }
            if (executor == null) {
                throw IllegalStateException("Command executor must not be null")
            }
            return object : CommandNode(description!!, aliases!!) {
                override suspend fun execute(context: CommandNodeContext): CommandResult {
                    return executor!!.invoke(context)
                }
            }
        }
    }

}