package com.allcom.security.entity;

import java.util.ArrayList;
import java.util.List;

import com.allcom.commons.util.tree.Tree;
import com.allcom.commons.util.tree.TreeNode;

/**
 * 资源树形结构.
 * @author dw
 *
 */
public class ResourceTree extends Tree {
	private boolean needCheckbox = false;//是否需要显示checkbox
	private List<Long> excludeIds = new ArrayList<Long>();//需要排除的节点ID

	@SuppressWarnings("unchecked")
	public ResourceTree(List nodes, boolean needCheckbox) {
		this.needCheckbox = needCheckbox;

		super.reload(nodes);
	}

	@SuppressWarnings("unchecked")
	public ResourceTree(List nodes, boolean needCheckbox, List<Long> excludeIds) {
		this.needCheckbox = needCheckbox;
		this.excludeIds = excludeIds;

		super.reload(nodes);
	}
	//	public Resource getResourceNode(String resoureceId) {
	//		TreeNode node = super.getTreeNode(resoureceId);
	//		return node == null ? null : (Resource) node.getBindData();
	//	}

	@Override
	protected TreeNode transform(Object info) {
		Resource resource = (Resource) info;
		if (!excludeIds.contains(resource.getId())) {
			TreeNode node = new TreeNode();
			node.setNodeId(String.valueOf(resource.getId()));
			node.setText(resource.getName());
			node.setUrl(resource.getUrl());
			node.setProperties(resource.getNodeProperties());
			if (needCheckbox) {
				node.setChecked(resource.isChecked());
			}

			if (resource.getParentResource() == null) {
				node.setParentId("");
			} else {
				node.setParentId(String.valueOf(resource.getParentResource().getId()));
			}

			//node.setBindData(resource);
			return node;
		} else
			return null;
	}
}
