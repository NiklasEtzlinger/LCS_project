import at.fhooe.sail.android.dsl_parser.DemoGrammarParserWrapper
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertTrue

class DemoParserWrapperTest {

    @TestFactory
    fun validExpression_shouldSucceed(): List<DynamicTest> {
        val input: List<String> = listOf(
            "1+2",
            "1",
            "1+2+3",
            "2 * (3 + 1)",
            "(1 + 2) * (2 + 3)",
        )
        return input.map { line ->
            DynamicTest.dynamicTest("parsing: $line") {
                val result: Result<String> =
                    DemoGrammarParserWrapper.evaluateExpression(line)
                assertTrue(
                    result.isSuccess,
                    """
                            Expected expression to succeed:
                            $line
                            Error:
                            ${result.exceptionOrNull()?.message}
                          """.trimIndent()
                )
            }
        } // map
    }

    @TestFactory
    fun invalidExpression_shouldFail(): List<DynamicTest> {
        val input: List<String> = listOf(
            "+2",
            "-",
            "**3",
            ")("
        )
        return input.map { line ->
            DynamicTest.dynamicTest("parsing: $line") {
                val result: Result<String> =
                    DemoGrammarParserWrapper.evaluateExpression(line)
                assertTrue(
                    result.isFailure,
                    """
                                Expected expression to fail:
                                $line
                                Error:
                                ${result.exceptionOrNull()?.message}
                              """.trimIndent()
                )
            }
        } // map
    }
}