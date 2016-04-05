package org.microcloud.manager.core.mapper.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.Mongo;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

import org.bson.types.ObjectId;
import org.microcloud.manager.core.mapper.RetrievableDataSource;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datasource.DataSource;
import org.microcloud.manager.core.model.key.Key;
import org.microcloud.manager.core.model.key.MongoKey;
import org.microcloud.manager.core.model.key.OvergrownMongoKey;
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

public class MongoDbDataSource extends RetrievableDataSource {

	private String hostName;
	private int port;
	private String namespace;
	
	private Mongo m;
	
/////////////////////////////////////////////////////////////////////
//// CONSTRUCTORS
/////////////////////////////////////////////////////////////////////
	
	public MongoDbDataSource(DataSource dataSource) {
		super(dataSource);
		
		this.hostName = dataSource.getHostName();
		this.port = dataSource.getPort();
		this.namespace = dataSource.getCollName();
		
		try {
			m = new Mongo( hostName , port );
			
		} catch (UnknownHostException e) {
			System.err.println("Data about Mongo host: " + hostName + ":" + port + " is incorrect!");
			System.err.println("Program is stopping!");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
/////////////////////////////////////////////////////////////////////
//// PUBLIC METHODS
/////////////////////////////////////////////////////////////////////
	
	@Override
	public UniqueBiMapping<Host, Key> getKeysHostsMap (final List<Object> keysList) {
	
		final NavigableMap<ChunkMapKey, Set<Host>> chunksMap = getChunksMap();
		
		org.microcloud.manager.logger.MyLogger.getInstance().log(chunksMap);
		
		UniqueBiMapping<Host, Key> hostsKeysMapping = new UniqueBiMapping<>();

		/* For every key, find Sets of Hosts */
		for(Object key : keysList) {
			ChunkMapKey anotherKey = new ChunkMapKey(key, -1);
			NavigableMap.Entry<ChunkMapKey, Set<Host>> entry = chunksMap.floorEntry(anotherKey);
			if(entry == null)
				throw new IllegalArgumentException("One of given keys doesn't exist in requested resource!");
			else {
				
				/* retrieve chunk size and number of chunks of the key */
				DB db = m.getDB("filesystem");
				GridFS gridFs = new GridFS(db);
				BasicDBObject dbObject = new BasicDBObject();
				dbObject.put("_id",(String)key);
				GridFSDBFile file1 = gridFs.findOne(dbObject);
				int chunkSizeKB = (int) (file1.getChunkSize()/1024);
				int chunksNo = (int) Math.ceil(file1.getLength()/(double)file1.getChunkSize());
				
				int lastChunk = chunksNo-1;
				boolean bContinue = true;
				/* Find all the entries which have a part of key -
				 * 		the one found before should be the last */
				do
				{
					MongoKey keyOfMapping;
					Set<Host> hostsWithKey;
					
					if(entry.getKey().getMinFiles_id().equals(key) && entry.getKey().getMinN() > 0) {
						
						keyOfMapping = new OvergrownMongoKey(dataSource, (String) key, entry.getKey().getMinN(), lastChunk, chunkSizeKB);
						hostsWithKey = entry.getValue();

						bContinue = true;
						lastChunk = entry.getKey().getMinN() -1;
						entry = chunksMap.lowerEntry(entry.getKey());
					}
					else {
						
						keyOfMapping = new OvergrownMongoKey(dataSource, (String) key, 0, lastChunk, chunkSizeKB);
						hostsWithKey = entry.getValue();
						
						bContinue = false;
					}

					hostsKeysMapping.addColumnToRowsMapping(hostsWithKey, keyOfMapping);
					
				} while(bContinue);
			}
			
		}
		
		return hostsKeysMapping;
	}
	
/////////////////////////////////////////////////////////////////////
//// PRIVATE / PROTECTED METHODS
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
				hostString = fitHostName(hostString);
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

	/**
	 * 
	 * @return mapping from Mongo keys to sets of hosts
	 */
	protected NavigableMap<ChunkMapKey, Set<Host>> getChunksMap() {
		
		DB configDB = m.getDB("config");
		
		NavigableMap<ChunkMapKey, Set<Host>> chunksMap = new TreeMap<>();
		
		Map<String, Set<Host>> shardsMap = getShardsMap(configDB.getCollection("shards"));
		
		//org.microcloud.manager.logger.Logger.getInstance().log(shardsMap);

		DBCursor dbCursor = configDB.getCollection("chunks").find();
		while(dbCursor.hasNext()) {
			DBObject dbObject = dbCursor.next();
			String ns = (String) dbObject.get("ns");
			if(! ns.startsWith(this.namespace+".fs"))
			//if(! dbObject.get("ns").equals(this.namespace))
				continue;
			
			/* specific implementation for GridFS */
			Object minFiles_id =  ((DBObject)dbObject.get("min")).get("files_id");
			Object minN   =  ((DBObject)dbObject.get("min")).get("n");
			if(minN == null) minN = 0;
			ChunkMapKey chunkMapKey = new ChunkMapKey(minFiles_id, minN);
			
			String shardsMapKey = (String) dbObject.get("shard");
			Set<Host> chunkMapValue = shardsMap.get(shardsMapKey);
			chunksMap.put(chunkMapKey, chunkMapValue);
		}

		return chunksMap;
	}
	
	private String fitHostName(String hostString) {
		int endIndex = hostString.indexOf(":");
		if(endIndex >= 0)
			return hostString.substring(0, endIndex);
		return hostString;
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
