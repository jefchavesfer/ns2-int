package tree.java;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {

	private TreeNode parent = null;
	private List<TreeNode> children = null;
	private TreeDataInfo data;

	/**
	 * @param obj
	 *            referenced object
	 */
	public TreeNode(TreeDataInfo tdf) {
		this.parent = null;
		this.data = tdf;
		this.children = new ArrayList<TreeNode>();
		
	}

	/**
	 * @param child
	 *            new tree node
	 */
	public void addChildNode(TreeNode child) {
		child.parent = this;
		if (!this.children.contains(child))
			this.children.add(child);
	}
	
	/**
	 * @param child
	 */
	private void removeChild(TreeNode child) {
		if (this.children.contains(child))
			this.children.remove(child);

	}
	
	/**
	 * remove node from the tree
	 */
	public void remove() {
		if (this.parent != null) {
			this.parent.removeChild(this);
		}
	}


	/**
	 * deep copy (clone)
	 * 
	 * @return copy of TreeNode
	 */
	/*public TreeNode deepCopy() {
		TreeNode newNode = new TreeNode(reference);
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			TreeNode child = (TreeNode) iter.next();
			newNode.addChildNode(child.deepCopy());
		}
		return newNode;
	}*/

	/**
	 * deep copy (clone) and prune
	 * 
	 * @param depth
	 *            - number of child levels to be copied
	 * @return copy of TreeNode
	 */
	/*public TreeNode deepCopyPrune(int depth) {
		if (depth < 0)
			throw new IllegalArgumentException("Depth is negative");
		TreeNode newNode = new TreeNode(reference);
		if (depth == 0)
			return newNode;
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			TreeNode child = (TreeNode) iter.next();
			newNode.addChildNode(child.deepCopyPrune(depth - 1));
		}
		return newNode;
	}*/

	/**
	 * @return level = distance from root
	 */
	public int getLevel() {
		int level = 0;
		TreeNode p = this.parent;
		while (p != null) {
			++level;
			p = p.parent;
		}
		return level;
	}

	/**
	 * walk through subtree of this node
	 * 
	 * @param callbackHandler
	 *            function called on iteration
	 */
	/*public int walkTree(TreeNodeCallback callbackHandler) {
		int code = 0;
		code = callbackHandler.handleTreeNode(this);
		if (code != TreeNodeCallback.CONTINUE)
			return code;
		ChildLoop: for (Iterator iter = children.iterator(); iter.hasNext();) {
			TreeNode child = (TreeNode) iter.next();
			code = child.walkTree(callbackHandler);
			if (code >= TreeNodeCallback.CONTINUE_PARENT)
				return code;
		}
		return code;
	}*/

	/**
	 * walk through children subtrees of this node
	 * 
	 * @param callbackHandler
	 *            function called on iteration
	 */
	/*public int walkChildren(TreeNodeCallback callbackHandler) {
		int code = 0;
		ChildLoop: for (Iterator iter = children.iterator(); iter.hasNext();) {
			TreeNode child = (TreeNode) iter.next();
			code = callbackHandler.handleTreeNode(child);
			if (code >= TreeNodeCallback.CONTINUE_PARENT)
				return code;
			if (code == TreeNodeCallback.CONTINUE) {
				code = child.walkChildren(callbackHandler);
				if (code > TreeNodeCallback.CONTINUE_PARENT)
					return code;
			}
		}
		return code;
	}*/

	/**
	 * @return List of children
	 */
	public List<TreeNode> getChildren() {
		return this.children;
	}

	/**
	 * @return parent node
	 */
	public TreeNode getParent() {
		return this.parent;
	}

	/**
	 * @return reference object
	 */
	public TreeDataInfo getData() {
		return this.data;
	}

	/**
	 * set reference object
	 * 
	 * @param object
	 *            reference
	 */
	public void setData(TreeDataInfo tdi) {
		this.data = tdi;
	}
}
