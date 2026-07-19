import at.fhooe.sail.android.dsl_parser.EmptyMinCasGrammarParserWrapper
import at.fhooe.sail.android.dsl_parser.generated.EmptyMinCasGrammarParser
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertTrue

class EmptyMinCasParserTest {

    @TestFactory
    fun validExpression_shouldSucceed(): List<DynamicTest> {
        val input: List<String> = listOf(
            "c_daytime EQUALS Day",
            "c_daytime EQUALS Night",
            "Day EQUALS c_daytime",
            "Night EQUALS c_daytime",
            "Day EQUALS Night",
        )
        return input.map { line ->
            DynamicTest.dynamicTest("parsing: $line") {
                val result: Result<String> =
                    EmptyMinCasGrammarParserWrapper.evaluateExpression(line)
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
            "TAg",
            "c_daytime EQUALS day",
            "daytime EQUALS Day",
            "c_daytime EQUALS Highnoon"
        )
        return input.map { line ->
            DynamicTest.dynamicTest("parsing: $line") {
                val result: Result<String> =
                    EmptyMinCasGrammarParserWrapper.evaluateExpression(line)
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