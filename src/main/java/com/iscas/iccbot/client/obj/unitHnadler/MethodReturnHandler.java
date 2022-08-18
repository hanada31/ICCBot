package com.iscas.iccbot.client.obj.unitHnadler;

import com.iscas.iccbot.analyze.model.analyzeModel.ObjectSummaryModel;

public class MethodReturnHandler extends UnitHandler {

    @Override
    public void handleSingleObject(ObjectSummaryModel singleObject) {
        singleObject.setFinishFlag(true);
    }

}
