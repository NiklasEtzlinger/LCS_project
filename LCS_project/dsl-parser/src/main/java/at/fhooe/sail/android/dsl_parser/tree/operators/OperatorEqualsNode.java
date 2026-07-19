package at.fhooe.sail.android.dsl_parser.tree.operators;

import at.fhooe.sail.android.dsl_parser.tree.TreeNode;
import at.fhooe.sail.android.dsl_parser.tree.context.ContextObject;
import at.fhooe.sail.android.dsl_parser.tree.exception.NodeError;

public class OperatorEqualsNode extends TreeNode {
    @Override
    public Object calculate() throws NodeError {
        if (mChilds != null) {
            if (mChilds.length == 2) {
                Object v1 = mChilds[0].calculate();
                Object v2 = mChilds[1].calculate();

                if (v1 instanceof String && v2 instanceof String) {
                    String a = (String)v1;
                    String b = (String)v2;
                    return a.equals(b);
                }
                throw new NodeError();
            }
            throw new NodeError();
        }
        throw new NodeError();
    }

    @Override
    public void setVariableParameters(ContextObject[] _context) {
        if (mChilds != null) {
            for (int i = 0 ; i < mChilds.length ; i++) {
                mChilds[i].setVariableParameters(_context);
            }
        }
    }

    @Override
    public void clear() {

    }
}
