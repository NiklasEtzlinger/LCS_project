package at.fhooe.sail.android.dsl_parser.tree.constants;

import at.fhooe.sail.android.dsl_parser.tree.TreeNode;
import at.fhooe.sail.android.dsl_parser.tree.context.ContextObject;
import at.fhooe.sail.android.dsl_parser.tree.exception.NodeError;

public class ConstantDaytimeNode extends TreeNode {

    private String mDaytime = null;

    public ConstantDaytimeNode(String _v) { mDaytime = _v; }
    @Override
    public Object calculate() throws NodeError {
        if (mDaytime == null) throw new NodeError();
        return mDaytime;
    }

    @Override
    public void setVariableParameters(ContextObject[] _context) { }

    @Override
    public void clear() { }
}
