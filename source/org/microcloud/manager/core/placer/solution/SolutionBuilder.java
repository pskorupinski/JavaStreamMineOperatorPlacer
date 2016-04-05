package org.microcloud.manager.core.placer.solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.property.Getter;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.model.key.DivisibleKeyInterface;
import org.microcloud.manager.core.model.key.Key;
import org.microcloud.manager.core.placer.solution.view.OrderNumber;

public class SolutionBuilder<DestinationType> {

	protected SolutionGraph<DestinationType> solutionGraph;
	protected List<SolutionSource> solutionSourceList = new ArrayList<SolutionSource>();
	protected List<SolutionSourceHost> solutionSourceHostList = new ArrayList<SolutionSourceHost>();
	protected List<SolutionConnection<DestinationType>> solutionConnectionList = new ArrayList<SolutionConnection<DestinationType>>();
	protected List<SolutionDestination<DestinationType>> solutionDestinationList = new ArrayList<SolutionDestination<DestinationType>>();
	protected List<SolutionKey> solutionKeyList = new ArrayList<SolutionKey>();
	
	protected List<SolutionMicroCloud> solutionDataCentersList = new ArrayList<>();
	
//////////////////////////////////////////////////////////////////////////
// CONSTRUCTORS
//////////////////////////////////////////////////////////////////////////

	public SolutionBuilder() {}
	
//////////////////////////////////////////////////////////////////////////
// PUBLIC METHODS
//////////////////////////////////////////////////////////////////////////
	
	public synchronized SolutionSource createSource(Host host, Key key) {
		
		SolutionSourceHost sourceHost = createSourceHost(host);
		
		SolutionKey solutionKey = createSolutionKey(key);
		
		SolutionSource source = new SolutionSource(sourceHost, solutionKey);
		
		return checkAddToList(source, solutionSourceList);
	}
	
	public synchronized SolutionDestination<DestinationType> createDestination(DestinationType dest) {

		SolutionMicroCloud solMicroCloud = null;
		if(dest instanceof MicroCloud) {
			solMicroCloud = createDataCenter((MicroCloud) dest);
		}
		else if(dest instanceof Host) {
			solMicroCloud = createDataCenter(((Host)dest).getRack().getMicroCloud());
		}
		
		SolutionDestination<DestinationType> destination = new SolutionDestination<DestinationType>(dest);
		destination.solutionMicroCloud = solMicroCloud;
		
		return checkAddToList(destination, solutionDestinationList);
	}
	
	public SolutionConnection<DestinationType> createConnection(SolutionSource source, SolutionDestination<DestinationType> destination, int transfer) {
		
		// object from another factory!
		if(! solutionSourceList.contains(source))
			return null;
		if(! solutionDestinationList.contains(destination))
			return null;
		
		return createConnectionCore(source, destination, transfer);
	}
	
	public synchronized SolutionConnection<DestinationType> createSourceDestConnection(Host host, Key key, DestinationType dest) {
		
		SolutionSource source = createSource(host, key);
		SolutionDestination<DestinationType> destination = createDestination(dest);
		
		return createConnectionCore(source, destination, -1);
	}
	
	public synchronized void connectAll() {
		for(SolutionSource ss :	solutionSourceList) {
			for(SolutionDestination<DestinationType> sd : solutionDestinationList) {
				createConnectionCore(ss, sd, -1);
			}
		}
	}

//////////////////////////////////////////////////////////////////////////
	
	public boolean sourceHostExists(Host host) {
		SolutionSourceHost sourceHost = new SolutionSourceHost(host);
		return solutionSourceHostList.contains(sourceHost);
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
						SolutionKey newSk = createSolutionKey(keys.get(i));
						SolutionSource ss = keyPartsList.get(i).getSolutionSource();
						ss.changeKey(newSk);
					}					
					
				}
				else {
					throw new IllegalStateException("redefinition needed on a key that is non-divisible!");					
				}
				
			}
		}
	}
	
	class SolutionSourceKeyPart implements Comparable<SolutionSourceKeyPart> {

		SolutionSource ss;
		double transfer;
		int numberOfParts;
		
		public SolutionSourceKeyPart(SolutionSource ss, Double outputTransfer) {
			this.ss = ss;
			this.transfer = outputTransfer;
		}

		public void setNumberOfParts(int chunksNumber) {
			this.numberOfParts = chunksNumber;
		}
		public int getNumberOfParts() {
			return numberOfParts;
		}
		public void incrementNumberOfParts() {
			this.numberOfParts++;
		}

		public SolutionSource getSolutionSource() {
			return ss;
		}
		public Double getTransfer() {
			return this.transfer;
		}

		@Override
		public int compareTo(SolutionSourceKeyPart o) {
			double diff = this.transfer - o.transfer;
			if(diff == 0.0)
				return 0;
			else if(diff > 0.0)
				return 1;
			else
				return -1;
		}
		
	}
	
//////////////////////////////////////////////////////////////////////////
// GETTERS
//////////////////////////////////////////////////////////////////////////
	
	public SolutionGraph<?> getSolutionGraph(GraphConfirmationType graphConfirmationType) {
		SolutionGraph<?> solutionGraph;
		
		assignOrderNumbers(solutionSourceList);
		assignOrderNumbers(solutionDestinationList);
		assignOrderNumbers(solutionKeyList);
		assignOrderNumbers(solutionSourceHostList);
		
		if(graphConfirmationType == GraphConfirmationType.DONE_DESTHOST) {
			List<SolutionDestination<Host>> l1 = new ArrayList(solutionDestinationList);
			List<SolutionConnection<Host>> l2 = new ArrayList(solutionConnectionList);
			solutionGraph = new SolutionGraphDoneHost(solutionSourceList, l1, l2, solutionSourceHostList.size(), solutionKeyList.size());
		}
		else if(graphConfirmationType == GraphConfirmationType.FULL_DESTDC) {
			List<SolutionDestination<MicroCloud>> l1 = new ArrayList(solutionDestinationList);
			List<SolutionConnection<MicroCloud>> l2 = new ArrayList(solutionConnectionList);
			solutionGraph = new SolutionGraphFullMC(solutionSourceList, l1, l2, solutionSourceHostList.size(), solutionKeyList.size());
		}
		else
			solutionGraph = null;
		
		return solutionGraph;
}
	
//////////////////////////////////////////////////////////////////////////
// protected METHODS
//////////////////////////////////////////////////////////////////////////	

	protected synchronized SolutionMicroCloud createDataCenter(MicroCloud dataCenter) {
		
		SolutionMicroCloud solDataCenter = new SolutionMicroCloud(dataCenter);
		
		return checkAddToList(solDataCenter, solutionDataCentersList);
	}
	
	protected synchronized SolutionSourceHost createSourceHost(Host host) {
		
		SolutionMicroCloud microCloud = createDataCenter(host.getRack().getMicroCloud());
		
		SolutionSourceHost sourceHost = new SolutionSourceHost(host);
		sourceHost.solutionMicroCloud = microCloud;
		
		return checkAddToList(sourceHost, solutionSourceHostList);
	}	
	
	protected synchronized SolutionConnection<DestinationType> createConnectionCore(
			SolutionSource source, SolutionDestination<DestinationType> destination, double transfer) {
		
		SolutionConnection<DestinationType> conn 
			= new SolutionConnection<>(source, destination);
		conn.setTransfer(transfer);

		return checkAddToList(conn, solutionConnectionList);
	}
	
	protected synchronized SolutionKey createSolutionKey(Key key) {
		
		SolutionKey solKey = new SolutionKey(key);
		
		return checkAddToList(solKey, solutionKeyList);
	}
	
	protected synchronized <T> T checkAddToList(T t, List<T> l) {
		
		int posInSet = l.indexOf(t);
		if(posInSet < 0) {
			l.add(t);
		}
		else {
			/* change a reference */
			t = l.get(posInSet);
		}
		
		return t;		
	}
	
	protected <T extends OrderNumber> void assignOrderNumbers(List<T> list) {
		int i=0;
		for(OrderNumber o : list) {
			o.setOrderNumber(i);
			i++;
		}
	}
	
}
