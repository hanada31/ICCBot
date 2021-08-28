package main.java.client.obj.dataHnadler.ictg;

import java.util.HashSet;
import java.util.Set;

import main.java.client.obj.dataHnadler.DataHandler;
import main.java.client.obj.model.ictg.SingleIntentModel;

public class GetActionHandler extends DataHandler {

	@Override
	public void handleData(SingleIntentModel singleIntent, String key, Set<String> dataSet) {
		Set<String> newDataSet = new HashSet<String>();
		for (String data : dataSet)
			newDataSet.add(data.replace("\"", ""));
		singleIntent.getGetActionCandidateList().addAll(newDataSet);
		return;
	}

}
