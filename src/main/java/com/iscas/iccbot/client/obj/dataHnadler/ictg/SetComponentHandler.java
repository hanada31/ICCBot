package com.iscas.iccbot.client.obj.dataHnadler.ictg;

import java.util.HashSet;
import java.util.Set;

import com.iscas.iccbot.client.obj.dataHnadler.DataHandler;
import com.iscas.iccbot.client.obj.model.ctg.IntentSummaryModel;

public class SetComponentHandler extends DataHandler {

	@Override
	public void handleData(IntentSummaryModel intentSummary, String key, Set<String> dataSet) {
		Set<String> newDataSet = new HashSet<String>();
		for (String data : dataSet) {
			data = data.replace("/", ".").replace("class L", "").replace(";", "");
			data = data.split("@")[0];
			data = data.replace("new ", "");
			newDataSet.add(data);
		}
		intentSummary.getSetDestinationList().addAll(newDataSet);

	}
}
