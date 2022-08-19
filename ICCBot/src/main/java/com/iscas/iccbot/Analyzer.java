package com.iscas.iccbot;

import com.iscas.iccbot.analyze.model.analyzeModel.AppModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Analyzer {
    public AppModel appModel;

    public Analyzer() {
        this.appModel = Global.v().getAppModel();
    }

    public abstract void analyze();

    public void start() {
        log.info("Start to run analyzer [" + this.getClass().getSimpleName() + "]");
        long startMS = System.currentTimeMillis();
        analyze();
        log.info(String.format("%s took %.2f seconds", this.getClass().getSimpleName(),
                (System.currentTimeMillis() - startMS) / 1000.0));
    }
}
