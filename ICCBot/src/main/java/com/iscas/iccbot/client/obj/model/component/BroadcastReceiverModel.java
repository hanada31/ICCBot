package com.iscas.iccbot.client.obj.model.component;

import com.iscas.iccbot.analyze.model.analyzeModel.AppModel;
import com.iscas.iccbot.analyze.utils.ConstantUtils;

public class BroadcastReceiverModel extends ComponentModel {
    private static final long serialVersionUID = 3L;

    public BroadcastReceiverModel(AppModel appModel) {
        super(appModel);
        type = "r";
    }

    @Override
    public String getComponentType() {
        return ConstantUtils.RECEIVER;
    }

}
