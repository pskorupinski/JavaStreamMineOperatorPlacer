package org.microcloud.manager.core.mapper.mongodb;

import com.mongodb.Mongo;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;

import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.manager.core.retriever.DataSource;
import org.microcloud.manager.persistence.EntityLoader;
import org.microcloud.manager.structures.UniqueBiMapping;


import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

public class MongoDbDataSourceOld implements DataSource {

	private String hostName;
	private int port;
	
/////////////////////////////////////////////////////////////////////
//// PUBLIC METHODS
/////////////////////////////////////////////////////////////////////
	
	@Override
	public UniqueBiMapping<Host, Object> getKeysHostsMap (
			final List<Object> keysList) {
	
		final NavigableMap<Object, Set<Host>> chunksMap = getChunksMap();
		
		UniqueBiMapping<Host, Object> hostsKeysMapping = new UniqueBiMapping<>(keysList.size());
		hostsKeysMapping.defineColumns(new HashSet<>(keysList));
		
		// 1. Correctness check - whether objects are of the same type
		Object obj1 = keysList.get(0);
		Object obj2 = chunksMap.entrySet().iterator().next().getKey();
		if( ! obj1.getClass().equals(obj2.getClass()) )
			throw new IllegalArgumentException("Argument list should contain list of objects of type " + obj1.getClass() +
					". Given type " +  obj2.getClass() + " is incorrect key type.");
		
		// 2. For every key, find Set of Hosts
		for(Object key : keysList) {
			Map.Entry<Object, Set<Host>> entry = chunksMap.floorEntry(key);
			if(entry == null)
				throw new IllegalArgumentException("One of given keys doesn't exist in requested resource!");
			else {
				Set<Host> hostsWithKey = entry.getValue();
				
				hostsKeysMapping.addColumnToRowsMapping(hostsWithKey, key);

//				// a. If some key was already found in this group
//				if(hostsKeysMapping.get(hostsWithKey) != null)
//					hostsKeysMapping.get(hostsWithKey).increment();
//				// b. If this is a first key for this group
//				else
//					hostsKeysMapping.put(hostsWithKey, new IncrementableInteger());
			}
			
		}
		
		return hostsKeysMapping;
	}
	
	public MongoDbDataSourceOld(String hostName, int port) {
		this.hostName = hostName;
		this.port = port;
	}
	
/////////////////////////////////////////////////////////////////////
//// PRIVATE METHODS
/////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 * @param shardsCollection - collection "shards" from config db
	 * @return map key-value pairs, where key is a name of a shard and value is a set of hosts
	 */
	private Map<String, Set<Host>> getShardsMap(DBCollection shardsCollection) {
		Map<String, Set<Host>> shardsMap = new TreeMap<>();
		
		DBCursor dbCursor = shardsCollection.find();
		while(dbCursor.hasNext()) {
			DBObject dbObject = dbCursor.next();
			String shardDescString = (String) dbObject.get("host");
			String[] shardDescParts = shardDescString.split("/");
			String[] hostsNames = shardDescParts[1].split(",");
			
			Set<Host> hostsList = new HashSet<>();
			
			for(String hostString : hostsNames) {
				try {
					Host host = EntityLoader.getInstance().getHost(hostString);
					hostsList.add(host);
				} catch (IllegalArgumentException e) {
					// Critical error
					System.err.println(e.getMessage());
					System.exit(-1);
				}
			}
			
			shardsMap.put(shardDescParts[0], hostsList);
		}
		
		return shardsMap;
	}
	
	private NavigableMap<Object, Set<Host>> getChunksMap() {
		
		Mongo m;
		try {
			m = new Mongo( hostName , port );
			
		} catch (UnknownHostException e) {
			System.err.println("Data about Mongo host: " + hostName + ":" + port + " is incorrect!");
			System.err.println("Program is stopping!");
			e.printStackTrace();
			System.exit(-1);
			return null; 
		}
		DB configDB = m.getDB("config");
		
		NavigableMap<Object, Set<Host>> chunksMap = new TreeMap<>();
		
		Map<String, Set<Host>> shardsMap = getShardsMap(configDB.getCollection("shards"));
		
		org.microcloud.manager.logger.Logger.getInstance().log(shardsMap);

		DBCursor dbCursor = configDB.getCollection("chunks").find();
		while(dbCursor.hasNext()) {
			DBObject dbObject = dbCursor.next();
			Object chunkMapKey = ((DBObject)dbObject.get("min")).get("number");
			
			// TODO This is only for test purposes!!!!
			if(chunkMapKey.getClass().equals(org.bson.types.MinKey.class))
				chunkMapKey = "";
			
			String shardsMapKey = (String) dbObject.get("shard");
			Set<Host> chunkMapValue = shardsMap.get(shardsMapKey);
			chunksMap.put(chunkMapKey, chunkMapValue);
		}

		return chunksMap;
	}

//	public String getShardedStatus() throws UnknownHostException {
//		
//		chunksMap = getChunksMap(configDB);
//		
//		org.microcloud.manager.logger.Logger.getInstance().log(chunksMap);
//		org.microcloud.manager.logger.Logger.getInstance().log("\n---\n");
//		org.microcloud.manager.logger.Logger.getInstance().log("8.0 >>" + chunksMap.floorEntry(8.0).getValue());
//		org.microcloud.manager.logger.Logger.getInstance().log("80.0 >>" + chunksMap.floorEntry(80.0).getValue());
//		org.microcloud.manager.logger.Logger.getInstance().log("180.0 >>" + chunksMap.floorEntry(180.0).getValue());
//		
//		
//		return null;
//	}

}
