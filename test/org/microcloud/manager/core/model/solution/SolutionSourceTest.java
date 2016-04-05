package org.microcloud.manager.core.model.solution;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.microcloud.manager.core.model.datacenter.MicroCloudAttributes;
import org.microcloud.manager.core.model.datacenter.MicroCloudProfile;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datacenter.HostBusyTimes;
import org.microcloud.manager.core.model.datacenter.LocPoint;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.model.datacenter.Rack;
import org.microcloud.manager.core.model.datasource.DataSource;
import org.microcloud.manager.core.model.key.HistoricalKey;
import org.microcloud.manager.core.model.key.Key;
import org.microcloud.manager.core.model.key.MongoKey;
import org.microcloud.manager.core.placer.solution.SolutionSource;
import org.microcloud.manager.core.placer.solution.SolutionSourceHost;

public class SolutionSourceTest {
	
	SolutionSource solutionSource;

	@Before
	public void setUp() throws Exception {
		
		MicroCloudAttributes dataCenterAttributes = new MicroCloudAttributes();
		dataCenterAttributes.setId(0);
		dataCenterAttributes.setInputBandwidthMBitInt(64);
		dataCenterAttributes.setOutputBandwidthMBitInt(16);
		
		MicroCloudProfile dataCenterProfile = new MicroCloudProfile();
		
		MicroCloud dataCenter = new MicroCloud();
		dataCenter.setConnectionBandInside(256);
		dataCenter.setDataCenterAttributes(dataCenterAttributes);
		dataCenter.setDataCenterProfile(dataCenterProfile);
		dataCenter.setHost("www.example.com/main/");
		dataCenter.setId(0);
		dataCenter.setLocation(new LocPoint());
		dataCenter.setName("dc1");
		
		Rack rack = new Rack();
		rack.setMicroCloud(dataCenter);
		rack.setId(0);
		rack.setName("rack1");
		
		Host host = new Host();
		host.setComputationPowerFactor(1.0);
		host.setHostBusyTimes(new HashSet<HostBusyTimes>());
		host.setId(0);
		host.setName("www.example.com/1/");
		host.setRack(rack);
		host.setDiskReadSpeed(1024);
		
		SolutionSourceHost solutionSourceHost = new SolutionSourceHost(host);
		
		DataSource dataSource = new DataSource();
		
//		Key key = new MongoKey(dataSource, key, 0, 2, 1024);
//		
//		solutionSource = new SolutionSource(host, key);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCountTime() {
		fail("Not yet implemented");
	}

}
