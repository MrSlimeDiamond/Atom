package net.slimediamond.atom.api.command.parameter

import net.slimediamond.atom.api.command.exceptions.ArgumentParseException
import net.slimediamond.atom.api.messaging.RichText

interface Parameter {

    /**
     * The "key" for the parameter.
     *
     * This is shown in the usage tip.
     */
    val key: String

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
        fun <T> builder(clazz: Class<T>): Value.Builder<T> {
            return Value.Builder<T>()
        }

        fun int(): Value.Builder<Int> {
            return builder(Int::class.java).parser {
                try {
                    return@parser it.toInt()
                } catch (e: NumberFormatException) {
                    throw ArgumentParseException(it, 0, RichText.of("Provided input is not a number"))
                }
            }
        }

        fun string(): Value.Builder<String> {
            return builder(String::class.java).parser { it }
        }

        fun boolean(): Value.Builder<Boolean> {
            return builder(Boolean::class.java).parser { it.toBoolean() }
        }
    }

    interface Value<T> : Parameter {

        /**
         * Parse the value from an input
         */
        @Throws(ArgumentParseException::class)
        fun parse(input: String): T?

        class Builder<T> {

            private var key: String = ""
            private var greedy: Boolean = false
            private var optional: Boolean = false
            private lateinit var parser: (String) -> T

            fun key(key: String) = apply { this.key = key }
            fun greedy(greedy: Boolean) = apply { this.greedy = greedy }
            fun greedy() = apply { this.greedy = true }
            fun optional(optional: Boolean) = apply { this.optional = optional }
            fun optional() = apply { this.optional = true }
            fun parser(parser: (String) -> T) = apply { this.parser = parser }

            fun build(): Value<T> {
                return ParameterValueImpl(key, greedy, optional, parser)
            }

        }

    }

}

class ParameterValueImpl<T>(
    override val key: String,
    override val greedy: Boolean,
    override val optional: Boolean,
    private val parser: (String) -> T
) : Parameter, Parameter.Value<T> {
    override fun parse(input: String): T = parser(input)
}