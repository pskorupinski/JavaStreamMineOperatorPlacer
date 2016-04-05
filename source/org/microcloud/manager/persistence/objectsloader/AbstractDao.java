package org.microcloud.manager.persistence.objectsloader;

import org.hibernate.Session;
import org.microcloud.manager.core.model.datasource.DataSource;
import org.microcloud.manager.persistence.EntityLoader;
import org.microcloud.manager.persistence.PersistenceFactory;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;

import java.util.Date;
import java.util.List;

public abstract class AbstractDao {
    private Session session;
    private Transaction tx;

    public AbstractDao() {
    }

    protected void saveOrUpdate(Object obj) {
        try {
            startOperation();
            session.saveOrUpdate(obj);
            tx.commit();
        } catch (HibernateException e) {
            handleException(e);
        } finally {
            PersistenceFactory.close(session);
        }
    }

    protected void delete(Object obj) {
        try {
            startOperation();
            session.delete(obj);
            tx.commit();
        } catch (HibernateException e) {
            handleException(e);
        } finally {
            PersistenceFactory.close(session);
        }
    }

    protected Object find(Class clazz, Integer id) {
        Object obj = null;
        try {
            startOperation();
            obj = session.get(clazz, id);
            tx.commit();
        } catch (HibernateException e) {
            handleException(e);
        } finally {
            PersistenceFactory.close(session);
        }
        return obj;
    }

    protected List findAll(Class clazz) {
        List objects = null;
        try {
            startOperation();
            Query query = session.createQuery("from " + clazz.getName());
            objects = query.list();
            tx.commit();
        } catch (HibernateException e) {
            handleException(e);
        } finally {
            PersistenceFactory.close(session);
        }
        return objects;
    }
    
    protected Object getResult(String queryString, Class<?> entity, List<Object> params, boolean isUnique) {
    	Object object = null;
        try {
            startOperation();
            Query query = session.createSQLQuery(queryString).addEntity(entity);
            for(int i=0; i<params.size(); i++) {
            	if(params.get(i) instanceof Date)
            		query.setTimestamp(i, (Date) params.get(i));
            	else
            		query.setParameter(i, params.get(i));
            }
            if(isUnique) object = query.uniqueResult();
            else object = query.list();
            tx.commit();
        } catch (HibernateException e) {
            handleException(e);
        } finally {
            PersistenceFactory.close(session);
        }
        return object;
    }

    protected void handleException(HibernateException e) throws DataAccessLayerException {
    	PersistenceFactory.rollback(tx);
        throw new DataAccessLayerException(e);
    }

    protected void startOperation() throws HibernateException {
        session = PersistenceFactory.openSession();
        tx = session.beginTransaction();
    }
}