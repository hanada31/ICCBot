package com.iscas.iccbot.client.soot;

import com.iscas.iccbot.MyConfig;
import com.iscas.iccbot.client.BaseClient;
import soot.PackManager;

/**
 * Analyzer Class
 *
 * @author hanada
 * @version 2.0
 */
public class IROutputClient extends BaseClient {

    @Override
    protected void clientAnalyze() {
        if (!MyConfig.getInstance().isSootAnalyzeFinish()) {
            SootAnalyzer sootAnalyzer = new SootAnalyzer();
            sootAnalyzer.analyze();
        }

        System.out.println("Successfully analyze with IROutputClient.");
    }

    @Override
    public void clientOutput() {
        PackManager.v().writeOutput();
    }

}