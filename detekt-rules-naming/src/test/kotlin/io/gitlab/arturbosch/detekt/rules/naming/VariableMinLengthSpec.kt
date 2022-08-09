package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class VariableMinLengthSpec {
    private val config = TestConfig(mapOf(VariableMinLength.MINIMUM_VARIABLE_NAME_LENGTH to "2"))
    private val subject = VariableMinLength(config)

    @Test
    fun `reports a very short variable name`() {
        val code = "private val a = 3"
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `does not report a variable with only a single underscore`() {
        val code = """
                class C {
                    val prop: (Int) -> Unit = { _ -> Unit }
            }
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }
}
