package main.java.client.obj.unitHnadler;

import main.java.analyze.model.analyzeModel.SingleObjectModel;

public class MethodReturnHandler extends UnitHandler {

	@Override
	public void handleSingleObject(SingleObjectModel singleObject) {
		singleObject.setFinishFlag(true);
	}

}
