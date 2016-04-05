package org.microcloud.manager.core.placer.placement.Greedy.varia;

import java.util.Random;

import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.placement.Simplex.variations.VariationImplCore;

public class RandomPickFromTheSet extends VariationImplCore implements PickFromTheSet {

	public RandomPickFromTheSet(PlacementProblem placementProblem) {
		super(placementProblem);
	}

	@Override
	public Host pickFromTheSet(Object[] domainOfHosts) {
		Random rand = new Random(System.currentTimeMillis());
		/* Pick the random from a set */
		Host chosenHost = (Host) domainOfHosts[rand.nextInt(domainOfHosts.length)];
		return chosenHost;
	}

}
