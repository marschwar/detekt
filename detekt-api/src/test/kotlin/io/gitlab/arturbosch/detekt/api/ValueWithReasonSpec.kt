package io.gitlab.arturbosch.detekt.api

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

private const val TEST_STRING = "a.bb.ccc"

class ValueWithReasonSpec {

    @Nested
    inner class LiteralValue {
        @ParameterizedTest
        @ValueSource(
            strings = [
                TEST_STRING,
                "literal:$TEST_STRING"
            ]
        )
        fun matches(value: String) {
            val subject = ValueWithReason(value = value)
            val actual = subject.matchesEntire(TEST_STRING)
            assertThat(actual).isTrue
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                "x$TEST_STRING",
                "${TEST_STRING}x",
                "foo:$TEST_STRING"
            ]
        )
        fun `does not match`(value: String) {
            val subject = ValueWithReason(value = value)
            val actual = subject.matchesEntire(TEST_STRING)
            assertThat(actual).isFalse
        }

    }

    @Nested
    inner class GlobValue {
        @ParameterizedTest
        @ValueSource(
            strings = [
                "glob:$TEST_STRING",
                "glob:*",
                "glob:a*",
                "glob:a.*",
                "glob:?.*",
                "glob:?.??.???",
                "glob:*.bb.*",
                "glob:*b*",
                "glob:*c",
                "glob:a.*c",
                "glob:a.*.ccc",
            ]
        )
        fun matches(value: String) {
            val subject = ValueWithReason(value = value)
            val actual = subject.matchesEntire(TEST_STRING)
            assertThat(actual).isTrue
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                "glob:x$TEST_STRING",
                "glob:${TEST_STRING}x",
                "glob:.*",
                "glob:b*",
                "glob:*b",
                "glob:*.b.*",
                "glob:??.??.??",
                "glob:*.cc",
                "glob:b.*",
            ]
        )
        fun `does not match`(value: String) {
            val subject = ValueWithReason(value = value)
            val actual = subject.matchesEntire(TEST_STRING)
            assertThat(actual).isFalse
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                """glob:[a-c].bb.*""",
                """glob:\w.bb.*""",
                """glob:*.c{1,5}""",
            ]
        )
        fun `invalid glob pattern`(value: String) {
            val subject = ValueWithReason(value = value)

            assertThatThrownBy { subject.matchesEntire(TEST_STRING) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

    }


    @Nested
    inner class RegexValue {
        @ParameterizedTest
        @ValueSource(
            strings = [
                """regex:a\.bb\.ccc""",
                """regex:a.bb.ccc""",
                """regex:.\.bb\.ccc""",
                """regex:.\.b.\.c..""",
                """regex:[a-c]+\.[a-c]+\.[a-c]+""",
                """regex:.*\.[a-c]{3,}""",
                """regex:.*""",
                """regex:a.*""",
                """regex:.*bb.*""",
                """regex:[a-c.]*""",
            ]
        )
        fun matches(value: String) {
            val subject = ValueWithReason(value = value)
            val actual = subject.matchesEntire(TEST_STRING)
            assertThat(actual).isTrue
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                "regex:a\\.bb\\...",
                "regex:.*\\.[a-c]{4,}",
                "regex:x.*bb.*",
                "regex:.*bb.*x",
                "regex:[a-c]*",
            ]
        )
        fun `does not match`(value: String) {
            val subject = ValueWithReason(value = value)
            val actual = subject.matchesEntire(TEST_STRING)
            assertThat(actual).isFalse
        }

    }
}
