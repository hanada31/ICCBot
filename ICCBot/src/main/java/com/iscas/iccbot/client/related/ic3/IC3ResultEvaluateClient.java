package com.iscas.iccbot.client.related.ic3;

import com.iscas.iccbot.Global;
import com.iscas.iccbot.MyConfig;
import com.iscas.iccbot.analyze.utils.ConstantUtils;
import com.iscas.iccbot.analyze.utils.GraphUtils;
import com.iscas.iccbot.analyze.utils.output.FileUtils;
import com.iscas.iccbot.client.BaseClient;
import com.iscas.iccbot.client.manifest.ManifestClient;
import com.iscas.iccbot.client.related.ic3.model.IC3Model;
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
public class IC3ResultEvaluateClient extends BaseClient {

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
        IC3Reader ic3 = new IC3Reader(result);
        ic3.start();
        log.info("Successfully analyze with IC3GraphClient");
    }

    @Override
    public void clientOutput() throws IOException, DocumentException {
        IC3ClientOutput outer = new IC3ClientOutput(this.result);
        IC3Model model = Global.v().getiC3Model();
        String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
                + File.separator;
        FileUtils.createFolder(summary_app_dir + ConstantUtils.IC3FOLDETR);

        String dotname = Global.v().getAppModel().getAppName() + "_" + ConstantUtils.ATGDOT_IC3;
        IC3ClientOutput.writeDotFileofIC3(summary_app_dir + ConstantUtils.IC3FOLDETR, dotname, model.getIC3AtgModel());
        IC3ClientOutput.writeIccLinksConfigFile(summary_app_dir + ConstantUtils.IC3FOLDETR, ConstantUtils.LINKFILE,
                model.getIC3AtgModel());
        GraphUtils.generateDotFile(summary_app_dir + ConstantUtils.IC3FOLDETR + dotname, "pdf");
        FileUtils.copyFile(model.getIC3FilePath(), summary_app_dir + ConstantUtils.IC3FOLDETR
                + Global.v().getAppModel().getAppName() + ".json");

        /** Intent **/
        outer.writeIntentSummaryModel(summary_app_dir + ConstantUtils.IC3FOLDETR, ConstantUtils.SINGLEOBJECT_ENTRY, true);
        outer.writeIntentSummaryModel(summary_app_dir + ConstantUtils.IC3FOLDETR, ConstantUtils.SINGLEOBJECT_ALL, false);
    }

}