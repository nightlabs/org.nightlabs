package org.nightlabs.jdo.moduleregistry;

import java.util.HashMap;
import java.util.Map;

import org.nightlabs.test.jdo.PersistenceCapableTestBase;

public class UpdateHistoryItemTest extends PersistenceCapableTestBase<UpdateHistoryItem> {
	public UpdateHistoryItemTest() {
		super(UpdateHistoryItem.class);
	}
	
	@Override
	protected Map<String, Object> getNamedQueryParameters(String queryName) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("moduleID", "MODULE ID");
		result.put("updateHistoryItemID", "UPDATE HISTORY ITEM ID");
		return result;
	}
}
