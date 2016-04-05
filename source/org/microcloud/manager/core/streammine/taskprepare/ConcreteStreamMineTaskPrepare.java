package org.microcloud.manager.core.streammine.taskprepare;


import org.microcloud.manager.core.model.clientquery.ClientQuery;
import org.microcloud.manager.core.model.streammine.ManagerInput;
import org.microcloud.manager.core.model.streammine.ManagerInputNode;
import org.microcloud.manager.core.model.streammine.ManagerTask;
import org.microcloud.manager.core.placer.solution.SolutionGraphDoneHost;
import org.microcloud.manager.core.systemstate.BusyHostsController;
import org.microcloud.manager.persistence.objectsloader.ManagerTaskDao;

public class ConcreteStreamMineTaskPrepare implements StreamMineTaskPrepare {

	@Override
	public boolean storeTask(SolutionGraphDoneHost solutionGraph, ClientQuery clientQuery) {
		
		ManagerTask managerTask = new ManagerTask(clientQuery.getStartTime());
		
		ManagerTaskDao managerTaskDao = new ManagerTaskDao();
		managerTaskDao.create(managerTask);
		
		ManagerInput managerInput = StreamMineTranslator.getInstance().translate(solutionGraph,clientQuery,managerTask);
		
		managerTask.setManagerInput(managerInput);
		managerTaskDao.update(managerTask);
		
		org.microcloud.manager.logger.MyLogger.getInstance().log("Outcome - task definition for StreamMine Manager");
		for(ManagerInputNode n : managerInput.getManagerInputNodes())
			org.microcloud.manager.logger.MyLogger.getInstance().log("Node: " + n);
		
//		BusyHostsController.getInstance().addHostBusyTimes(solutionGraph, clientQuery.getStartTime(), managerTask);
		
		return true;
	}

}
