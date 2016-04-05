package org.microcloud.manager.core.systemstate;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datacenter.HostBusyTimes;
import org.microcloud.manager.core.model.streammine.ManagerTask;
import org.microcloud.manager.core.placer.solution.SolutionDestination;
import org.microcloud.manager.core.placer.solution.SolutionGraphDoneHost;
import org.microcloud.manager.core.placer.solution.SolutionNode;
import org.microcloud.manager.core.placer.solution.SolutionSource;
import org.microcloud.manager.persistence.objectsloader.HostBusyTimesDao;

public class BusyHostsController {
	
	private static BusyHostsController busyHostsController = null;
	
	public static BusyHostsController getInstance() {
		if(BusyHostsController.busyHostsController == null)
			busyHostsController = new BusyHostsController();
		
		return busyHostsController;
	}
	
	private HostBusyTimesDao hostBusyTimesDao = new HostBusyTimesDao();
	
	public boolean addHostBusyTimes(SolutionGraphDoneHost solutionGraph, Date startTime, ManagerTask managerTask) {
				
		Set<SolutionNode> allNodes = new HashSet<>();
		
		for(SolutionSource ss : solutionGraph.getSources()) {
			allNodes.add(ss.getHost());
		}
		for(SolutionDestination<Host> sd : solutionGraph.getDestinations()) {
			allNodes.add(sd);
		}
		
		for(SolutionNode sn : allNodes) {
			HostBusyTimes hostBusyTimes = new HostBusyTimes();
			hostBusyTimes.setExpStartTime(startTime);
			hostBusyTimes.setExpEndTime(sn.getEndTime());
			hostBusyTimes.setManagerTask(managerTask);
			hostBusyTimes.setHost((Host) sn.getNodeObject());
			
			hostBusyTimesDao.create(hostBusyTimes);
		}
		
		return true;
	}
	
	public int addHostBusyTime(SolutionNode solutionNode, ManagerTask managerTask) {
		
		HostBusyTimes hostBusyTimes = new HostBusyTimes();
		hostBusyTimes.setExpStartTime(managerTask.getStartTime());
		hostBusyTimes.setExpEndTime(solutionNode.getEndTime());
		hostBusyTimes.setManagerTask(managerTask);
		hostBusyTimes.setHost((Host) solutionNode.getNodeObject());
		
		hostBusyTimesDao.create(hostBusyTimes);
		
		return hostBusyTimes.getId();
	}

	public void freeHostTime(Integer hostBusyTimesId) {
		
		HostBusyTimes hostBusyTimes = hostBusyTimesDao.find(hostBusyTimesId);
		if(hostBusyTimes != null)
			hostBusyTimesDao.delete(hostBusyTimes);
		
	}
	
}
