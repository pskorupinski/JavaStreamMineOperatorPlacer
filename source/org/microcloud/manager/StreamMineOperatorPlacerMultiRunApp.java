package org.microcloud.manager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.microcloud.manager.core.model.clientquery.ClientQuery;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithmType;
import org.microcloud.manager.core.placer.solution.SolutionGraphDoneHost;
import org.microcloud.manager.core.schedulerinput.ConcreteSourceManager;
import org.microcloud.manager.core.schedulerinput.SourceManager;

public class StreamMineOperatorPlacerMultiRunApp {

	public static enum EntranceType {
		CONFIG_NAME,
		EXEC_TIME,
		EXEC_START_DAY,
		EXEC_START_HOUR,
		SOURCE_TYPE,
		SOURCE_NAME,
		KEY_NAME
	}

	private static Properties filesMap = new Properties();
	
	private static Object translateKey(String string) {
		return filesMap.get(string);
	}
	
	private static boolean isInt(String s) {
		Scanner sc = new Scanner(s.trim());
		if(!sc.hasNextInt()) return false;
		sc.nextInt();
		return !sc.hasNext();
	}
	
	private static EntranceType[] parseArgs(String[] args) {
		EntranceType [] entranceTypeArray;
		
		if(args.length < 4) {
			entranceTypeArray = null;
		}
		else {
			entranceTypeArray = new EntranceType[args.length];
			
			entranceTypeArray[0] = EntranceType.CONFIG_NAME;
			
			int lastSourceType = 0;
			boolean rightPlaceToFinish = false;
			EntranceType expectedType = EntranceType.SOURCE_NAME;
			
			if(!isInt(args[1]) || !isInt(args[2])) {
				entranceTypeArray = null;
			}
			else {
				entranceTypeArray[1] = EntranceType.EXEC_START_DAY;
				
				entranceTypeArray[2] = EntranceType.EXEC_START_HOUR;
				
				entranceTypeArray[3] = EntranceType.EXEC_TIME;
				
				entranceTypeArray[4] = EntranceType.SOURCE_TYPE;
				lastSourceType = Integer.parseInt(args[4]);
				
				if(lastSourceType < 1 || lastSourceType > 2)
					entranceTypeArray = null;
				else {
					for(int i=5; i<args.length; i++) {
						entranceTypeArray[i] = expectedType;
						
						if(expectedType == EntranceType.SOURCE_TYPE)
							lastSourceType = Integer.parseInt(args[i]);
						
						rightPlaceToFinish = false;
						
						if(expectedType == EntranceType.SOURCE_NAME) {
							if(lastSourceType == 1) // Historical
								expectedType = EntranceType.KEY_NAME;
							else if(lastSourceType == 2) { // RealTime
								expectedType = EntranceType.SOURCE_TYPE;
								rightPlaceToFinish = true;
							}
							else {
								entranceTypeArray = null;
								break;
							}
						}
						else if(expectedType == EntranceType.KEY_NAME) {
							if(i+1<args.length && !isInt(args[i+1]))
								expectedType = EntranceType.KEY_NAME;
							else {
								expectedType = EntranceType.SOURCE_TYPE;
								rightPlaceToFinish = true;
							}
						}
						else if(expectedType == EntranceType.SOURCE_TYPE) {
							expectedType = EntranceType.SOURCE_NAME;
						}
					}
					if(!rightPlaceToFinish)
						entranceTypeArray = null;
				}
			}
		}
		
		
		return entranceTypeArray;
	}
	
	/**
	 * @param args (config_name exec_start_day exec_start_hour exec_time 
	 * 					T source_name key_1 ... key_n 
	 * 					T source_name 
	 * 					...)
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		EntranceType[] entranceTypeArray = parseArgs(args);
		
		if(entranceTypeArray == null) {
			throw new IOException("Incorrect input parameters!");
		}
		
		InputStream is2 = new FileInputStream("/home/vmt/files_map.txt");
		filesMap.load(is2);
		
		SourceManager sourceManager = new ConcreteSourceManager();
		Integer executionTime = null;
		Calendar startTimeCal = Calendar.getInstance();
		
		for(int i=0; i<args.length; i++) {
			
			if(entranceTypeArray[i] == EntranceType.CONFIG_NAME) {
				org.microcloud.manager.logger.MyLogger.newInstance(args[i]);
				org.microcloud.manager.logger.MyLogger.getInstance().logm("StreamMineOperatorPlacerMultiRunApp " + Arrays.toString(args));
				
//				InputStream is = StreamMineOperatorPlacerMultiRunApp.class.
//						getResourceAsStream("config/"+args[i]+".properties");
//				Properties prop = new Properties();
//				prop.load(is);
//
//				Factory.createFactory(prop);
			}
			else if(entranceTypeArray[i] == EntranceType.EXEC_START_DAY) {
				int dayOfMonth = Integer.parseInt(args[i]);
				if(dayOfMonth > 0)
					startTimeCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			}
			else if(entranceTypeArray[i] == EntranceType.EXEC_START_HOUR) {
				int hourOfDay = Integer.parseInt(args[i]);
				if(hourOfDay > -1) {
					startTimeCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
					startTimeCal.set(Calendar.MINUTE, 0);
					startTimeCal.set(Calendar.SECOND, 0);
					startTimeCal.set(Calendar.MILLISECOND, 0);
				}
			}
			else if(entranceTypeArray[i] == EntranceType.EXEC_TIME) {
				executionTime = Integer.parseInt(args[i]);
			}
			else if(entranceTypeArray[i] == EntranceType.SOURCE_TYPE) {
				int sourceType = Integer.parseInt(args[i]);
				
				i++;
				
				String sourceName = args[i];
				
				if(sourceType == 1) { /* historical */
					List<Object> keyObjectsList = new ArrayList<>();
					
					for( i=i+1 ; i<args.length && entranceTypeArray[i] == EntranceType.KEY_NAME ; i++ ) {
						Object key = translateKey(args[i]);
						org.microcloud.manager.logger.MyLogger.getInstance().log("Key name translation: " + args[i] + " -> " + key);
						if(key == null)
							throw new IOException("Incorrect key name " + args[i] + " in parameters!");
						keyObjectsList.add(key);
					}
					i--;
					
					/* Add a historical data source */
					org.microcloud.manager.logger.MyLogger.getInstance().log("sourceManager.useHistoricalDataSource(" + sourceName + ", " + keyObjectsList.size() + ");");
					sourceManager.useHistoricalDataSource(sourceName, keyObjectsList);
				}
				else {
					/* Add a real time data source */
					org.microcloud.manager.logger.MyLogger.getInstance().log("sourceManager.useRealTimeDataSource(" + sourceName + ");");
					sourceManager.useRealTimeDataSource(sourceName);
				}
			}
			
		}
		
		/* Run placement */
		List<SolutionGraphDoneHost> solutionsList = new ArrayList<>();
		ClientQuery clientQuery = new ClientQuery(executionTime, null, WorkerAlgorithmType.WORD_COUNT, startTimeCal.getTime());
		
		InputStream is;
		Properties prop;
		
		Factory.createFactory();
		
		String [] propertiesNamesArray /*= null;
		if(args[0].contains("part1"))
			propertiesNamesArray*/ = new String[]{
				/*"defgreedy",
				"defgreedy",
				"defgreedy",
				"defgreedyp",*/
				/*"defallinone",
				"defsimplexk",
				/*"defsimplexkdhalf",
				"defsimplexkdmax",/*};
		else if(args[0].contains("part2"))
			propertiesNamesArray = new String[]{
				"defsimplexkt",
				"defsimplexktdquater",*/
				"defsimplexktdhalf",
				"defsimplexktdmax",
				"defsimplexktdmore"};
		/*else
			System.exit(0);*/
		
		org.microcloud.manager.logger.MyLogger.getInstance().log("\n\n >>> Running placements...\n");
		
		for(String propertiesName : propertiesNamesArray) {
			
				org.microcloud.manager.logger.MyLogger.getInstance().logm("[" + propertiesName + "]\n");
		
				is = StreamMineOperatorPlacerMultiRunApp.class.
						getResourceAsStream("config/"+propertiesName+".properties");
				prop = new Properties();
				prop.load(is);

				Factory.changeFactoryProperties(prop);
				
				List<SolutionGraphDoneHost> newSolutionsList = sourceManager.runPlacement(clientQuery);
		
				solutionsList.addAll(
						newSolutionsList);
				
				for(int i=0; i<newSolutionsList.size(); i++) {	
					org.microcloud.manager.logger.MyLogger.getInstance().logm("");
					org.microcloud.manager.logger.MyLogger.getInstance().logm("--- SOLUTION" + i + " ---");
					org.microcloud.manager.logger.MyLogger.getInstance().logm(newSolutionsList.get(i));
					org.microcloud.manager.logger.MyLogger.getInstance().logm("");
					
				}
				
				org.microcloud.manager.logger.MyLogger.getInstance().logm("");
				org.microcloud.manager.logger.MyLogger.getInstance().logm("");
		}
		
		int bestSolutionId = 0;
		for(int i=1; i<solutionsList.size(); i++) {
			if(solutionsList.get(i).getApproximatePrice() < 
					solutionsList.get(bestSolutionId).getApproximatePrice()) {
				bestSolutionId = i;
			}
		}
		
		/* Run solution */
		sourceManager.confirmExecution(bestSolutionId);
		
		org.microcloud.manager.logger.MyLogger.getInstance().close();		
	}

}
