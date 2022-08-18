package com.iscas.iccbot.client.manifest;

import com.iscas.iccbot.Global;
import com.iscas.iccbot.MyConfig;
import com.iscas.iccbot.analyze.utils.ConstantUtils;
import com.iscas.iccbot.analyze.utils.output.FileUtils;
import com.iscas.iccbot.client.BaseClient;
import com.iscas.iccbot.client.soot.SootAnalyzer;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;

import java.io.File;
import java.io.IOException;

/**
 * Analyzer Class
 *
 * @author hanada
 * @version 2.0
 */
@Slf4j
public class ManifestClient extends BaseClient {

    @Override
    protected void clientAnalyze() {
        if (!MyConfig.getInstance().isSootAnalyzeFinish()) {
            SootAnalyzer sootAnalyzer = new SootAnalyzer();
            sootAnalyzer.start();
        }

        long startMS = System.currentTimeMillis();
        ManifestAnalyzer manifestAnalyzer = new ManifestAnalyzer();
        manifestAnalyzer.start();
        log.info(String.format("Successfully analyze with ManifestClient in %.2f seconds",
                (System.currentTimeMillis() - startMS) / 1000.0));
    }

    @Override
    public void clientOutput() throws IOException, DocumentException {
        String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
                + File.separator;
        FileUtils.createFolder(summary_app_dir + ConstantUtils.MANIFOLDETR);

        ManifestClientOutput.writeManifest(summary_app_dir + ConstantUtils.MANIFOLDETR, ConstantUtils.MANIFEST);
        // TODO: component number count
        // String content = Global.v().getAppModel().getAppName() + "\t" +
        // Global.v().getAppModel().getPackageName()
        // + "\t" + Global.v().getAppModel().getComponentMap().size() + "\t"
        // + Global.v().getAppModel().getExportedComponentMap().size() + "\t"
        // + Global.v().getAppModel().getActivityMap().size() + "\n";
        // FileUtils.writeText2File(MyConfig.getInstance().getResultFolder() +
        // "componentNumber.txt", content, true);
    }
}