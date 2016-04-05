package org.microcloud.manager.structures;

public class BooleansTable {

	boolean [] booleansTable = null;
	int pointer = -1;
	
	public BooleansTable(int size) {
		this.booleansTable = new boolean[size];
	}
	
	public synchronized int getFirstFree() {
		pointer++;
		for(int i=pointer; ; i++) {
			if(booleansTable[i]==false) {
				booleansTable[i] = true;
				pointer = i;
				return i;
			}
			if(i==booleansTable.length-1)
				i = -1;
		}
	}
	
	public synchronized void clear(int id) {
		if(booleansTable.length > id)
			booleansTable[id] = false;
	}
	
}
