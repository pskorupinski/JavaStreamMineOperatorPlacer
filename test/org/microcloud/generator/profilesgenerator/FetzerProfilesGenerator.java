package org.microcloud.generator.profilesgenerator;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.model.datacenter.MicroCloudProfile;
import org.microcloud.manager.core.model.datacenter.MicroCloudProfileNode;
import org.microcloud.manager.persistence.PersistenceFactory;
import org.microcloud.manager.persistence.objectsloader.AbstractDao;
import org.microcloud.manager.persistence.objectsloader.MicroCloudDao;
import org.microcloud.manager.persistence.objectsloader.MicroCloudProfileDao;


public class FetzerProfilesGenerator {

	private static final SortedMap<Integer,Integer> profileOption1;
	private static final SortedMap<Integer,Integer> profileOption2;
	private static final Integer outInBandPrice;
	
	static {
		SortedMap<Integer,Integer> p1 = new TreeMap<Integer, Integer>();
		SortedMap<Integer,Integer> p2 = new TreeMap<Integer, Integer>();
		
		p1.put( 0,  40);	/* p1 */
		p1.put( 9,  80);
		p1.put(10, 110);
		p1.put(11, 120);
		p1.put(13, 110);
		p1.put(14, 100);
		p1.put(15,  90);
		p1.put(16,  60);
		p1.put(17,  40);
		p2.put( 0, 500);	/* p2 */
		
		profileOption1 = p1;
		profileOption2 = p2;
		outInBandPrice = 10;
	}
	
	public static void main(String[] args) {
		Integer weeksToGenerate;
		Integer microcloudsNumber;
		
		/* PRECONDITIONS */
		
		if(args.length < 2) {
			throw new InvalidParameterException("Not enough parameters!");
		}
		
		try {
			weeksToGenerate = Integer.parseInt(args[0]);
			microcloudsNumber = Integer.parseInt(args[1]);
		} catch(NumberFormatException e) {
			throw new InvalidParameterException("Parameters should be of types: int int ");
		}
		
		/* CORE */
		
		Random random = new Random();
		final Double profile1Prob = 0.65;
		final Integer nextIntMax = 3;

		final Integer daysInWeek = 7;
		final Integer minutesInWeek = daysInWeek * 24 * 60;
		final Long msInHour = 60 * 60 * 1000L;
		final Long msInDay  = 24 * msInHour;
		final Long msInWeek  = 7 * msInDay;
		
		Calendar calendarPointer = Calendar.getInstance();
		int weekOfYear = calendarPointer.get(Calendar.WEEK_OF_YEAR);
		int year = calendarPointer.get(Calendar.YEAR);

		calendarPointer.clear();
		calendarPointer.set(Calendar.WEEK_OF_YEAR, weekOfYear);
		calendarPointer.set(Calendar.YEAR, year);
		
		MicroCloudDao microCloudDao = new MicroCloudDao();
//		MicroCloudProfileDao microCloudProfileDao = new MicroCloudProfileDao();

//		Session session = null;
//		SessionFactory sessionFactory = PersistenceFactory.getSessionFactory();
//		session = sessionFactory.openSession();
//		
//		Transaction tx = session.beginTransaction();
		
		/* for every microcloud */
		for(int j=1; j<=microcloudsNumber; j++) {
			
			MicroCloud microCloud = microCloudDao.find(j);
			
			MicroCloudProfile microCloudProfile = new MicroCloudProfile();
			Set<MicroCloudProfileNode> microCloudProfileNodes = new HashSet<>();

			@SuppressWarnings("unchecked")
			Map<Integer,Integer> [] chosenMaps = (Map<Integer,Integer>[]) new Map[daysInWeek];				
			
			/* add nodes for this day for that microcloud for every of the weeks to the database */
			Calendar calendarWeeksIterator = (Calendar) calendarPointer.clone();	
			
			/* for every day in week */
			for(int i=0; i<daysInWeek; i++) {
				/* draw a profile */
				int drawnNumber = random.nextInt(nextIntMax);
				if(drawnNumber < profile1Prob * nextIntMax)
					chosenMaps[i] = profileOption1;
				else
					chosenMaps[i] = profileOption2;
			}
			
			for(int k=0; k<weeksToGenerate; k++) {
				Date periodStart = calendarWeeksIterator.getTime();
				
				for(Map<Integer,Integer> mapForDay : chosenMaps) {
					
					periodStart = new Date(periodStart.getTime() + msInDay);
					
					for(Map.Entry<Integer, Integer> entryForHour : mapForDay.entrySet()) {
						Date time = new Date(periodStart.getTime() + entryForHour.getKey()*msInHour);
						Integer price = entryForHour.getValue();

						/* Insert to database */
						MicroCloudProfileNode profileNode = new MicroCloudProfileNode();
						profileNode.setInPrice(outInBandPrice);
						profileNode.setOutPrice(outInBandPrice);
						profileNode.setTime(time);
						profileNode.setUsagePrice(price);
						profileNode.setDataCenterProfile(microCloudProfile);
						
						microCloudProfileNodes.add(profileNode);
						
						org.microcloud.manager.logger.MyLogger.getInstance().log("MicroCloud_" + j + ": " + time + " - " + price);
					}
					
				}
				calendarWeeksIterator.add(Calendar.WEEK_OF_YEAR, 1);
			}
			
			microCloudProfile.setName("PROFILE"+j);
			microCloudProfile.setDataCenterProfileNodes(microCloudProfileNodes);
			
			microCloud.setDataCenterProfile(microCloudProfile);
			
//			microCloudProfileDao.create(microCloudProfile);
			microCloudDao.update(microCloud);
		}
		
	}
	
}
