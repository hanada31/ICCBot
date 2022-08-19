package com.iscas.iccbot.client.soot;

import com.iscas.iccbot.MyConfig;
import com.iscas.iccbot.client.BaseClient;
import lombok.extern.slf4j.Slf4j;
import soot.PackManager;

/**
 * Analyzer Class
 *
 * @author hanada
 * @version 2.0
 */
@Slf4j
public class IROutputClient extends BaseClient {

    @Override
    protected void clientAnalyze() {
        if (!MyConfig.getInstance().isSootAnalyzeFinish()) {
            SootAnalyzer sootAnalyzer = new SootAnalyzer();
            sootAnalyzer.start();
        }
        log.info("Successfully analyze with IROutputClient");
        MyConfig.getInstance().setWriteSootOutput(true);
    }

    @Override
    public void clientOutput() {
        PackManager.v().writeOutput();
    }

}