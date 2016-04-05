package org.microcloud.manager.persistence.objectsloader;

import java.util.List;

import org.microcloud.manager.core.model.datacenter.HostBusyTimes;

public class HostBusyTimesDao extends AbstractDao {
	
	public HostBusyTimesDao() {
		super();
	}
	
    /**
     * Insert a new HostBusyTimes into the database.
     * @param HostBusyTimes
     */
    public void create(HostBusyTimes HostBusyTimes) throws DataAccessLayerException {
        super.saveOrUpdate(HostBusyTimes);
    }


    /**
     * Delete a detached HostBusyTimes from the database.
     * @param HostBusyTimes
     */
    public void delete(HostBusyTimes HostBusyTimes) throws DataAccessLayerException {
        super.delete(HostBusyTimes);
    }

    /**
     * Find an HostBusyTimes by its primary key.
     * @param id
     * @return
     */
    public HostBusyTimes find(Integer id) throws DataAccessLayerException {
        return (HostBusyTimes) super.find(HostBusyTimes.class, id);
    }

    /**
     * Updates the state of a detached HostBusyTimes.
     *
     * @param HostBusyTimes
     */
    public void update(HostBusyTimes HostBusyTimes) throws DataAccessLayerException {
        super.saveOrUpdate(HostBusyTimes);
    }

    /**
     * Finds all HostBusyTimess in the database.
     * @return
     */
    public List findAll() throws DataAccessLayerException{
        return super.findAll(HostBusyTimes.class);
    }

}
