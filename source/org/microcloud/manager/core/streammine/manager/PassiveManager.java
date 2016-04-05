package org.microcloud.manager.core.streammine.manager;

import java.util.List;

import org.microcloud.manager.core.model.streammine.ManagerInput;
import org.microcloud.manager.core.model.streammine.ManagerInputNode;
import org.microcloud.manager.core.model.streammine.ManagerTask;
import org.microcloud.manager.persistence.objectsloader.ManagerTaskDao;

import streammine3G.Action;
import streammine3G.CloudControl;
import streammine3G.Manager;
import streammine3G.PerformanceProbe;

public class PassiveManager implements Manager {

	@Override
	public void init(CloudControl arg0, String arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActionCompleted(Action arg0, String arg1, int arg2,
			String arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCustomProbe(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNodeJoin(String arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNodeLeave(String arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPerformanceProbe(PerformanceProbe arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTimer() {
		
		/* Get tasks from within the next 10 seconds */
	    ManagerTaskDao managerTaskDao = new ManagerTaskDao();
	    List<ManagerTask> tasks = managerTaskDao.getByTime(10);
		
		/* DEPLOY */
	    for(ManagerTask task : tasks) {
	    	ManagerInput managerInput = task.getManagerInput();		
	    	
	    	List<ManagerInputNode> managerInputNodeList = managerInput.getManagerInputNodes();
	    	
	    	System.out.println("NEW TASK");
	    	for(ManagerInputNode n : managerInputNodeList)
	    		System.out.println("node: " + n);

		    /* Remove the deployed task */
	    	managerTaskDao.delete(task);
	    }
		
	}

	@Override
	public String version() {
		// TODO Auto-generated method stub
		return null;
	}

}
