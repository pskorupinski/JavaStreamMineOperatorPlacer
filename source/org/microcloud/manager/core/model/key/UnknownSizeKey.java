package org.microcloud.manager.core.model.key;

import org.microcloud.manager.core.model.datasource.DataSource;

public class UnknownSizeKey extends Key {

	public UnknownSizeKey(DataSource dataSource) {
		super(dataSource, -1);
	}
}
