package main.java.client.obj.unitHnadler;

import main.java.analyze.model.analyzeModel.SingleObjectModel;

public class ReceiveFromParaHandler extends UnitHandler {

	@Override
	public void handleSingleObject(SingleObjectModel singleObject) {
		singleObject.getReceiveFromParaList().add(unit);
	}

}
