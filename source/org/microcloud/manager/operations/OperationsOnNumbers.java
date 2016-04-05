package org.microcloud.manager.operations;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class OperationsOnNumbers {

	public static List<Integer> doublesListToIntegersList(List<Double> doublesList, Integer integersSum) {
		List<Integer> integersList = new ArrayList<>();
		
		int setIntegersSum = 0, integersRemaining = 0;
		double doublesSum = OperationsOnNumbers.collectionSum(doublesList);	/* 1 */ 
		double averagePerInteger = doublesSum / (double)integersSum;		/* 2 */
		for(Double d : doublesList) {										/* 3a*/ 
			double intNumberDouble = d / averagePerInteger;
			int intNumber = (int) Math.floor(intNumberDouble);
			integersList.add(intNumber);
			setIntegersSum += intNumber;
		}
		integersRemaining = integersSum - setIntegersSum;					/* 3b*/ 
		for(int i=0; i<integersRemaining; i++) {				
			integersList.set(i, integersList.get(i)+1);
		}				
		
		return integersList;
	}
	
	public static Double collectionSum(Collection<Double> collection) {
		Double sum = new Double(0.0);
		
		for(Double d : collection) {
			sum += d;
		}
		
		return sum;
	}
	
	public static String intToString(int num, int digits) {
	    assert digits > 0 : "Invalid number of digits";

	    // create variable length array of zeros
	    char[] zeros = new char[digits];
	    Arrays.fill(zeros, '0');
	    // format number as String
	    DecimalFormat df = new DecimalFormat(String.valueOf(zeros));

	    return df.format(num);
	}
	
}
