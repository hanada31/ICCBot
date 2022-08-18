package com.iscas.iccbot.client.obj.model.component;

import com.iscas.iccbot.analyze.model.analyzeModel.AppModel;
import com.iscas.iccbot.analyze.utils.ConstantUtils;

public class ContentProviderModel extends ComponentModel {
    private static final long serialVersionUID = 3L;

    public ContentProviderModel(AppModel appModel) {
        super(appModel);
        type = "p";
    }

    @Override
    public String getComponentType() {
        return ConstantUtils.PROVIDER;
    }
}
