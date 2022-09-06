package com.iscas.iccbot.client.obj.checker;

import com.iscas.iccbot.MyConfig;
import com.iscas.iccbot.client.BaseClient;
import com.iscas.iccbot.client.manifest.ManifestClient;
import com.iscas.iccbot.client.soot.IROutputClient;
import com.iscas.iccbot.client.statistic.model.StatisticResult;
import org.dom4j.DocumentException;

import java.io.IOException;

public class MisEACheckerClient extends BaseClient {

    @Override
    protected void clientAnalyze() {
        result = new StatisticResult();

        if (!MyConfig.getInstance().isManifestAnalyzeFinish()) {
            new ManifestClient().start();
        }
        if (MyConfig.getInstance().isWriteSootOutput()) {
            new IROutputClient().start();
        }
        MisEAAnalysis misEaAnalyzer = new MisEAAnalysis();
        misEaAnalyzer.start();

        System.out.println("Successfully analyze with MisEACheckerClient.");
    }

    @Override
    public void clientOutput() throws IOException, DocumentException {

    }

}
