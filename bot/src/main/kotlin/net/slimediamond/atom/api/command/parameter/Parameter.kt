package net.slimediamond.atom.api.command.parameter

import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.command.exceptions.ArgumentParseException
import net.slimediamond.atom.api.messaging.RichText
import net.slimediamond.atom.api.service.ServiceContainer

interface Parameter {

    /**
     * The "key" for the parameter.
     *
     * This is shown in the usage tip.
     */
    val key: String

    /**
     * The description of the parameter
     */
    val description: String

    /**
     * Whether this parameter is greedy.
     *
     * This means it will consume all the remaining input
     */
    val greedy: Boolean

    /**
     * Whether this is optional
     */
    val optional: Boolean

    companion object {
        inline fun <reified T : Any> builder(config: Value.Builder<T>.() -> Unit): Value.Builder<T> {
            return Value.Builder<T>().apply(config)
        }

        fun int(config: Value.Builder<Int>.() -> Unit): Value<Int> {
            return builder<Int> {
                parser = IntValueParser()
            }.apply(config).build()
        }

        fun string(config: Value.Builder<String>.() -> Unit): Value<String> {
            return builder<String> {
                parser { it }
            }.apply(config).build()
        }

        fun boolean(config: Value.Builder<Boolean>.() -> Unit): Value<Boolean> {
            return builder<Boolean> {
                parser = BooleanValueParser()
            }.apply(config).build()
        }

        fun service(config: Value.Builder<ServiceContainer>.() -> Unit): Value<ServiceContainer> {
            return builder<ServiceContainer> {
                parser {
                    Atom.bot.serviceManager.getByName(it)
                        ?: throw ArgumentParseException(it, 0, RichText.of("Could not find service '$it'"))
                }
            }.apply(config).build()
        }
    }

    interface Value<T> : Parameter {

        /**
         * The value parser
         */
        val parser: ValueParser<T>

        /**
         * Parse the value from an input
         */
        @Throws(ArgumentParseException::class)
        fun parse(input: String): T? {
            return parser.parse(input)
        }

        class Builder<T> {

            var key: String = ""
            var description: String = "(no description provided)"
            var greedy: Boolean = false
            var optional: Boolean = false
            lateinit var parser: ValueParser<T>

            fun parser(parser: (String) -> T) = apply { this.parser = LambdaValueParser(parser) }

            fun build(): Value<T> {
                return ParameterValueImpl(key, description, greedy, optional, parser)
            }

        }

    }

}

class LambdaValueParser<T>(val parser: (String) -> T) : ValueParser<T> {

    override fun parse(input: String): T {
        return parser.invoke(input)
    }

}

class ParameterValueImpl<T>(
    override val key: String,
    override val description: String,
    override val greedy: Boolean,
    override val optional: Boolean,
    override val parser: ValueParser<T>
) : Parameter, Parameter.Value<T>

inline fun <reified T : Any> parameter(config: Parameter.Value.Builder<T>.() -> Unit): Parameter.Value<T> {
    return Parameter.builder<T>(config).build()
}