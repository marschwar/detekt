package io.gitlab.arturbosch.detekt.rules.empty

import io.github.detekt.test.utils.compileForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.regex.PatternSyntaxException

private const val ALLOWED_EXCEPTION_NAME_REGEX = "allowedExceptionNameRegex"

internal class EmptyCatchBlockSpec {

    private val subject = EmptyCatchBlock()

    @Test
    fun `finds empty catch`() {
        val testFile = compileForTest(resourceAsPath("Empty.kt"))
        val findings = subject.lint(testFile)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `finds empty nested catch`() {
        val code = """
            fun f() {
                try {
                } catch (ignore: Exception) {
                    try {
                    } catch (e: Exception) {
                    }
                }
            }
        """
        val findings = subject.compileAndLint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report ignored or expected exception`() {
        val code = """
            fun f() {
                try {
                } catch (ignore: IllegalArgumentException) {
                } catch (expected: Exception) {
                }
            }
        """

        val findings = subject.compileAndLint(code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report empty catch with config`() {
        val code = """
            fun f() {
                try {
                } catch (foo: Exception) {
                }
            }
        """
        val config = TestConfig(mapOf(ALLOWED_EXCEPTION_NAME_REGEX to "foo"))

        val findings = EmptyCatchBlock(config).compileAndLint(code)

        assertThat(findings).isEmpty()
    }

    @Nested
    inner class `Allowed exception names` {
        private val code = """
            fun f() {
                try {
                } catch (foo: Exception) {
                }
            }
        """

        @Test
        fun `does not fail with invalid regex when disabled`() {
            val config = TestConfig(
                "active" to "false",
                ALLOWED_EXCEPTION_NAME_REGEX to "*foo"
            )

            val findings = EmptyCatchBlock(config).compileAndLint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `does fail with invalid regex`() {
            val config = TestConfig(
                "active" to "true",
                ALLOWED_EXCEPTION_NAME_REGEX to "*foo"
            )

            assertThatExceptionOfType(PatternSyntaxException::class.java)
                .isThrownBy { EmptyCatchBlock(config).compileAndLint(code) }
        }
    }
}
