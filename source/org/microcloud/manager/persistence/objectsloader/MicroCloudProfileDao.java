package org.microcloud.manager.persistence.objectsloader;

import java.util.List;

import org.microcloud.manager.core.model.datacenter.MicroCloudProfile;

public class MicroCloudProfileDao extends AbstractDao {
	
	public MicroCloudProfileDao() {
		super();
	}
	
    /**
     * Insert a new profile into the database.
     * @param profile
     */
    public void create(MicroCloudProfile profile) throws DataAccessLayerException {
        super.saveOrUpdate(profile);
    }


    /**
     * Delete a detached profile from the database.
     * @param profile
     */
    public void delete(MicroCloudProfile profile) throws DataAccessLayerException {
        super.delete(profile);
    }

    /**
     * Find an profile by its primary key.
     * @param id
     * @return
     */
    public MicroCloudProfile find(Integer id) throws DataAccessLayerException {
        return (MicroCloudProfile) super.find(MicroCloudProfile.class, id);
    }

    /**
     * Updates the state of a detached profile.
     *
     * @param profile
     */
    public void update(MicroCloudProfile profile) throws DataAccessLayerException {
        super.saveOrUpdate(profile);
    }

    /**
     * Finds all profiles in the database.
     * @return
     */
    public List findAll() throws DataAccessLayerException{
        return super.findAll(MicroCloudProfile.class);
    }

}
