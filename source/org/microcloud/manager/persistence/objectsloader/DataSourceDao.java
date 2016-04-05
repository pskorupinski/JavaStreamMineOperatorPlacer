package org.microcloud.manager.persistence.objectsloader;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.microcloud.manager.core.model.datasource.DataSource;
import org.microcloud.manager.core.model.datasource.DataSourceDefinition;
import org.microcloud.manager.persistence.PersistenceFactory;


public class DataSourceDao extends AbstractDao {
	
	public DataSourceDao() {
		super();
	}
	
    /**
     * Insert a new DataSource into the database.
     * @param DataSource
     */
    public void create(DataSource dataSource) throws DataAccessLayerException {
        super.saveOrUpdate(dataSource);
    }


    /**
     * Delete a detached DataSource from the database.
     * @param DataSource
     */
    public void delete(DataSource dataSource) throws DataAccessLayerException {
        super.delete(dataSource);
    }

    /**
     * Find an DataSource by its primary key.
     * @param id
     * @return
     */
    public DataSource find(Integer id) throws DataAccessLayerException {
        return (DataSource) super.find(DataSource.class, id);
    }

    /**
     * Updates the state of a detached DataSource.
     *
     * @param DataSource
     */
    public void update(DataSource dataSource) throws DataAccessLayerException {
        super.saveOrUpdate(dataSource);
    }

    /**
     * Finds all DataSources in the database.
     * @return
     */
    public List findAll() throws DataAccessLayerException{
        return super.findAll(DataSource.class);
    }
    
    public DataSource getByDefinition(DataSourceDefinition def) {
    	
    	String query = "SELECT * \n" +
				"FROM datasource \n" +
				"INNER JOIN datasource_tech ON ds_tech = dst_id \n" +
				"WHERE dst_type = ? AND dst_techtype = ? AND ds_name = ?";
    	Class<?> entity = DataSource.class;
    	List<Object> params = new ArrayList<>();
    	params.add(def.getDataSourceTech().getDataSourceType().toString());
		params.add(def.getDataSourceTech().getDataSourceTechType().toString());
		params.add(def.getDataSourceName());

    	return (DataSource) getResult(query, entity, params, true);
    }
    
    public DataSource getByName(String name) {
    	
    	String query = "SELECT * \n" +
				"FROM datasource \n" +
				"WHERE ds_name = ?";
    	Class<?> entity = DataSource.class;
    	List<Object> params = new ArrayList<>();
		params.add(name);

    	return (DataSource) getResult(query, entity, params, true);
    }

}
