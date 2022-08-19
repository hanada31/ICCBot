package com.iscas.iccbot.client.obj.unitHnadler.ictg;

import com.iscas.iccbot.analyze.model.analyzeModel.ObjectSummaryModel;
import com.iscas.iccbot.client.obj.model.ctg.IntentSummaryModel;
import com.iscas.iccbot.client.obj.unitHnadler.UnitHandler;

public class ReceiveFromOutHandler extends UnitHandler {

    @Override
    public void handleSingleObject(ObjectSummaryModel singleObject) {
        ((IntentSummaryModel) singleObject).getReceiveFromOutList().add(unit);
    }

}
