package io.gitlab.arturbosch.detekt.rules.empty

import io.github.detekt.test.utils.compileForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.test.lint
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.psi.KtFile
import org.junit.jupiter.api.Test

class EmptyCodeRulesSpec {

    private val file = compileForTest(resourceAsPath("Empty.kt"))

    private val subject = EmptyCodeProvider()

    private fun lint(file: KtFile, config: Config = Config.empty): List<Finding> =
        subject.instance(config).rules.flatMap { it.lint(file) }

    private fun lint(@Language("kotlin") code: String): List<Finding> =
        subject.instance(Config.empty).rules.flatMap { it.lint(code) }

    @Test
    fun `should report one finding per rule`() {
        val findings = lint(file)
        // -1 because the empty kt file rule doesn't get triggered in the 'Empty' test file
        val rulesSize = EmptyBlocks().rules.size - 1
        assertThat(findings).hasSize(rulesSize)
    }

    @Test
    fun `should not report any as all empty block rules are deactivated`() {
        val config = yamlConfig("deactivated-empty-blocks.yml")

        val findings = lint(file, config)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `reports an empty kt file`() {
        assertThat(lint("")).hasSize(1)
    }

    @Test
    fun `reports no duplicated findings - issue #1605`() {
        val code = """
            class EmptyBlocks {
                class EmptyClass {}
                fun exceptionHandling() {
                    try {
                        println()
                    } finally {
                    }
                }
            }
        """

        val findings = lint(code)

        assertThat(findings).hasSize(2)
    }
}
