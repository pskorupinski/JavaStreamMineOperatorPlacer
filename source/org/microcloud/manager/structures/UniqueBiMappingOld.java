package org.microcloud.manager.structures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;

/**
 * 
 * Class of two sets of unique elements that can have references to multiple objects
 * of the second set. It can be imagined as a 2D table of unique rows and columns.
 * 
 * Number of columns and their "definitions" have to be defined before other steps.
 * 
 * @author Pawel Skorupinski
 *
 * @param <Row>
 * @param <Column>
 */
public class UniqueBiMappingOld<Row, Column> {
	
/////////////////////////////////////////////////////
// FIELDS
/////////////////////////////////////////////////////	
	
	private final int tableSize;
	private UniqueArrayList<Row> rowsList;
	private UniqueArrayList<Column> columnsList;
	private ArrayList<boolean[]> mappingTable;
	
/////////////////////////////////////////////////////
// CONSTRUCTORS
/////////////////////////////////////////////////////
	
	public UniqueBiMappingOld(int maxColumns) {
		rowsList = new UniqueArrayList<>();
		columnsList = new UniqueArrayList<>(maxColumns);
		mappingTable = new ArrayList<>();
	
		this.tableSize = maxColumns;
	}
	
/////////////////////////////////////////////////////
// PUBLIC METHODS
/////////////////////////////////////////////////////

	public Set<Integer> getColIndexesOfRowIndex(int index) {
		Set<Integer> columns = new HashSet<>();
		
		boolean[] row = mappingTable.get(index);
		
		for(int i = 0; i<tableSize; i++) {
			if(row[i] == true)
				columns.add(i);
		}
		
		return columns;
	}
	
	public Set<Integer> getRowIndexesOfColIndex(int index) {
		Set<Integer> rows = new HashSet<>();
		
		for(int i=0; i<mappingTable.size(); i++) {
			boolean[] row = mappingTable.get(i);
			if(row[index] == true)
				rows.add(i);
		}
		
		return rows;
	}
	
//	public Set<Row> getRowsOfColIndex(int index) {
//		return 
//	}
	
//	public Set<Column> getColumnsOfRowIndex(int index) {
//		return 
//	}
	
	public Row getRow(int index) {
		return rowsList.get(index);
	}
	
	public Column getColumn(int index) {
		return columnsList.get(index);
	}
	
	public synchronized boolean addColumnToRowMapping(Row row, Column column) {
		HashSet<Row> rows = new HashSet<>();
		rows.add(row);
		
		HashSet<Column> columns = new HashSet<>();
		columns.add(column);
		
		return addColumnsToRowsMapping(rows, columns);
	}
	
	public boolean addRowToColumnsMapping(Row row, Set<Column> columns) {
		HashSet<Row> rows = new HashSet<>();
		rows.add(row);
		
		return addColumnsToRowsMapping(rows, columns);
	}
	
	public boolean addColumnToRowsMapping(Set<Row> rows, Column column) {
		HashSet<Column> columns = new HashSet<>();
		columns.add(column);
		
		return addColumnsToRowsMapping(rows, columns);		
	}	
	
	public boolean addColumnsToRowsMapping(Set<Row> rows, Set<Column> columns) {
				
		ArrayList<Integer> rowIndexes = new ArrayList<>();
		ArrayList<Integer> colIndexes = new ArrayList<>();
		
		/* find Columns */
		for(Column column : columns) {
			int ind = this.columnsList.indexOf(column);
			if(ind == -1)
				return false;
			else
				colIndexes.add(ind);
		}
		
		/* find Rows */
		for(Row row : rows) {
			boolean isNew = rowsList.add(row);
			int rowIndex;
			
			if(isNew) {
				insertMappingRow();
				rowIndex = mappingTable.size()-1;
			}
			else
				rowIndex = rowsList.indexOf(row);
			
			rowIndexes.add(rowIndex);
		}
		
		/* set mapping Row<->Column */
		for(int rowIndex : rowIndexes) {
			for(int columnIndex : colIndexes) {
				mappingTable.get(rowIndex)[columnIndex] = true;
			}
		}
		
		return true;
	}
	
	/**
	 * Define columns, exactly the number of maxColumns
	 * @param columns
	 */
	public synchronized boolean defineColumns(Set<Column> columns) {
		if(columns.size() != tableSize)
			return false;
		
		for(Column col : columns) {
			this.columnsList.add(col);
		}
		
		return true;
	}
	
/////////////////////////////////////////////////////
// PRIVATE METHODS
/////////////////////////////////////////////////////
	
	private void insertMappingRow() {
		boolean [] mappingCols = new boolean[this.tableSize];
		mappingTable.add(mappingCols);
	}
	
	private boolean checkColumnExistence(Column column) {
		if( this.columnsList.indexOf(column) == -1)
			return false;
		else
			return true;
	}
	
}
