package org.microcloud.manager.operations;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class OperationsOnNumbersTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDoublesListToIntegersList() {
		List<Double> doublesList = new ArrayList<>();
		doublesList.add(15.0*1024*1024);
		doublesList.add(1.0*1024*1024);
		doublesList.add(0.0);
		doublesList.add(0.0);
		doublesList.add(0.0);
		OperationsOnNumbers.doublesListToIntegersList(doublesList, 2);
	}

}
