package com.iscas.iccbot;

import com.iscas.iccbot.analyze.model.analyzeModel.AppModel;

public abstract class Analyzer {
    public AppModel appModel;

    public Analyzer() {
        this.appModel = Global.v().getAppModel();
    }

    public abstract void analyze();
}
