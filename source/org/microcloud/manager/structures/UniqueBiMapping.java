package org.microcloud.manager.structures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
public class UniqueBiMapping<Row, Column> {
	
/////////////////////////////////////////////////////
// FIELDS
/////////////////////////////////////////////////////	
	
	private UniqueArrayList<Row> rowsList;
	private UniqueArrayList<Column> columnsList;
	private ArrayList<ArrayList<Boolean>> mappingTable;
	
/////////////////////////////////////////////////////
// CONSTRUCTORS
/////////////////////////////////////////////////////
	
	private void init() {
		rowsList = new UniqueArrayList<>();
		columnsList = new UniqueArrayList<>();
		mappingTable = new ArrayList<>();		
	}
	
	public UniqueBiMapping() {
		init();
	}
	
	public UniqueBiMapping(Set<Row> rows, Set<Column> columns) {
		init();
		
		if(rows != null)
			defineRows(new ArrayList<>(rows));
		if(columns != null)
			defineColumns(new ArrayList<>(columns));
	}	
	
	public UniqueBiMapping(List<Row> rows, List<Column> columns) {
		init();
		
		if(rows != null)
			defineRows(rows);
		if(columns != null)
			defineColumns(columns);
	}	
	
/////////////////////////////////////////////////////
// PUBLIC METHODS
/////////////////////////////////////////////////////
	
	public Set<Integer> getRowIndexesOfColIndex(int index) {
		Set<Integer> rows = new HashSet<>();
		
		for(int i=0; i<mappingTable.size(); i++) {
			ArrayList<Boolean> row = mappingTable.get(i);
			if(row.get(index) == true)
				rows.add(i);
		}
		
		return rows;
	}

	public Set<Integer> getColIndexesOfRowIndex(int index) {
		Set<Integer> columns = new HashSet<>();
		
		ArrayList<Boolean> row = mappingTable.get(index);
		
		for(int i = 0; i<row.size(); i++) {
			if(row.get(i) == true)
				columns.add(i);
		}
		
		return columns;
	}
	
	public Set<Row> getRowsOfColIndex(int index) {
		Set<Row> rows = new HashSet<>();
		
		for(int i=0; i<mappingTable.size(); i++) {
			ArrayList<Boolean> row = mappingTable.get(i);
			if(row.get(index) == true)
				rows.add(rowsList.get(i));
		}
		
		return rows;
	}
	
	public Set<Column> getColumnsOfRowIndex(int index) {
		Set<Column> columns = new HashSet<>();
		
		ArrayList<Boolean> row = mappingTable.get(index);
		
		for(int i = 0; i<row.size(); i++) {
			if(row.get(i) == true)
				columns.add(columnsList.get(i));
		}
		
		return columns;
	}
	
	public Set<Column> getRowsOfColumn(Column column) {
		int columnIndex = columnsList.indexOf(column);
		return getColumnsOfRowIndex(columnIndex);
	}
	
	public Set<Column> getColumnsOfRow(Row row) {
		int rowIndex = rowsList.indexOf(row);
		return getColumnsOfRowIndex(rowIndex);
	}
	
	public Set<Row> getRows() {
		return rowsList.get();
	}
	
	public Set<Column> getColumns() {
		return columnsList.get();
	}	
	
	public Row getRow(int index) {
		return rowsList.get(index);
	}
	
	public Column getColumn(int index) {
		return columnsList.get(index);
	}
	
	public int getRowsNumber() {
		return rowsList.size();
	}
	
	public int getColumnsNumber() {
		return columnsList.size();
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
			boolean isNew = columnsList.add(column);
			int columnIndex;
			
			if(isNew) {
				insertMappingColumn();
				columnIndex = columnsList.size()-1;
			}
			else
				columnIndex = columnsList.indexOf(column);
			
			colIndexes.add(columnIndex);
		}
		
		/* find Rows */
		for(Row row : rows) {
			boolean isNew = rowsList.add(row);
			int rowIndex;
			
			if(isNew) {
				insertMappingRow();
				rowIndex = rowsList.size()-1;
			}
			else
				rowIndex = rowsList.indexOf(row);
			
			rowIndexes.add(rowIndex);
		}
		
		try {
			checkCorrectnessOfStructure();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		/* set mapping Row<->Column */
		for(int rowIndex : rowIndexes) {
			for(int columnIndex : colIndexes) {
				mappingTable.get(rowIndex).set(columnIndex, true);
			}
		}
		
		return true;
	}
	
	/**
	 * Define columns
	 * @param columns
	 */
	public synchronized boolean defineColumns(List<Column> columns) {
		
		for(Column col : columns) {
			this.columnsList.add(col);
			insertMappingColumn();
		}
		
		return true;
	}
	
	/**
	 * Define rows
	 * @param rows
	 */
	public synchronized boolean defineRows(List<Row> rows) {
		
		for(Row row : rows) {
			this.rowsList.add(row);
			insertMappingRow();
		}
		
		return true;
	}	
	
	public synchronized boolean removeRow(int index) {
		
		rowsList.remove(index);
		//columnsList.remove(index);
		mappingTable.remove(index);
		
		return true;
	}
	
/////////////////////////////////////////////////////
// PRIVATE METHODS
/////////////////////////////////////////////////////
	
	private void checkCorrectnessOfStructure() throws Exception {
		boolean bCorrect = true;
		
		int rowsNumber = rowsList.size();
		if( mappingTable.size() != rowsNumber )
			bCorrect = false;
		
		int colsNumber = columnsList.size();
		for( List<Boolean> colsList : mappingTable ) {
			if( colsList.size() != colsNumber ) {
				bCorrect = false;
				break;
			}
		}
		
		if(bCorrect == false)
			throw new Exception("Fatal Error! Something incorrect with UniqueBiMapping structure!");
	}

	/**
	 * Inserts a new mapping row with a number of fields equal to columnsList size
	 */
	private void insertMappingRow() {
		ArrayList<Boolean> mappingCols = new ArrayList<>();
		for(int i=0; i<columnsList.size();i++) {
			mappingCols.add(new Boolean(false));
		}
		
		mappingTable.add(mappingCols);
	}
	
	/**
	 * Inserts a new field into every row as a new column comes
	 */
	private void insertMappingColumn() {
		for(ArrayList<Boolean> colList : mappingTable) {
			colList.add(new Boolean(false));
		}
	}
	
	private boolean checkColumnExistence(Column column) {
		if( this.columnsList.indexOf(column) == -1)
			return false;
		else
			return true;
	}
	
	
	@Override
	public String toString() {
		String str = "";
		
		int colNum = columnsList.size();
		
		for(int i=0; i<colNum; i++) {
			str += columnsList.get(i);
			str += " ---> ";
			
			Set<Row> rowsSet = getRowsOfColIndex(i);
			for(Row row : rowsSet) {
				str += row + ", ";
			}
			
			str += "\n";
		}
		
		return str;
	}
	
}
