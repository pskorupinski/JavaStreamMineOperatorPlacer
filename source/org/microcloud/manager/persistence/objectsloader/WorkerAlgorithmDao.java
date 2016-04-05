package org.microcloud.manager.persistence.objectsloader;

import java.util.ArrayList;
import java.util.List;

import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithm;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithmType;


public class WorkerAlgorithmDao extends AbstractDao {
	
	public WorkerAlgorithmDao() {
		super();
	}
	
    /**
     * Insert a new WorkerAlgorithm into the database.
     * @param WorkerAlgorithm
     */
    public void create(WorkerAlgorithm workerAlgorithm) throws DataAccessLayerException {
        super.saveOrUpdate(workerAlgorithm);
    }


    /**
     * Delete a detached WorkerAlgorithm from the database.
     * @param WorkerAlgorithm
     */
    public void delete(WorkerAlgorithm WorkerAlgorithm) throws DataAccessLayerException {
        super.delete(WorkerAlgorithm);
    }

    /**
     * Find an WorkerAlgorithm by its primary key.
     * @param id
     * @return
     */
    public WorkerAlgorithm find(Integer id) throws DataAccessLayerException {
        return (WorkerAlgorithm) super.find(WorkerAlgorithm.class, id);
    }

    /**
     * Updates the state of a detached WorkerAlgorithm.
     *
     * @param WorkerAlgorithm
     */
    public void update(WorkerAlgorithm WorkerAlgorithm) throws DataAccessLayerException {
        super.saveOrUpdate(WorkerAlgorithm);
    }

    /**
     * Finds all WorkerAlgorithms in the database.
     * @return
     */
    public List findAll() throws DataAccessLayerException{
        return super.findAll(WorkerAlgorithm.class);
    }
    
    public WorkerAlgorithm getByType(WorkerAlgorithmType type) {
    	
    	String query = "SELECT * \n" +
				"FROM workeralgorithm \n" +
				"WHERE wa_type = ?";
    	Class<?> entity = WorkerAlgorithm.class;
    	List<Object> params = new ArrayList<>();
    	params.add(type.toString());

    	return (WorkerAlgorithm) getResult(query, entity, params, true);
    }

}
