package at.fhooe.sail.android.dsl_parser.tree.variables;

import at.fhooe.sail.android.dsl_parser.tree.TreeNode;
import at.fhooe.sail.android.dsl_parser.tree.context.ContextObject;
import at.fhooe.sail.android.dsl_parser.tree.exception.NodeError;
import at.fhooe.sail.android.dsl_parser.tree.context.ContextDaytime;

public class VariableDaytimeNode extends TreeNode {

    private ContextObject mContextValue = null;
    @Override
    public Object calculate() throws NodeError {
        if (mContextValue == null) throw new NodeError();
        return mContextValue.value;
    }

    @Override
    public void setVariableParameters(ContextObject[] _context) {
        for (int i = 0; i < _context.length ; i++) {
            ContextObject o = _context[i];
            if (o instanceof ContextDaytime) {
                mContextValue = o;
            }
        }
    }

    @Override
    public void clear() {
        mContextValue = null;
    }
}
