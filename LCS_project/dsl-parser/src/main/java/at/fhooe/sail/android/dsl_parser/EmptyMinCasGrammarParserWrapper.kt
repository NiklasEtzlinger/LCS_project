package at.fhooe.sail.android.dsl_parser

import at.fhooe.sail.android.dsl_parser.generated.EmptyMinCasGrammarParser
import at.fhooe.sail.android.dsl_parser.generated.ParseException
import at.fhooe.sail.android.dsl_parser.generated.TokenMgrError
import java.io.StringReader

object EmptyMinCasGrammarParserWrapper {

    fun evaluateExpression(
        input: String
    ): Result<String> {
        return try {
            val parser = EmptyMinCasGrammarParser(StringReader(input))
            parser.expression()
            Result.success("Syntax Validated Successfully!")
        } catch (e: ParseException) {
            Result.failure(
                Exception(
                    "Syntax Error at line ${
                        e.currentToken?.beginLine ?: 0
                    }"
                )
            )
        } catch (e: TokenMgrError) {
            Result.failure(
                Exception(
                    "Lexical Error: " + e.message
                )
            )
        }
    }
}