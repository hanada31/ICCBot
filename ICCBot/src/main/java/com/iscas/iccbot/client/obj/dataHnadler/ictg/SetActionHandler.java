package com.iscas.iccbot.client.obj.dataHnadler.ictg;

import com.iscas.iccbot.client.obj.dataHnadler.DataHandler;
import com.iscas.iccbot.client.obj.model.ctg.IntentSummaryModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetActionHandler extends DataHandler {

    @Override
    public void handleData(IntentSummaryModel intentSummary, String key, Set<String> dataSet) {
        Set<String> newDataSet = new HashSet<String>();
        for (String data : dataSet)
            newDataSet.add(data.replace("\"", ""));
        intentSummary.addSetActionValueList(new ArrayList<>(newDataSet));
        return;
    }

}
