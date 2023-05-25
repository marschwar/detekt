package io.gitlab.arturbosch.detekt.generator.collection

internal data class RuleSetPage(
    val ruleSet: RuleSetProvider,
    val rules: List<Rule>
)
