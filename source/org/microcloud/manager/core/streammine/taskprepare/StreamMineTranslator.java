package org.microcloud.manager.core.streammine.taskprepare;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.NotImplementedException;
import org.microcloud.manager.Factory;
import org.microcloud.manager.core.model.clientquery.ClientQuery;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datasource.DataSource;
import org.microcloud.manager.core.model.datasource.DataSourceTechType;
import org.microcloud.manager.core.model.datasource.DataSourceType;
import org.microcloud.manager.core.model.key.Key;
import org.microcloud.manager.core.model.key.MongoKey;
import org.microcloud.manager.core.model.streammine.ManagerInput;
import org.microcloud.manager.core.model.streammine.ManagerInputNode;
import org.microcloud.manager.core.model.streammine.ManagerTask;
import org.microcloud.manager.core.model.streammine.SliceHost;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithmType;
import org.microcloud.manager.core.placer.solution.SolutionDestination;
import org.microcloud.manager.core.placer.solution.SolutionGraphDoneHost;
import org.microcloud.manager.core.placer.solution.SolutionNode;
import org.microcloud.manager.core.placer.solution.SolutionSource;
import org.microcloud.manager.core.placer.solution.SolutionSourceHost;
import org.microcloud.manager.core.systemstate.BusyHostsController;
import org.microcloud.manager.operations.OperationsOnNumbers;
import org.microcloud.manager.proto.SMOperatorInitParam.GeneralKey;
import org.microcloud.manager.proto.SMOperatorInitParam.SearchRequest;
import org.microcloud.manager.proto.SMOperatorInitParam.SearchRequest.DataSourceImplType;
import org.microcloud.manager.proto.SMOperatorInitParam.SearchRequest.ReadPreferenceType;

import com.google.protobuf.TextFormat;

public class StreamMineTranslator {
	
	private static StreamMineTranslator translator = null;
	private static Properties smconfig = new Properties();

	public static StreamMineTranslator getInstance() {
		if(StreamMineTranslator.translator == null) {
			translator = new StreamMineTranslator();
			
			try { setProperties(); } 
			catch (IOException e) { e.printStackTrace(); }
		}
		
		return StreamMineTranslator.translator;
	}
	
	private static void setProperties() throws IOException {
		String propsFileName = "smconfig";
		
		InputStream is = StreamMineTranslator.class.
				getResourceAsStream("..//config//"+propsFileName+".properties");
		smconfig.load(is);
	}

	public ManagerInput translate(SolutionGraphDoneHost solutionGraph, ClientQuery clientQuery, ManagerTask managerTask) {
		
		/* Generate unique random string for this problem operators */
		UUID uuid = UUID.randomUUID();
		String placementUID = uuid.toString();
		
		if(clientQuery.getWorkerAlgorighmType() == WorkerAlgorithmType.WORD_COUNT) {
			return translateForWordCount(solutionGraph, clientQuery, managerTask, placementUID);
		}
		else {
			throw new NotImplementedException("Translation for this WorkerAlgorithmType not yet implemented");
		}
		
	}
	



///////////////////////////////////////////////////////////
// PRIVATE METHODS
///////////////////////////////////////////////////////////

	private ManagerInput translateForWordCount(SolutionGraphDoneHost solutionGraph,
			ClientQuery clientQuery, ManagerTask managerTask, String placementUID) {
		
		ManagerInput managerInput = new ManagerInput();
		
		/* 
		 * 1. Worker operator (Reducer)
		 */
		
		List<SliceHost> workerHosts = new ArrayList<>();
		int workerSliceId = 0;
		for(SolutionDestination<Host> sd : solutionGraph.getDestinations()) {
			SliceHost sliceHost = createSliceHost(sd, managerTask, workerSliceId);
			workerHosts.add(sliceHost);
			workerSliceId++;
		}
		String workerName = placementUID + "_Workerop_" + clientQuery.getWorkerAlgorighmType().toString();
		
		ManagerInputNode worker = new ManagerInputNode();
		
		worker.setName(workerName);
		worker.setLibraryPath(smconfig.getProperty(clientQuery.getWorkerAlgorighmType().toString()));
		worker.setPartitionerPath(smconfig.getProperty("ALPHABET_PARTITIONER"));
		worker.setHosts(workerHosts);
		
		/*
		 * 2. Mapper
		 */
		
		List<SliceHost> mapperHosts = new ArrayList<>();
		int mapperSliceId = 0;
		/* ... (finished with Access Operators definition) */
		
		String mapperName = placementUID + "_Mapper_" + clientQuery.getWorkerAlgorighmType().toString();
		List<String> mapperWireWith = new ArrayList<>();
		mapperWireWith.add(workerName);
		
		ManagerInputNode mapper = new ManagerInputNode();
		
		mapper.setName(mapperName);
		mapper.setLibraryPath(smconfig.getProperty("MAPPER"));
		mapper.setWireWith(mapperWireWith);
		
		/*
		 * 3. Access operators
		 */
		
		String commonSourceLibraryPath = smconfig.getProperty("SOURCE");
		List<String> commonWireWith = new ArrayList<>();
		commonWireWith.add(mapperName);
		
		Set<SolutionSourceHost> solutionSourceHosts = new HashSet<>();
		List<ManagerInputNode> sourcesList = new ArrayList<>();
		int counter = 0;
		for(SolutionSource ss : solutionGraph.getSources()) {
			/* a new host */
			if(solutionSourceHosts.add(ss.getHost())) {
				SolutionSourceHost ssh = ss.getHost();
				
				List<SliceHost> sourceHosts = new ArrayList<>();
				sourceHosts.add(createSliceHost(ssh, managerTask, 0));
				
				Map<DataSource, Set<SolutionSource>> sourcesOnHostGrouped = groupSources(ssh.getSourcesSet());
				
				for(Map.Entry<DataSource, Set<SolutionSource>> src : sourcesOnHostGrouped.entrySet()) {
					
					/* define parameter : name */
					String sourceName = placementUID + "_Accessop_" + OperationsOnNumbers.intToString(counter, 5); 
										
					/* define parameter : parameters */
					String parameters = createParametersObject(
							ssh.getHost().getName(), 
							src.getKey(), 
							src.getValue(),
							clientQuery,
							mapperSliceId);
					
					
					ManagerInputNode source = new ManagerInputNode();
					source.setName(sourceName);
					source.setLibraryPath(commonSourceLibraryPath);
					source.setWireWith(commonWireWith);
					source.setParameters(parameters);
					source.setHosts(sourceHosts);

					sourcesList.add(source);
					
					counter++;					
				}
				
				/* ... (mapper slice id) */
				SliceHost mapperSliceHost = createSliceHost(ssh, managerTask, mapperSliceId);
				mapperHosts.add(mapperSliceHost);
				mapperSliceId++;
				
			}
		}
		
		mapper.setRoutingKeyRangeSize(mapperHosts.size());
		mapper.setHosts(mapperHosts);
		
		/* set EOS signals to shutdown for every operator */
		worker.setEOSSignalsToShutDownSlice(sourcesList.size());
		mapper.setEOSSignalsToShutDownSlice(sourcesList.size());
		for(ManagerInputNode source : sourcesList)
			source.setEOSSignalsToShutDownSlice(1);
		
		/* add manager input nodes */
		managerInput.addManagerInputNode(worker);
		managerInput.addManagerInputNode(mapper);
		for(ManagerInputNode source : sourcesList)
			managerInput.addManagerInputNode(source);
			
		return managerInput;
	}	
	
	private Map<DataSource, Set<SolutionSource>> groupSources(Set<SolutionSource> sourcesSet) {
		Map<DataSource, Set<SolutionSource>> dataSourcesMap = new HashMap<>();
		
		for(SolutionSource ss : sourcesSet) {
			DataSource ds = ss.getKey().getKey().getDataSource();
			Set<SolutionSource> ofDataSourceSet;
			if(dataSourcesMap.containsKey(ds))
				ofDataSourceSet = dataSourcesMap.get(ds);
			else
				ofDataSourceSet = new HashSet<>();
			ofDataSourceSet.add(ss);
			dataSourcesMap.put(ds, ofDataSourceSet);
		}
		
		return dataSourcesMap;
	}
	
	private String createParametersObject(String hostName, DataSource ds, Set<SolutionSource> solutionSources, ClientQuery clientQuery, int mapperSliceId) {
			SearchRequest.Builder builder = SearchRequest.newBuilder();
			
			builder.setHost(hostName); /*setter*/
			builder.setPort(ds.getPortOnHosts()); /*setter*/
			builder.setSourceName(ds.getCollName()); /*setter*/
			
			builder.setPartitionKey(mapperSliceId);
			
			if(ds.getDataSourceType() == DataSourceType.REAL_TIME) {
				builder.setDataSourceImplType(DataSourceImplType.REAL_TIME); /*setter*/
				builder.setReadPreferenceType(ReadPreferenceType.READ_ALL); /*setter*/
				builder.setTimeLimitMin(clientQuery.getTime());
			}
			else {
				if(ds.getDataSourceTechType() == DataSourceTechType.MONGODB_CLUSTER) {
					builder.setDataSourceImplType(DataSourceImplType.MONGO_GRIDFS); /*setter*/
					builder.setReadPreferenceType(ReadPreferenceType.LIST_OF_KEYS); /*setter*/
					
					for(SolutionSource ss : solutionSources) {
						Key key = ss.getKey().getKey();
						if(key instanceof MongoKey) {
							MongoKey mongoKey = (MongoKey) key;
							
							GeneralKey.Builder keyBuilder = GeneralKey.newBuilder();
							keyBuilder.setName(mongoKey.getKey());
							keyBuilder.setFirst(mongoKey.getFirstChunk());
							keyBuilder.setLast(mongoKey.getLastChunk());
							GeneralKey generalKey = keyBuilder.build();
							
							builder.addGeneralKeys(generalKey); /*setter*/
						}
						else {
							throw new RuntimeException("A key of Mongo Data Source is not an instance of MongoKey!");
						}
					}
					
				}
				else {
					throw new NotImplementedException();
				}
			}
			
			return TextFormat.printToString(builder.build());
		}
	
	private SliceHost createSliceHost(SolutionNode solutionNode, ManagerTask managerTask, Integer sliceId) {
		
		//int hostBusyTimeId = BusyHostsController.getInstance().addHostBusyTime(solutionNode, managerTask);
		
		return new SliceHost(((Host)solutionNode.getNodeObject()).getName(),sliceId,0/*hostBusyTimeId*/);
	}
	
}
