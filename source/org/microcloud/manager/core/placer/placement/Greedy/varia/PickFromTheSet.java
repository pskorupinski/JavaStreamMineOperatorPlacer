package org.microcloud.manager.core.placer.placement.Greedy.varia;

import org.microcloud.manager.core.model.datacenter.Host;

public interface PickFromTheSet {

	public Host pickFromTheSet(Object [] domainOfHosts);
}
