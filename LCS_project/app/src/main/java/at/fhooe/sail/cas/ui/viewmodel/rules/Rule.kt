package at.fhooe.sail.cas.ui.viewmodel.rules

import at.fhooe.sail.android.dsl_parser.MinCasGrammarParserWrapper
import at.fhooe.sail.android.dsl_parser.tree.TreeNode
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Rule(
    val condition: String,
    val action: Action,
) {
    @Transient
    var conditionTree: TreeNode? = null
        private set

    fun inflate() {
        conditionTree = MinCasGrammarParserWrapper.evaluateExpression(condition)
    }
}
