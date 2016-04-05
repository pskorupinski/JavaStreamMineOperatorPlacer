package org.microcloud.manager;

import org.microcloud.manager.core.streammine.manager.AsyncMicroCloudSMManager;
import org.microcloud.manager.core.streammine.manager.MicroCloudSMManager;
import org.microcloud.manager.core.streammine.manager.PassiveManager;
import org.microcloud.manager.core.streammine.manager.SampleManager;

import streammine3G.GPBManagerBrigde.GPBManagerInterfaceBridge;

public class StreamMineManagerApp {
	
	public static void main(String[] args)
	{
		new GPBManagerInterfaceBridge(new AsyncMicroCloudSMManager());
	}

}
