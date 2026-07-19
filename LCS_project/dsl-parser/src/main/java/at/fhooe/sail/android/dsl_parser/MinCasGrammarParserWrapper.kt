package at.fhooe.sail.android.dsl_parser

import at.fhooe.sail.android.dsl_parser.generated.MinCasGrammarParser
import at.fhooe.sail.android.dsl_parser.generated.ParseException
import at.fhooe.sail.android.dsl_parser.generated.TokenMgrError
import at.fhooe.sail.android.dsl_parser.tree.TreeNode
import java.io.StringReader

object MinCasGrammarParserWrapper {

    fun evaluateExpression(
        input: String
    ): TreeNode? {
        return try {
            val parser = MinCasGrammarParser(StringReader(input))
            return parser.expression()
            // Result.success("Syntax Validated Successfully!")
        } catch (e: ParseException) {
            println(e)
            return null;
            /*
            Result.failure(
                Exception(
                    "Syntax Error at line ${
                        e.currentToken?.beginLine ?: 0
                    }"
                )
            )
             */
        } catch (e: TokenMgrError) {
            println(e)
            return null;
            /*
            Result.failure(
                Exception(
                    "Lexical Error: " + e.message
                )
            )
             */
        }
    }
}