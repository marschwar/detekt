package io.gitlab.arturbosch.detekt.core.baseline

import io.github.detekt.test.utils.createTempDirectoryForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createIssue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import kotlin.io.path.copyTo
import kotlin.io.path.deleteIfExists

class BaselineFacadeSpec {

    private val baselineFile = createTempDirectoryForTest("baseline_format").resolve("baseline.xml")
    private val validBaseline = resourceAsPath("/baseline_feature/valid-baseline.xml")

    @AfterEach
    fun tearDown() {
        baselineFile.deleteIfExists()
    }

    @Test
    fun `returns a BaselineFilteredResult when the baseline exists`() {
        val detektion = BaselineFacade().transformResult(validBaseline, TestDetektion())

        assertThat(detektion).isInstanceOf(BaselineFilteredResult::class.java)
    }

    @Test
    fun `returns the same detektion when the baseline doesn't exist`() {
        val initialDetektion = TestDetektion()
        val detektion = BaselineFacade().transformResult(baselineFile, initialDetektion)

        assertThat(detektion).isEqualTo(initialDetektion)
    }

    @Test
    fun `doesn't create a baseline file without issues`() {
        BaselineFacade().createOrUpdate(baselineFile, emptyList())

        assertThat(baselineFile).doesNotExist()
    }

    @Test
    fun `creates on top of an existing a baseline file without issues`() {
        validBaseline.copyTo(baselineFile)

        BaselineFacade().createOrUpdate(baselineFile, emptyList())

        assertThat(baselineFile).hasContent(
            """
                <?xml version="1.0" ?>
                <SmellBaseline>
                  <ManuallySuppressedIssues>
                    <ID>LongParameterList:Signature</ID>
                    <ID>LongMethod:Signature</ID>
                  </ManuallySuppressedIssues>
                  <CurrentIssues></CurrentIssues>
                </SmellBaseline>
            """.trimIndent()
        )
    }

    @Test
    fun `creates on top of an existing a baseline file with issues`() {
        validBaseline.copyTo(baselineFile)

        BaselineFacade().createOrUpdate(baselineFile, listOf(createIssue()))

        assertThat(baselineFile).hasContent(
            """
                <?xml version="1.0" ?>
                <SmellBaseline>
                  <ManuallySuppressedIssues>
                    <ID>LongParameterList:Signature</ID>
                    <ID>LongMethod:Signature</ID>
                  </ManuallySuppressedIssues>
                  <CurrentIssues>
                    <ID>TestSmell:TestEntitySignature</ID>
                  </CurrentIssues>
                </SmellBaseline>
            """.trimIndent()
        )
    }
}
