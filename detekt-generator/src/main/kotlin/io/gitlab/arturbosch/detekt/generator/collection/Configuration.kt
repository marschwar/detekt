package io.gitlab.arturbosch.detekt.generator.collection

internal data class Configuration(
    val name: String,
    val description: String,
    val defaultValue: DefaultValue,
    val defaultAndroidValue: DefaultValue?,
    val deprecated: String?
) {
    fun isDeprecated() = deprecated != null
}

internal enum class ConfigParamDataType {
    String, Int, Boolean, StringList, ValuesWithReason
}
