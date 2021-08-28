package main.java.client.obj.unitHnadler.ictg;

import main.java.analyze.model.analyzeModel.SingleObjectModel;
import main.java.client.obj.model.ictg.SingleIntentModel;
import main.java.client.obj.unitHnadler.UnitHandler;

public class ReceiveFromOutHandler extends UnitHandler {

	@Override
	public void handleSingleObject(SingleObjectModel singleObject) {
		((SingleIntentModel) singleObject).getReceiveFromOutList().add(unit);
	}

}
