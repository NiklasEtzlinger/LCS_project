import at.fhooe.sail.android.dsl_parser.DemoGrammarParserWrapper
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


class DemoGrammarParserWrapperTest {
    @Test
    fun validExpression_shouldSucceed(){
        val result: Result<String> = DemoGrammarParserWrapper.evaluateExpression("1+2")

        assertTrue(result.isSuccess, "Error")
    }
}