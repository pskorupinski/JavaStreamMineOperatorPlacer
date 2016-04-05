package org.microcloud.generator.profilesgenerator;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ProfilesGeneratorApp {
	
	static class PriceProbPair {
		protected Double price;
		protected Double probability;
		
		public PriceProbPair(Double price, Double probability) {
			this.price = price;
			this.probability = probability;
		}
	}

	public static void main(String[] args)
	{
		Integer changesPeriod;
		Integer weeksToGenerate;
		List<PriceProbPair> pricesProbs = new ArrayList<>();
		
		/* PRECONDITIONS */
		
		if(args.length < 4) {
			throw new InvalidParameterException("Not enough parameters!");
		}
		
		try {
			changesPeriod = Integer.parseInt(args[0]);
			weeksToGenerate = Integer.parseInt(args[1]);
			
			Double sum = 0.0;
			for(int i=2; i<args.length; i+=2) {
				Double price = Double.parseDouble(args[i]);
				Double currentProb = Double.parseDouble(args[i+1]);
				Double probability = sum + currentProb;
				sum = probability;
				pricesProbs.add(new PriceProbPair(price,probability));
			}
			
			if (sum != 1.0)
				throw new InvalidParameterException("Invalid probabilities sum");
			
		} catch(NumberFormatException e) {
			throw new InvalidParameterException("Parameters should be of types: int int double double ... ");
		}
		
		/* CORE */
		
		Long msInWeek = 7 * 24 * 60 * 60 * 1000L;
		Integer minutesInWeek = 7 * 24 * 60;
		
		Calendar calendarPointer = Calendar.getInstance();
		int weekOfYear = calendarPointer.get(Calendar.WEEK_OF_YEAR);
		int year = calendarPointer.get(Calendar.YEAR);

		calendarPointer.clear();
		calendarPointer.set(Calendar.WEEK_OF_YEAR, weekOfYear);
		calendarPointer.set(Calendar.YEAR, year);
		
		Integer iterations = (minutesInWeek / changesPeriod);
		
		Random random = new Random();
		
		for(int i=0; i<iterations; i++) {
			
			Double randDouble = random.nextDouble();
			
			Double priceDuringPeriod = 0.0;
			
			for(PriceProbPair priceProb : pricesProbs) {
				if(priceProb.probability >= randDouble) {
					priceDuringPeriod = priceProb.price;
					break;
				}
			}
			
			Calendar calendarWeeksIterator = (Calendar) calendarPointer.clone();
			
			for(int j=0; j<weeksToGenerate; j++) {
				Date periodStart = calendarWeeksIterator.getTime();
				
				// TODO Insert to database
				org.microcloud.manager.logger.MyLogger.getInstance().log(periodStart + ": " + priceDuringPeriod);
				
				calendarWeeksIterator.add(Calendar.WEEK_OF_YEAR, 1);
			}
			
			calendarPointer.add(Calendar.MINUTE, changesPeriod);
		}
		
	}	
	
}
