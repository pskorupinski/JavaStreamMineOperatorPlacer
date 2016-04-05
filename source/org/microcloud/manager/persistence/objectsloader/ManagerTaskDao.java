package org.microcloud.manager.persistence.objectsloader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.microcloud.manager.core.model.streammine.ManagerTask;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithm;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithmType;


public class ManagerTaskDao extends AbstractDao {
	
	public ManagerTaskDao() {
		super();
	}
	
    /**
     * Insert a new ManagerTask into the database.
     * @param ManagerTask
     */
    public void create(ManagerTask ManagerTask) throws DataAccessLayerException {
        super.saveOrUpdate(ManagerTask);
    }


    /**
     * Delete a detached ManagerTask from the database.
     * @param ManagerTask
     */
    public void delete(ManagerTask ManagerTask) throws DataAccessLayerException {
        super.delete(ManagerTask);
    }

    /**
     * Find an ManagerTask by its primary key.
     * @param id
     * @return
     */
    public ManagerTask find(Integer id) throws DataAccessLayerException {
        return (ManagerTask) super.find(ManagerTask.class, id);
    }

    /**
     * Updates the state of a detached ManagerTask.
     *
     * @param ManagerTask
     */
    public void update(ManagerTask ManagerTask) throws DataAccessLayerException {
        super.saveOrUpdate(ManagerTask);
    }

    /**
     * Finds all ManagerTasks in the database.
     * @return
     */
    public List findAll() throws DataAccessLayerException{
        return super.findAll(ManagerTask.class);
    }
    
    public List<ManagerTask> getByTime(int secondsFromNow) {
    	
    	Date date = new Date(new Date().getTime() + secondsFromNow*1000);
    	
    	String query = "SELECT * \n" +
				"FROM manager_task mt \n" +
				"WHERE mt.mt_start <= ?";
    	Class<?> entity = ManagerTask.class;
    	List<Object> params = new ArrayList<>();
    	params.add(date);

    	return (List<ManagerTask>) getResult(query, entity, params, false);
    }    

}
