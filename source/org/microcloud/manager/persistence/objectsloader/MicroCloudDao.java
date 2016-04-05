package org.microcloud.manager.persistence.objectsloader;

import java.util.List;

import org.microcloud.manager.core.model.datacenter.MicroCloud;

public class MicroCloudDao extends AbstractDao {
	
	public MicroCloudDao() {
		super();
	}
	
    /**
     * Insert a new DataCenter into the database.
     * @param DataCenter
     */
    public void create(MicroCloud DataCenter) throws DataAccessLayerException {
        super.saveOrUpdate(DataCenter);
    }


    /**
     * Delete a detached DataCenter from the database.
     * @param DataCenter
     */
    public void delete(MicroCloud DataCenter) throws DataAccessLayerException {
        super.delete(DataCenter);
    }

    /**
     * Find an DataCenter by its primary key.
     * @param id
     * @return
     */
    public MicroCloud find(Integer id) throws DataAccessLayerException {
        return (MicroCloud) super.find(MicroCloud.class, id);
    }

    /**
     * Updates the state of a detached DataCenter.
     *
     * @param DataCenter
     */
    public void update(MicroCloud DataCenter) throws DataAccessLayerException {
        super.saveOrUpdate(DataCenter);
    }

    /**
     * Finds all DataCenters in the database.
     * @return
     */
    public List findAll() throws DataAccessLayerException{
        return super.findAll(MicroCloud.class);
    }

}
