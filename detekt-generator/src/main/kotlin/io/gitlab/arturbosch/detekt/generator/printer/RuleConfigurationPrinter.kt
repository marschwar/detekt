package io.gitlab.arturbosch.detekt.generator.printer

import io.github.detekt.utils.bold
import io.github.detekt.utils.cell
import io.github.detekt.utils.code
import io.github.detekt.utils.crossOut
import io.github.detekt.utils.h4
import io.github.detekt.utils.header
import io.github.detekt.utils.markdown
import io.github.detekt.utils.row
import io.github.detekt.utils.table
import io.gitlab.arturbosch.detekt.generator.collection.ConfigParamDataType
import io.gitlab.arturbosch.detekt.generator.collection.Configuration

internal object RuleConfigurationPrinter : DocumentationPrinter<List<Configuration>> {

    override fun print(item: List<Configuration>): String {
        if (item.isEmpty()) return ""
        return markdown {
            h4 { "Configuration options:" }
            table {
                header {
                    cell { "Option" }
                    cell { "Type" }
                    cell { "Default Value" }
                    cell { "Description" }
                }
                item.forEach {
                    row {
                        cell {
                            val name = code { it.name }
                            if (it.isDeprecated()) crossOut { name } else name
                        }
                        cell {
                            val dataType = it.defaultValue.dataType
                            if (dataType == ConfigParamDataType.ValuesWithReason) {
                                "[${dataType.name}](../introduction/configurations#values--with--reason)"
                            } else {
                                dataType.name
                            }
                        }
                        cell {
                            val defaultValues = it.defaultValue.printAsMarkdownCode()
                            val defaultAndroidValues = it.defaultAndroidValue?.printAsMarkdownCode()
                            val defaultString = if (defaultAndroidValues != null) {
                                "${code { defaultValues }} (android: ${code { defaultAndroidValues }})"
                            } else {
                                code { defaultValues }
                            }
                            defaultString
                        }
                        cell {
                            if (it.isDeprecated()) {
                                "${bold { "Deprecated" }}: ${it.deprecated}"
                            } else {
                                it.description
                            }
                        }
                    }
                }
            }
        }
    }
}
