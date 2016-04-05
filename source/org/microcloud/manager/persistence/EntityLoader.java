package org.microcloud.manager.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.microcloud.manager.core.model.datacenter.MicroCloudProfileNode;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datacenter.HostBusyTimes;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.model.datasource.DataSource;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithm;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithmProfileNode;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithmType;

public class EntityLoader {
	
	private static EntityLoader entityLoader = null;
	
	private SessionFactory sessionFactory = PersistenceFactory.getSessionFactory();
	private Session session = sessionFactory.openSession();

	public static EntityLoader getInstance() {
		if(entityLoader == null)
			entityLoader = new EntityLoader();
		return entityLoader;
	}
	
	public Host getHost(String hostString) {
		
		String [] hostPortArray = hostString.split(":");
		
		Query query = session.createSQLQuery("SELECT * \n" +
				"FROM host h \n" +
				"WHERE h_name = :name")
				.addEntity(Host.class)
				.setParameter("name", hostPortArray[0]);
		
		Host host = (Host)query.uniqueResult();
		
		return host;
	}
	
	public List<HostBusyTimes> getDataCenters() {
		
		Query query = session.createSQLQuery("SELECT * \n" +
				"FROM datacenter d")
				.addEntity(MicroCloud.class);
		
		List<HostBusyTimes> list = query.list();
		
		return list;		
	}
	
	public WorkerAlgorithm getWorkerAlgorithm(WorkerAlgorithmType workerAlgorithmType) {
		
		Query query = session.createSQLQuery(
				"SELECT * \n" +
				"FROM workeralgorithm \n" +
				"WHERE wa_type = :type")
				.addEntity(WorkerAlgorithm.class)
				.setParameter("type", workerAlgorithmType.toString());
		
		WorkerAlgorithm wa = (WorkerAlgorithm)query.uniqueResult();
		
		return wa;		
	}
	
	@SuppressWarnings("unchecked")
	public List<Host> getBusyHostsInTime(Set<Integer> hostsIds, Date startTime, Date endTime) {
		
		Query query = session.createSQLQuery(
				"SELECT * \n" +
				"FROM host h \n" +
				"JOIN hostbusytimes hbt ON h.h_id = hbt.h_id \n" +
				"WHERE h.h_id IN (:ids)" +
				"AND (hbt.hbt_starttime < :end AND hbt.hbt_endtime > :begin)"
				)
				.addEntity(Host.class)
				.setParameterList("ids", hostsIds)
				.setTimestamp("begin", startTime)
				.setTimestamp("end", endTime);
		
		List<Host> list = query.list();
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * 
	 * @param dataCenterId
	 * @param startTime
	 * @param endTime
	 * @return list of data center profile nodes - one before startTime until one before endTime
	 */
	public List<MicroCloudProfileNode> getDataCenterProfileNodes(int dataCenterProfileId, Date startTime, Date endTime) {
		
		List<MicroCloudProfileNode> dataCenterProfileNodes = new ArrayList<>();
		
		Query query1 = session.createSQLQuery(
				"SELECT * \n" +
				"FROM microcloud_profile_node mcpn \n" +
				"WHERE mcpn.mcp_id = :id \n" +
				"AND mcpn.mcpn_time < :start \n" +
				"ORDER BY mcpn.mcpn_time DESC LIMIT 1")
				.addEntity(MicroCloudProfileNode.class)
				.setParameter("id", dataCenterProfileId)
				.setTimestamp("start", startTime);
		
		if(query1.uniqueResult() != null)
			dataCenterProfileNodes.add( (MicroCloudProfileNode) query1.uniqueResult() );
		
		Query query2 = session.createSQLQuery(
				"SELECT * \n" +
				"FROM microcloud_profile_node mcpn \n" +
				"WHERE mcpn.mcp_id = :id \n" +
				"AND mcpn.mcpn_time BETWEEN :start AND :end \n" +
				"ORDER BY mcpn.mcpn_time ASC")
				.addEntity(MicroCloudProfileNode.class)
				.setParameter("id", dataCenterProfileId)
				.setTimestamp("start", startTime)
				.setTimestamp("end", endTime);
		
		dataCenterProfileNodes.addAll( query2.list() );	
		
		return dataCenterProfileNodes;
	}

	@SuppressWarnings("unchecked")
	public List<WorkerAlgorithmProfileNode> getWorkerAlgorithmProfile(WorkerAlgorithmType workerAlgorithmType) {
		
		Query query = session.createSQLQuery(
				"SELECT * \n" +
				"FROM workeralgorithm_profilenode wapn \n" +
				"JOIN workeralgorithm wa ON wa.wa_id = wapn.wa_id \n" +
				"WHERE wa.wa_type = :type \n" +
				"ORDER BY wapn.wapn_slicesno ASC"
				)
				.addEntity(WorkerAlgorithmProfileNode.class)
				.setParameter("type", workerAlgorithmType.name());
		
		List<WorkerAlgorithmProfileNode> workerAlgorighmProfile = query.list();
		
		return workerAlgorighmProfile;
	}

	public Session getSession() {
		return session;
	}
	
}
