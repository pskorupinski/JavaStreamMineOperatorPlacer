package org.microcloud.manager.persistence.objectsloader;

import java.util.List;

import org.microcloud.manager.core.model.datacenter.Host;

public class HostDao extends AbstractDao {
	
	public HostDao() {
		super();
	}
	
    /**
     * Insert a new Host into the database.
     * @param Host
     */
    public void create(Host Host) throws DataAccessLayerException {
        super.saveOrUpdate(Host);
    }


    /**
     * Delete a detached Host from the database.
     * @param Host
     */
    public void delete(Host Host) throws DataAccessLayerException {
        super.delete(Host);
    }

    /**
     * Find an Host by its primary key.
     * @param id
     * @return
     */
    public Host find(Integer id) throws DataAccessLayerException {
        return (Host) super.find(Host.class, id);
    }

    /**
     * Updates the state of a detached Host.
     *
     * @param Host
     */
    public void update(Host Host) throws DataAccessLayerException {
        super.saveOrUpdate(Host);
    }

    /**
     * Finds all Hosts in the database.
     * @return
     */
    public List findAll() throws DataAccessLayerException{
        return super.findAll(Host.class);
    }

}
