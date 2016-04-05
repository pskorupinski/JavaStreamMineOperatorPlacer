package org.microcloud.manager.core.model.key;

import org.microcloud.manager.core.model.datasource.DataSource;

public abstract class HistoricalKey extends Key {
	
	public HistoricalKey(DataSource dataSource, int size) {
		super(dataSource,size);
	}
	
}
