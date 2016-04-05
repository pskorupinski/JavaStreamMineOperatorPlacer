package org.microcloud.manager.core.placer;

import org.microcloud.manager.core.placer.parameterenums.StrategyHostChoice;

public class PlacerParameters {
	
	public static double WorkerHostsPickerParam_SourceRacksPercent = 0.6;
	public static StrategyHostChoice WorkerHostsPickerParam_StrategyHostChoice = StrategyHostChoice.SourcesOblivious;
}
