package org.microcloud.manager.core.model.key;

import org.microcloud.manager.core.model.datasource.DataSource;

public class OvergrownMongoKey extends MongoKey {

	public OvergrownMongoKey(DataSource dataSource, String key, int firstChunk,
			int lastChunk, int chunkSizeKB) {
		super(dataSource, key, firstChunk, lastChunk, chunkSizeKB*10240);
	}

}
