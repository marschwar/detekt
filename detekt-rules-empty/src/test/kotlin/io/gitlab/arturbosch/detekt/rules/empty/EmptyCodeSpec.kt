package io.gitlab.arturbosch.detekt.rules.empty

import io.github.detekt.test.utils.compileForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import java.util.regex.PatternSyntaxException


class EmptyCodeSpec {

    @Test
    fun `findsEmptyFinally`() {
        test { EmptyFinallyBlock(Config.empty) }
    }

    @Test
    fun `findsEmptyIf`() {
        test { EmptyIfBlock(Config.empty) }
    }

    @Test
    fun `findsEmptyElse`() {
        test { EmptyElseBlock(Config.empty) }
    }

    @Test
    fun `findsEmptyFor`() {
        test { EmptyForBlock(Config.empty) }
    }

    @Test
    fun `findsEmptyWhile`() {
        test { EmptyWhileBlock(Config.empty) }
    }

    @Test
    fun `findsEmptyDoWhile`() {
        test { EmptyDoWhileBlock(Config.empty) }
    }

    @Test
    fun `findsEmptyFun`() {
        test { EmptyFunctionBlock(Config.empty) }
    }

    @Test
    fun `findsEmptyClass`() {
        test { EmptyClassBlock(Config.empty) }
    }

    @Test
    fun `findsEmptyTry`() {
        test { EmptyTryBlock(Config.empty) }
    }

    @Test
    fun `findsEmptyWhen`() {
        test { EmptyWhenBlock(Config.empty) }
    }

    @Test
    fun `findsEmptyInit`() {
        test { EmptyInitBlock(Config.empty) }
    }

    @Test
    fun `findsOneEmptySecondaryConstructor`() {
        test { EmptySecondaryConstructor(Config.empty) }
    }
}

private fun test(block: () -> Rule) {
    val rule = block()
    rule.lint(compileForTest(resourceAsPath("Empty.kt")))
    assertThat(rule.findings).hasSize(1)
}
