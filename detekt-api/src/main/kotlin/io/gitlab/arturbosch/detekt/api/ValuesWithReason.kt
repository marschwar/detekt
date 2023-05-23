package io.gitlab.arturbosch.detekt.api

/**
 * This factory method can be used by rule authors to specify one or many configuration values along with an
 * explanation for each value. For example:
 *
 * ```kotlin
 *  @Configuration("imports which should not be used")
 *  private val imports: ValuesWithReason by config(
 *      valuesWithReason("org.junit.Test" to "Do not use Junit4. Use org.junit.jupiter.api.Test instead.")
 *  )
 * ```
 *
 * Note that the [config] property delegate only supports the factory methods when defining [ValuesWithReason].
 */
fun valuesWithReason(vararg values: Pair<String, String?>): ValuesWithReason {
    return ValuesWithReason(values.map { ValueWithReason(it.first, it.second) })
}

private const val FORMAT_DELIMITER = ':'

/**
 * [ValuesWithReason] is essentially the same as [List] of [ValueWithReason]. Due to type erasure we cannot use the
 * list directly. Instances of this type should always be created using the [valuesWithReason] factory method.
 */
data class ValuesWithReason internal constructor(private val values: List<ValueWithReason>) :
    Iterable<ValueWithReason> by values

/**
 * A ValueWithReason represents a single configuration value that may have an explanation as to why it is used.
 * @property value the actual value that is configured. The actual value can be prefixed with 'regex:', 'glob:'
 * or 'literal:' to indicate how the value should be interpreted. If there is no prefix, it will be interpreted as a literal value.
 * @property reason an optional explanation for the configured value
 */
data class ValueWithReason(val value: String, val reason: String? = null) {
    private val regex: Regex by lazy {
        when (value.substringBefore(FORMAT_DELIMITER)) {
            "regex" -> value.substringAfter(FORMAT_DELIMITER).toRegex()
            "glob" -> value.substringAfter(FORMAT_DELIMITER).globToRegex()
            "literal" -> value.substringAfter(FORMAT_DELIMITER).literalToRegex()
            else -> value.literalToRegex()
        }
    }

    fun matches(other: String): Boolean {
        return regex.matchEntire(other) != null
    }
}

private fun String.globToRegex(): Regex {
    return this.simplePatternToRegex()
}

private fun String.literalToRegex(): Regex {
    return Regex.escape(this).toRegex()
}
