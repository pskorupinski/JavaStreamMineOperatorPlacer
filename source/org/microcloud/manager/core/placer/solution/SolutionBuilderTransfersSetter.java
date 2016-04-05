package org.microcloud.manager.core.placer.solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.microcloud.manager.core.model.datacenter.MicroCloud;

public class SolutionBuilderTransfersSetter extends
		SolutionBuilder<MicroCloud> {

	public void setTranfersEqually() {
		
		for(SolutionKey sk : solutionKeyList) {
			long sizeKB = sk.getKey().getSizeKB();
			Set<SolutionSource> solutionSources = sk.getSourcesSet();
			int sourcesNumber = solutionSources.size();
			
			for(SolutionSource ss : solutionSources) {
				Set<SolutionConnection<?>> outConnections = ss.getOutputConnections();
				int connectionsNumber = outConnections.size();

				double transfer = ((double)sizeKB)/(double)(sourcesNumber*connectionsNumber);
				for(SolutionConnection<?> sc : outConnections) {
					sc.setTransfer(transfer);
				}
			}
		}
		
	}
	
	public void setTranfersChooseOne() {
		
		Random random = new Random();
		
		for(SolutionKey sk : solutionKeyList) {
			long sizeKB = sk.getKey().getSizeKB();
			List<SolutionSource> solutionSources = new ArrayList(sk.getSourcesSet());
			int sourcesNumber = solutionSources.size();
			
			SolutionSource ss = solutionSources.get(random.nextInt(sourcesNumber));
			
			List<SolutionConnection<?>> outConnections = new ArrayList<>(ss.getOutputConnections());
			int connectionsNumber = outConnections.size();

			double transfer = (double)sizeKB;
			SolutionConnection<?> sc = outConnections.get(random.nextInt(connectionsNumber));
			sc.setTransfer(transfer);
		}		
		
	}
	
}
