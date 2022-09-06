package com.iscas.iccbot.client.obj.model.component;

import com.iscas.iccbot.analyze.model.analyzeModel.AppModel;
import com.iscas.iccbot.analyze.utils.ConstantUtils;

public class ServiceModel extends ComponentModel {
    private static final long serialVersionUID = 3L;

    public ServiceModel(AppModel appModel) {
        super(appModel);
        type = "s";
    }

    @Override
    public String getComponentType() {
        return ConstantUtils.SERVICE;
    }
}
