package com.iscas.iccbot.client.related.a3e;

import com.iscas.iccbot.Global;
import com.iscas.iccbot.MyConfig;
import com.iscas.iccbot.analyze.utils.ConstantUtils;
import com.iscas.iccbot.analyze.utils.GraphUtils;
import com.iscas.iccbot.analyze.utils.output.FileUtils;
import com.iscas.iccbot.client.BaseClient;
import com.iscas.iccbot.client.manifest.ManifestClient;
import com.iscas.iccbot.client.related.a3e.model.A3EModel;
import com.iscas.iccbot.client.soot.SootAnalyzer;
import com.iscas.iccbot.client.statistic.model.StatisticResult;
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
public class A3EResultEvaluateClient extends BaseClient {

    /**
     * analyze logic for single app
     *
     * @return
     */
    @Override
    protected void clientAnalyze() {
        result = new StatisticResult();
        if (!MyConfig.getInstance().isSootAnalyzeFinish()) {
            SootAnalyzer analyzer = new SootAnalyzer();
            analyzer.start();
        }
        if (!MyConfig.getInstance().isManifestAnalyzeFinish()) {
            new ManifestClient().start();
        }
        A3EReader a3e = new A3EReader(result);
        a3e.start();

        log.info("Successfully analyze with A3EClient");
    }

    @Override
    public void clientOutput() throws IOException, DocumentException {
        A3EClientOutput outer = new A3EClientOutput(this.result);
        A3EModel model = Global.v().getA3eModel();
        String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
                + File.separator;
        FileUtils.createFolder(summary_app_dir + ConstantUtils.A3EFOLDETR);

        String dotname = Global.v().getAppModel().getAppName() + "_" + ConstantUtils.ATGDOT_A3E;
        A3EClientOutput.writeDotFileofA3E(summary_app_dir + ConstantUtils.A3EFOLDETR, dotname, model.geta3eAtgModel());
        GraphUtils.generateDotFile(summary_app_dir + ConstantUtils.A3EFOLDETR + dotname, "pdf");
        FileUtils.copyFile(model.geta3eFilePath(), summary_app_dir + ConstantUtils.A3EFOLDETR
                + Global.v().getAppModel().getAppName() + ".xml");
        FileUtils.copyFile(model.geta3eFilePath() + ".dot", summary_app_dir + ConstantUtils.A3EFOLDETR + dotname + "_original" + ".dot");
        GraphUtils.generateDotFile(summary_app_dir + ConstantUtils.A3EFOLDETR + dotname + "_original", "pdf");

        /** Intent **/
        outer.writeIntentSummaryModel(summary_app_dir + ConstantUtils.A3EFOLDETR, ConstantUtils.SINGLEOBJECT_ENTRY, true);
        outer.writeIntentSummaryModel(summary_app_dir + ConstantUtils.A3EFOLDETR, ConstantUtils.SINGLEOBJECT_ALL, false);
    }

}