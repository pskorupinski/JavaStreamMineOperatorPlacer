package org.microcloud.manager.core.model.datacenter;

import static org.junit.Assert.*;

import java.awt.Point;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Test;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datacenter.LocPoint;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.model.datacenter.Rack;
import org.microcloud.manager.persistence.PersistenceFactory;

public class HostPersistanceTest {

	@Test
	public void test() {
		Session session = null;
		try 
		 {
			try
			{
				SessionFactory sessionFactory = PersistenceFactory.getSessionFactory();
				session = sessionFactory.openSession();
				
				MicroCloud dc;
				Rack r;
				Host host;

				org.microcloud.manager.logger.MyLogger.getInstance().log("Inserting Records");

				Transaction tx = session.beginTransaction();

				dc = new MicroCloud();
				dc.setHost("localhost");
				dc.setLocation(new LocPoint(100.0, 200.0));
				session.save(dc);

				r = new Rack();
				r.setMicroCloud(dc);
				session.save(r);

				host = new Host("localhost:8080");
				host.setRack(r);
				session.save(host);
				
				tx.commit();
				
				org.microcloud.manager.logger.MyLogger.getInstance().log("Done");
				
				org.microcloud.manager.logger.MyLogger.getInstance().log("Time zone:" + dc.getTimeZoneInt());
			} catch (Exception e) {
				org.microcloud.manager.logger.MyLogger.getInstance().log(e.getMessage());
			}
		 } finally {
			session.close();
		}
	}

}
