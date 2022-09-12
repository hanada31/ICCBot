package com.iscas.iccbot.client.obj.dataHnadler.ictg;

import com.iscas.iccbot.analyze.utils.StringUtils;
import com.iscas.iccbot.client.obj.dataHnadler.DataHandler;
import com.iscas.iccbot.client.obj.model.component.Flag;
import com.iscas.iccbot.client.obj.model.ctg.IntentSummaryModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SetFlagHandler extends DataHandler {

    @Override
    public void handleData(IntentSummaryModel intentSummary, String key, Set<String> dataSet) {
        Set<String> newDataSet = new HashSet<String>();
        for (String data : dataSet) {
            if (StringUtils.isInteger(data)) {
                Flag flag = new Flag();
                try {
                    newDataSet.add(flag.getFlag(Integer.parseInt(data)));
                } catch (Exception NumberFormatException) {
                    newDataSet.add(data);
                }
            } else {
                newDataSet.add(data);
            }
        }
        intentSummary.addSetFlagsList(new ArrayList<>(newDataSet));
    }
}
