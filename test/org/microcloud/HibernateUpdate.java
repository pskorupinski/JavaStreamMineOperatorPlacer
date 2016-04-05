package org.microcloud;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.persistence.objectsloader.HostDao;
import org.microcloud.manager.persistence.objectsloader.MicroCloudDao;

public class HibernateUpdate {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		HostDao dao = new HostDao();
		dao.findAll();
	}

}
