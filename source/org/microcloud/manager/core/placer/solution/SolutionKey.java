package org.microcloud.manager.core.placer.solution;

import java.util.HashSet;
import java.util.Set;

import org.microcloud.manager.core.model.key.Key;

public class SolutionKey extends OrderableSolutionElement {
	private Key key;
	private Set<SolutionSource> sourcesSet = new HashSet<>();

	public SolutionKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}
	
	public void addSolutionSource(SolutionSource source) {
		sourcesSet.add(source);
	}
	
	public Set<SolutionSource> getSourcesSet() {
		return sourcesSet;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		
		if(obj instanceof SolutionKey) {
			SolutionKey other = (SolutionKey) obj;
			
			if(this.key.equals(other.key))
				isEqual = true;
		}
		
		return isEqual;
	}
	
	@Override
	public String toString() {
		return super.toString() + this.key;
	}
	
}
