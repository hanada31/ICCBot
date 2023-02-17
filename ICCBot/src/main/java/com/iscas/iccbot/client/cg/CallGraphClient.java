package com.iscas.iccbot.client.cg;

import com.iscas.iccbot.Global;
import com.iscas.iccbot.MyConfig;
import com.iscas.iccbot.analyze.utils.ConstantUtils;
import com.iscas.iccbot.analyze.utils.output.FileUtils;
import com.iscas.iccbot.client.BaseClient;
import com.iscas.iccbot.client.manifest.ManifestClient;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * Analyzer Class
 *
 * @author hanada
 * @version 2.0
 */
@Slf4j
public class CallGraphClient extends BaseClient {

    @Override
    protected void clientAnalyze() {
        if (!MyConfig.getInstance().isManifestAnalyzeFinish()) {
            new ManifestClient().start();
        }

        if (!MyConfig.getInstance().isCallGraphAnalyzeFinish()) {
            CgConstructor cgAnalyzer = new CgConstructor();
            cgAnalyzer.start();
        }
        log.info("Call Graph Construction finished");

        CgModify cgModify = new CgModify();
        cgModify.start();
        log.info("Call Graph Enhancing finished");

        log.info("Successfully analyze with CallGraphClient");
        MyConfig.getInstance().setCallGraphAnalyzeFinish(true);
    }

    @Override
    public void clientOutput() {
        String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
                + File.separator;
        FileUtils.createFolder(summary_app_dir + ConstantUtils.CGFOLDETR);

        // Call graph
        CgClientOutput.writeCG(summary_app_dir + ConstantUtils.CGFOLDETR,
                ConstantUtils.CG, Global.v().getAppModel().getCg());
        CgClientOutput.writeCGToString(summary_app_dir + ConstantUtils.CGFOLDETR,
                Global.v().getAppModel().getAppName() + "_cg.txt", Global.v().getAppModel().getCg());
//        CgClientOutput.writeTopoMethodFile(summary_app_dir + ConstantUtils.CGFOLDETR, ConstantUtils.TOPO, Global.v()
//                .getAppModel().getTopoMethodQueue());
    }

}