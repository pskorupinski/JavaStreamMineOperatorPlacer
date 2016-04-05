package org.microcloud.manager.core.placer.solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.model.key.DivisibleKeyInterface;
import org.microcloud.manager.core.model.key.Key;
import org.microcloud.manager.core.placer.solution.SolutionBuilder.SolutionSourceKeyPart;

/**
 * Rebuilds a given solution's graph to conform:
 * <ul>
 * 	<li> sources (removes unused) </li>
 * 	<li> keys (redefines the divisible ones, if needed) </li>
 * 	<li> connections (spreads transfers equally)
 * 
 * @author PSkorupinski
 *
 */
public class SolutionNormalizer extends SolutionBuilder<MicroCloud>  {
	
	SolutionGraphFullMC inputGraph;
	
	public SolutionNormalizer(SolutionGraphFullMC inputGraph, Map<SolutionDestination<MicroCloud>,Integer> numbersMap) {
		this.inputGraph = inputGraph;
		
		createSources(inputGraph);
		createDestinations(numbersMap);
		//connectAll();
		setConnectionsWithTransfers(numbersMap);
		redefineKeys();
	}

	/**
	 * Creates sources by copying those that have transfer greater than zero
	 * 
	 * @param inputGraph
	 */
	private void createSources(SolutionGraphFullMC inputGraph) {
		for(SolutionSource ss : inputGraph.sources) {
			if(ss.getTransfer() > 0.0)
				this.createSource(ss.getHost().getHost(), ss.getKey().getKey());
		}	
	}
	
	private void createDestinations(
			Map<SolutionDestination<MicroCloud>, Integer> numbersMap) {
		
		for(Map.Entry<SolutionDestination<MicroCloud>, Integer> entry : numbersMap.entrySet()) {
			if(entry.getValue() > 0) {
				MicroCloud mc = entry.getKey().getDestination();
				this.createDestination(mc);
			}
		}
		
	}
	
	private void setConnectionsWithTransfers(Map<SolutionDestination<MicroCloud>, Integer> numbersMap) {
		
		int workersNo = sumMapValues(numbersMap);
		
		List<SolutionSource> originalSources = inputGraph.getSources();
		
		for(SolutionSource ss : solutionSourceList) {
			int origSSIndex = originalSources.indexOf(ss);
			double sourceDataSize = originalSources.get(origSSIndex).getTransfer();
			
			for(SolutionDestination<MicroCloud> sd : solutionDestinationList) {
				Integer workersInSD = numbersMap.get(sd);
				double fractionOfDataIncomingToMC = (double)workersInSD / (double)workersNo;
				
				createConnectionCore(ss, sd, fractionOfDataIncomingToMC*sourceDataSize);				
			}
		}
	}
	
	private int sumMapValues(Map<SolutionDestination<MicroCloud>, Integer> numbersMap) {
		int sum = 0;
		
		for(Map.Entry<SolutionDestination<MicroCloud>, Integer> entry : numbersMap.entrySet()) {
			sum += entry.getValue();
		}
		
		return sum;
	}
	
	public SolutionGraphFullMC getSolutionGraph() {
		return (SolutionGraphFullMC) getSolutionGraph(GraphConfirmationType.FULL_DESTDC);
	}
	
	/**
	 * Method that analyzes sources of every key.
	 * If there are more with out transfer, then:
	 * <ul>
	 * 	<li> if a key is divisible - splits the key </li>
	 * 	<li> if a key is not divisible - throws exception </li>
	 */
	public void redefineKeys() {
		
		List<SolutionKey> solutionKeyListCopy = new ArrayList<>(this.solutionKeyList);
		
		for(SolutionKey sk : solutionKeyListCopy) {
			Set<SolutionSource> sourcesSet = sk.getSourcesSet();
			List<SolutionSourceKeyPart> keyPartsList = new ArrayList<>();
			Double outputTransferSum = 0.0;
			
			for(SolutionSource ss : sourcesSet) {
				Double outputTransfer = ss.getTransfer();
				if(outputTransfer > 0.0) {
					keyPartsList.add(new SolutionSourceKeyPart(ss, outputTransfer));
					outputTransferSum += outputTransfer;
				}
			}
			
			if(Math.round(outputTransferSum) != sk.getKey().getSizeKB())
				throw new IllegalStateException("transfers for the key counted incorrectly.");
			
			if(keyPartsList.size() == 0)
				throw new IllegalStateException("keys redefinition called when transfers not yet set or failed to be set.");
			else if(keyPartsList.size() == 1);
			else { /* Redefinition needed */
				if( sk.getKey() instanceof DivisibleKeyInterface ) {
					
					org.microcloud.manager.logger.MyLogger.getInstance().log("Redefinition needed on " + sk);
					
					int keyNumberOfParts = ((DivisibleKeyInterface)sk.getKey()).getNumberOfParts();
					int totalChunksNumber = 0, chunksRemaining = 0;
					
					for(SolutionSourceKeyPart activeSS : keyPartsList) {	 
						double chunksNumberDouble = keyNumberOfParts*(activeSS.getTransfer() / outputTransferSum);
						int chunksNumber = (int) Math.floor(chunksNumberDouble);
						activeSS.setNumberOfParts(chunksNumber);
						totalChunksNumber += chunksNumber;
					}
					
					Collections.sort(keyPartsList);
					chunksRemaining = keyNumberOfParts - totalChunksNumber;		
					for(int i=0; i<chunksRemaining; i++) {				
						keyPartsList.get(i).incrementNumberOfParts();
					}
					
					List<Integer> numberOfPartsList = new ArrayList<>();
					for(SolutionSourceKeyPart keyPart : keyPartsList) {
						numberOfPartsList.add(keyPart.getNumberOfParts());
					}
					List<Key> keys = ((DivisibleKeyInterface)sk.getKey()).divide(numberOfPartsList);
					
					/* Remove the old key from the keys list */
					this.solutionKeyList.remove(sk);
					
					/* Define new keys, redefine keys for sources */
					for(int i=0; i<keyPartsList.size(); i++) {
						if(i<keys.size()) {
							SolutionKey newSk = createSolutionKey(keys.get(i));
							SolutionSource ss = keyPartsList.get(i).getSolutionSource();
							SolutionKey oldSk = ss.getKey();
							ss.changeKey(newSk);
							double resizingFactor = newSk.getKey().getSizeKB() / oldSk.getKey().getSizeKB();
							for(SolutionConnection<?> sc : ss.getOutputConnections()) {
								sc.setTransfer(resizingFactor*sc.getTransfer());
							}
						}
						else {
							/* wyeb */
							SolutionSource ss = keyPartsList.get(i).getSolutionSource();
							Set<SolutionConnection<?>> scSet = ss.getOutputConnections();
							this.solutionConnectionList.removeAll(scSet);
							SolutionSourceHost ssh = ss.getHost();
							ssh.getSourcesSet().remove(ss);
							SolutionKey ssk = ss.getKey();
							ssk.getSourcesSet().remove(ss);
							this.solutionSourceList.remove(ss);
						}
					}					
					
				}
				else {
					throw new IllegalStateException("redefinition needed on a key that is non-divisible!");					
				}
				
			}
		}
	}
	
}
