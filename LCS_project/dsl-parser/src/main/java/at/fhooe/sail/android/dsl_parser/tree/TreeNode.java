package at.fhooe.sail.android.dsl_parser.tree;

import at.fhooe.sail.android.dsl_parser.tree.context.ContextObject;
import at.fhooe.sail.android.dsl_parser.tree.exception.NodeError;

public abstract class TreeNode {
    protected TreeNode[] mChilds = null;

    public abstract Object calculate() throws NodeError;

    public abstract void setVariableParameters(ContextObject[] _context);

    public abstract void clear();

    public void setmChilds(TreeNode[] _c) { mChilds = _c; }
}


