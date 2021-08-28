package main.java.client.obj.unitHnadler;

import main.java.analyze.model.analyzeModel.SingleObjectModel;

public class CreateHandler extends UnitHandler {

	@Override
	public void handleSingleObject(SingleObjectModel singleObject) {

		singleObject.getCreateList().add(unit);
	}

}
