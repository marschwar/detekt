package io.github.detekt.utils

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.FileWriter
import java.net.URL

fun migrate(userConfigFile: URL, defaultConfigResource: URL, target: URL = userConfigFile) {
    val dumperOptions = DumperOptions().apply {
        defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        this.defaultScalarStyle = DumperOptions.ScalarStyle.SINGLE_QUOTED
    }
    val yaml = Yaml(dumperOptions)
    val userData = userConfigFile.openSafeStream().use {
        yaml.load<Map<String, Any>>(it)
    } ?: emptyMap()
    val defaultData = requireNotNull(defaultConfigResource.openSafeStream().use { yaml.load<Map<String, Any>>(it) })

    val configPart = userData.filter { knownTopLevelKeys.contains(it.key) }
    val rulesPart = defaultData
        .filterNot { knownTopLevelKeys.contains(it.key) }
        .map { it.key to mergeRuleSet(it.value, userData[it.key]) }
        .toMap()

    yaml.dump(configPart + rulesPart, FileWriter(target.file))
}

fun mergeRuleSet(defaultRuleSet: Any, userData: Any?): Any {
    val defaultRuleSetMap = defaultRuleSet as Map<String, Any>
    val userDataMap = userData as? Map<String, Any> ?: emptyMap()
    return defaultRuleSetMap.mapValues { userDataMap.getOrDefault(it.key, it.value) }
}

private val knownTopLevelKeys = setOf(
    "build", "config", "processors", "console-reports", "output-reports"
)
