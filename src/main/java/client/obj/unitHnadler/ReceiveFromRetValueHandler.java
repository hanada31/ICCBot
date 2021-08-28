package main.java.client.obj.unitHnadler;

import main.java.analyze.model.analyzeModel.SingleObjectModel;

public class ReceiveFromRetValueHandler extends UnitHandler {

	@Override
	public void handleSingleObject(SingleObjectModel singleObject) {
		singleObject.getReceiveFromFromRetValueList().add(unit);
	}

}
