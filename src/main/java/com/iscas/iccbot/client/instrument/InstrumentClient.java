package com.iscas.iccbot.client.instrument;

import com.iscas.iccbot.Global;
import com.iscas.iccbot.MyConfig;
import com.iscas.iccbot.analyze.utils.ConstantUtils;
import com.iscas.iccbot.analyze.utils.output.FileUtils;
import com.iscas.iccbot.client.BaseClient;
import com.iscas.iccbot.client.cg.CallGraphClient;
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
public class InstrumentClient extends BaseClient {
    @Override
    protected void clientAnalyze() {
        if (!MyConfig.getInstance().isManifestAnalyzeFinish()) {
            new ManifestClient().start();
        }

        if (!MyConfig.getInstance().isCallGraphAnalyzeFinish()) {
            new CallGraphClient().start();
        }
        InstrumentAnalyzer analyzer = new InstrumentAnalyzer();
        analyzer.start();
        log.info("Successfully analyze with InstrumentClientClient");
    }

    @Override
    public void clientOutput() {
        String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
                + File.separator;
        FileUtils.createFolder(summary_app_dir + ConstantUtils.INSTRUFOLDER);
        /** call graph **/
        InstrumentClientOutput.writeInstrumentFile(summary_app_dir + ConstantUtils.INSTRUFOLDER,
                ConstantUtils.INSTRUFILE);
        String instrumentedApkPath = summary_app_dir + ConstantUtils.INSTRUFOLDER
                + Global.v().getAppModel().getAppName() + ".apk";
        System.out.println("sign Apk.");
        InstrumentClientOutput.signApk(instrumentedApkPath);
    }

}