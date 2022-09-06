package com.iscas.iccbot.client.obj.unitHnadler.ictg;

import com.iscas.iccbot.analyze.model.analyzeModel.ObjectSummaryModel;
import com.iscas.iccbot.analyze.utils.ConstantUtils;
import com.iscas.iccbot.client.obj.model.ctg.IntentSummaryModel;
import com.iscas.iccbot.client.obj.unitHnadler.UnitHandler;

public class SendIntent2ServiceHandler extends UnitHandler {

    IntentSummaryModel intentSummary;

    @Override
    public void handleSingleObject(ObjectSummaryModel singleObject) {
        intentSummary = ((IntentSummaryModel) singleObject);
        intentSummary.getSendIntent2ICCList().add(unit);
        intentSummary.setTargetType(ConstantUtils.SERVICE);
    }

}
