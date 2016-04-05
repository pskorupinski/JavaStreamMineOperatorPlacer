package org.microcloud.manager.core.placer.placement.Simplex.variations.workersnumber;

public class NextNumber {

	public static Integer counter = 0;
	
	/**
	 * @param args
	 */
	public static Integer getNext() {
		counter = (counter % 40) + 1;
		return counter;
	}

}
