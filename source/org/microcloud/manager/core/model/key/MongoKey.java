package org.microcloud.manager.core.model.key;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.microcloud.manager.core.model.datasource.DataSource;

public class MongoKey extends HistoricalKey implements DivisibleKeyInterface {
	private String key;
	private int firstChunk;
	private int lastChunk;
	private int chunkSizeKB;
	private DivisibleKey divisibleKey;
	
	public MongoKey(DataSource dataSource, String key, int firstChunk, int lastChunk, int chunkSizeKB) {
		super( dataSource, (lastChunk - firstChunk + 1) * chunkSizeKB );
		this.key = key;
		this.firstChunk = firstChunk;
		this.lastChunk = lastChunk;
		this.chunkSizeKB = chunkSizeKB;
		
		this.divisibleKey = new DivisibleKey(this, lastChunk - firstChunk + 1);
	}
	
	public String getKey() {
		return key;
	}
	public int getFirstChunk() {
		return firstChunk;
	}
	public int getLastChunk() {
		return lastChunk;
	}
	public int getChunkSizeKB() {
		return chunkSizeKB;
	}
	
	@Override
	public int getNumberOfParts() {
		return divisibleKey.getNumberOfParts();
	}
	@Override
	public List<Key> divide(List<Integer> numberOfPartsList) {
		List<Key> newKeys = new ArrayList<>();
		
		int firstChunkForNew = firstChunk;		
		for(Integer numberOfParts : numberOfPartsList) {
			if(numberOfParts > 0) {
				MongoKey mongoKey = new MongoKey(getDataSource(), key, firstChunkForNew, 
								firstChunkForNew+numberOfParts-1, chunkSizeKB);
				
				firstChunkForNew += numberOfParts;
				
				newKeys.add(mongoKey);
			}
		}
		
		return newKeys;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		
		if(obj instanceof MongoKey) {
			MongoKey other = (MongoKey) obj;
			
			if(this.key.equals(other.key) 
					&& this.firstChunk == other.firstChunk 
					&& this.lastChunk == other.lastChunk) {
				isEqual = true;
			}	
		}
		
		return isEqual;
	}
	
	@Override
	public String toString() {
		return super.toString() + "type: MongoKey, key: " + this.key + 
				", range: (" + this.firstChunk + "," + this.lastChunk + ") }";
	}
}
