package main.java.client.obj.unitHnadler.ictg;

import main.java.analyze.model.analyzeModel.SingleObjectModel;
import main.java.client.obj.model.ictg.SingleIntentModel;
import main.java.client.obj.unitHnadler.UnitHandler;

public class SendIntent2UnkownHandler extends UnitHandler {

	SingleIntentModel singleIntent;

	@Override
	public void handleSingleObject(SingleObjectModel singleObject) {
		singleIntent = ((SingleIntentModel) singleObject);
		singleIntent.getSendIntent2ICCList().add(unit);
		singleIntent.setTargetType("");
	}

}
