package org.microcloud.manager.core.mapper.mongodb;

/**
 * A class representing a identification for data part in Mongo. 
 * This identification consists of a string key and a first chunk number.
 * 
 */
public class ChunkMapKey implements Comparable<Object> {
	
	private boolean isMin;
	private String minFiles_id;
	private Integer minN;
	
//////////////////////////////////////////////////////////
// CONSTRUCTORS
//////////////////////////////////////////////////////////	
	
	public ChunkMapKey(Object minFiles_id, Object minN) {
		if(minFiles_id.getClass().equals(org.bson.types.MinKey.class)) {
			this.isMin = true;
			this.minFiles_id = "";
			this.minN = -1;
		}		
		else if(minFiles_id.getClass().equals(String.class)) {
			this.isMin = false;
			this.minFiles_id = (String) minFiles_id;
			this.minN = (int) minN;
		}
	}
	
//////////////////////////////////////////////////////////
// GETTERS AND SETTERS
//////////////////////////////////////////////////////////
	
	public String getMinFiles_id() {
		return minFiles_id;
	}

	public int getMinN() {
		return minN;
	}
	
//////////////////////////////////////////////////////////
// METHODS OVERRIDES
//////////////////////////////////////////////////////////

	public boolean equals(Object o) {
		boolean bRet = false;
		
		if(isMin == true);
		else if(o instanceof ChunkMapKey) {
			ChunkMapKey compChunkMapKey = (ChunkMapKey) o;
			if(compChunkMapKey.minFiles_id.equals(this.minFiles_id)) {
				if(compChunkMapKey.minN == this.minN) {
					bRet = true;
				}
			}
		}
		else if(o instanceof String) {
			String minFiles_id = (String) o;
			if(minFiles_id.equals(this.minFiles_id)) {
				bRet = true;
			}
		}
		
		return bRet;
	}

	@Override
	public int compareTo(Object o) {
		int iRet = 0;
		
		if(this.isMin == true) {
			if(o instanceof ChunkMapKey && ((ChunkMapKey) o).isMin==true)
				iRet = 0;
			else
				iRet = -1;
		}
		else if(o instanceof ChunkMapKey) {
			if(((ChunkMapKey) o).isMin == true) {
				iRet = 1;
			}
			else {
				ChunkMapKey compChunkMapKey = (ChunkMapKey) o;
				int compOfStrings = this.minFiles_id.compareTo(compChunkMapKey.minFiles_id);
				
				if(compOfStrings == 0) {
					/* -1 means - chunk numbers doesn't matter in comparing */
					if(compChunkMapKey.minN==-1 || this.minN==-1)
						iRet = 0;
					else {
						int compOfInts = this.minN.compareTo(compChunkMapKey.minN);
						iRet = compOfInts;
					}
				}
				else {
					iRet = compOfStrings;
				}
			}
		}
		else if(o instanceof String) {
			String minFiles_id = (String) o;
			int compOfStrings = this.minFiles_id.compareTo(minFiles_id);
			iRet = compOfStrings;
		}
		else {
			throw new IllegalArgumentException("Only String and ChunkMapKey can be objects of comparison " +
					"in ChunkMapKey.compareTo(o)");
		}
		
		return iRet;
	}
	
	@Override
	public String toString() {
		return this.minFiles_id + ":" + this.minN + "[" + this.isMin + "]";
	}
}
