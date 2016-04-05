package org.microcloud.manager.core.model.key;

import java.util.List;

public interface DivisibleKeyInterface {
	int getNumberOfParts();
	List<Key> divide(List<Integer> numberOfPartsList);
}
