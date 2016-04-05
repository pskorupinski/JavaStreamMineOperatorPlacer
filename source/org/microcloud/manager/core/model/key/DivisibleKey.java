package org.microcloud.manager.core.model.key;

import java.util.List;

public class DivisibleKey implements DivisibleKeyInterface {

	private int numberOfParts;
	private Key key;
	
	public DivisibleKey(Key key, int numberOfParts) {
		this.numberOfParts = numberOfParts;
		this.key = key;
	}
	
	@Override
	public int getNumberOfParts() {
		return this.numberOfParts;
	}

	@Override
	public List<Key> divide(List<Integer> numberOfPartsList) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
