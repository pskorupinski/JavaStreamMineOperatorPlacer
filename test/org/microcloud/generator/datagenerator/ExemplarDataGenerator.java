package org.microcloud.generator.datagenerator;

import java.awt.Point;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.microcloud.manager.core.model.datacenter.MicroCloudAttributes;
import org.microcloud.manager.core.model.datacenter.MicroCloudProfile;
import org.microcloud.manager.core.model.datacenter.MicroCloudProfileNode;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datacenter.HostBusyTimes;
import org.microcloud.manager.core.model.datacenter.LocPoint;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.model.datacenter.Rack;
import org.microcloud.manager.core.model.datacenter.MicroCloudProfileNode.MicroCloudProfilePk;
import org.microcloud.manager.core.model.datasource.DataSource;
import org.microcloud.manager.core.model.datasource.DataSourceDefinition;
import org.microcloud.manager.core.model.datasource.DataSourceTech;
import org.microcloud.manager.core.model.datasource.DataSourceTechType;
import org.microcloud.manager.core.model.datasource.DataSourceType;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithm;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithmProfileNode;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithmType;
import org.microcloud.manager.persistence.PersistenceFactory;

public class ExemplarDataGenerator {
	
	private static ExemplarDataGenerator gen = null;

	public static ExemplarDataGenerator getInstance() {
		if(gen == null)
			gen = new ExemplarDataGenerator();
		return gen;
	}
	
	public void generate() {
		Session session = null;
		try 
		 {
			try
			{
				SessionFactory sessionFactory = PersistenceFactory.getSessionFactory();
				session = sessionFactory.openSession();
				
				// 11 serializable classes of objects
				
				MicroCloud dc;
				MicroCloudAttributes dca;
				MicroCloudProfileNode dcpn;
				MicroCloudProfile dcp;
								
				Rack r;
				
				Host [] host = new Host[6];
				HostBusyTimes hbt;
				
				DataSource dataSource;
				DataSourceTech dataSourceTech;
				
				WorkerAlgorithm wa;
				WorkerAlgorithmProfileNode wapn;
				
				org.microcloud.manager.logger.MyLogger.getInstance().log("Inserting Records");
				
				////////////////////////////////////////////////////////////
				
				Transaction tx = session.beginTransaction();
				
				dca = new MicroCloudAttributes();
				dca.setId(0);
				dca.setInputBandwidthMBitInt(64);
				dca.setOutputBandwidthMBitInt(16);
				dca.setConnectionBandInside(256);
				session.save(dca);
				
				dcp = new MicroCloudProfile();
				dcp.setName("profile1");
				session.save(dcp);
				
				dcpn = new MicroCloudProfileNode();
				dcpn.setInPrice(5);
				dcpn.setOutPrice(8);
				dcpn.setTime(new Date());
				dcpn.setDataCenterProfile(dcp);
				dcpn.setUsagePrice(10);
				session.save(dcpn);
				
				dc = new MicroCloud();
				dc.setHost("localhost");
				dc.setLocation(new LocPoint(100.0, 200.0));
				dc.setDataCenterAttributes(dca);
				dc.setDataCenterProfile(dcp);
				dc.setName("dc1");
				session.save(dc);
				
				r = new Rack();
				r.setMicroCloud(dc);
				r.setName("r1");
				session.save(r);
				
				hbt = new HostBusyTimes();
				hbt.setExpStartTime(new Date());
				hbt.setExpEndTime(new Date(new Date().getTime()+1000*60*60));
			
				int i = 1;
				for(Host h : host) {
					h = new Host("localhost:1000" + i);
					h.setRack(r);
					h.setComputationPowerFactor(1.0);
					h.setDiskReadSpeed(1024);
					session.save(h);
					
					hbt = new HostBusyTimes();
					hbt.setExpStartTime(new Date());
					hbt.setExpEndTime(new Date(new Date().getTime()+1000*60*60));
					hbt.setHost(h);
					session.save(hbt);
					
					i++;
				}
				
				dataSourceTech = new DataSourceTech();
				dataSourceTech.setDataSourceType(DataSourceType.HISTORICAL);
				dataSourceTech.setDataSourceTechType(DataSourceTechType.MONGODB_CLUSTER);
				dataSourceTech.setSourceBandwidthFactor(1.0);
				dataSourceTech.setDataSourceVersion("2.0.0");
				session.save(dataSourceTech);
				
				DataSourceDefinition dataSourceDef = new DataSourceDefinition();
				dataSourceDef.setDataSourceName("source1");
				dataSourceDef.setDataSourceTech(dataSourceTech);
				
				dataSource = new DataSource(dataSourceDef, "localhost", 27017, null);
				session.save(dataSource);
				
				wa = new WorkerAlgorithm();
				wa.setType(WorkerAlgorithmType.WORD_COUNT);
				session.save(wa);
				
				wapn = new WorkerAlgorithmProfileNode();
				wapn.setWorkerAlgorithm(wa);
				wapn.setSlicesNo(1);
				wapn.setVelocity(1024);
				session.save(wapn);
				
				tx.commit();
				
				org.microcloud.manager.logger.MyLogger.getInstance().log("Done");
			} catch (Exception e) {
				org.microcloud.manager.logger.MyLogger.getInstance().log(e.getMessage());
			}
		 } finally {
			session.close();
		 }
	}
	
	public static void main(String [] args) {
		ExemplarDataGenerator.getInstance().generate();
	}
}
