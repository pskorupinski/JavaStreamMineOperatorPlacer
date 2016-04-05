package org.microcloud.manager.structures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class UniqueArrayList<E> {
	
	private HashSet<E> hashSet;
	private ArrayList<E> arrayList;
	private final int maxSize;
	private final boolean sizeLimited;
	
	public UniqueArrayList() {
		hashSet = new HashSet<>();
		arrayList = new ArrayList<>();
		this.maxSize = -1;
		this.sizeLimited = false;
	}
	
	public UniqueArrayList(int maxSize) {
		hashSet = new HashSet<>();
		arrayList = new ArrayList<>();
		this.maxSize = maxSize;
		this.sizeLimited = true;
	}
	
	public boolean add(E e) {
		if(sizeLimited && arrayList.size() == maxSize)
			throw new IndexOutOfBoundsException("There cannot be more elements in list");
		
		boolean newEl = hashSet.add(e);
		
		if(newEl)
			return arrayList.add(e);
		else
			return newEl;
	}
	
	public E get(int index) {
		return arrayList.get(index);
	}
	
	public int indexOf(Object o) {
		return arrayList.indexOf(o);
	}
	
	public int addAndGetIndex(E e) {
		add(e);
		return indexOf(e);
	}
	
	public int size() {
		return arrayList.size();
	}
	
	public Set<E> get() {
		return hashSet;
	}
	
	public void remove(int index) {
		E e = arrayList.get(index);
		hashSet.remove(e);
		arrayList.remove(index);
	}
	
}
