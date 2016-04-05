package org.microcloud.manager.core.model.streammine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ManagerInput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9164126626706261582L;

	private List<ManagerInputNode> managerInputNodes = new ArrayList<>();
	
	public List<ManagerInputNode> getManagerInputNodes() {
		return managerInputNodes;
	}
	public void addManagerInputNode(ManagerInputNode managerInputNode) {
		managerInputNodes.add(managerInputNode);
	}
	
}
