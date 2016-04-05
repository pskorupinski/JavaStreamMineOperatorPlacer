package org.microcloud.manager.structures;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.structures.UniqueBiMapping;

public class UniqueBiMappingTest {
	
	UniqueBiMapping<Host, Object> uniqueBiMapping;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void usageTest() {
		// 1. construct
		uniqueBiMapping = new UniqueBiMapping<>();
		
		// 2. define columns
		Set<Object> columns = new HashSet<>();
		columns.add(5.0);
		columns.add(10.0);
		columns.add(15.0);
		
		// 3. 
		Host host1 = new Host();
		host1.setName("www.google.com");
		Host host2 = new Host();
		host2.setName("www.yahoo.com");
		
		Set<Host> hosts = new HashSet<>();
		hosts.add(host1);
		uniqueBiMapping.addColumnToRowsMapping(hosts, new Double(5.0));
		
		hosts = new HashSet<>();
		hosts.add(host2);
		uniqueBiMapping.addColumnToRowsMapping(hosts, new Double(10.0));	
		
		hosts.add(host1);
		uniqueBiMapping.addColumnToRowsMapping(hosts, new Double(15.0));
		
		// 4.
		Set<Integer> colIndexes = uniqueBiMapping.getColIndexesOfRowIndex(0);
		Set<Integer> rowIndexes = uniqueBiMapping.getRowIndexesOfColIndex(2);
		
		for(Integer colInd : colIndexes) {
			System.out.print( uniqueBiMapping.getColumn(colInd) + ", " );
		}
		org.microcloud.manager.logger.MyLogger.getInstance().log();
		
		for(Integer rowInd : rowIndexes) {
			System.out.print( uniqueBiMapping.getRow(rowInd) + ", " );
		}
		org.microcloud.manager.logger.MyLogger.getInstance().log();
	}

//	@Test
//	public void testUniqueBiMapping() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetColIndexesOfRowIndex() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetRowIndexesOfColIndex() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetRow() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetColumn() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testAddColumnToRowMapping() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testAddRowToColumnsMapping() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testAddColumnToRowsMapping() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testAddColumnsToRowsMapping() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDefineColumns() {
//		fail("Not yet implemented");
//	}

}
