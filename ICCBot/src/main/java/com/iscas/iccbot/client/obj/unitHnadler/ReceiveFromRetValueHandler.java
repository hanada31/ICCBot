package com.iscas.iccbot.client.obj.unitHnadler;

import com.iscas.iccbot.analyze.model.analyzeModel.ObjectSummaryModel;
import soot.SootMethod;
import soot.Unit;

public class ReceiveFromRetValueHandler extends UnitHandler {

    @Override
    public void handleSingleObject(ObjectSummaryModel singleObject) {
        singleObject.getReceiveFromFromRetValueList().add(unit);
    }

}
