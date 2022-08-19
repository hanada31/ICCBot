package com.iscas.iccbot.client.related.story;

import com.iscas.iccbot.Global;
import com.iscas.iccbot.MyConfig;
import com.iscas.iccbot.analyze.utils.ConstantUtils;
import com.iscas.iccbot.analyze.utils.GraphUtils;
import com.iscas.iccbot.analyze.utils.output.FileUtils;
import com.iscas.iccbot.client.BaseClient;
import com.iscas.iccbot.client.manifest.ManifestClient;
import com.iscas.iccbot.client.related.story.model.StoryModel;
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
public class StoryResultEvaluateClient extends BaseClient {

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
        StoryReader story = new StoryReader(result);
        story.start();
        log.info("Successfully analyze with StoryClient");
    }

    @Override
    public void clientOutput() throws IOException, DocumentException {
        StoryClientOutput outer = new StoryClientOutput(this.result);
        StoryModel model = Global.v().getStoryModel();
        String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
                + File.separator;
        FileUtils.createFolder(summary_app_dir + ConstantUtils.STORYFOLDETR);

        String dotname = Global.v().getAppModel().getAppName() + "_" + ConstantUtils.ATGDOT_STORY;
        StoryClientOutput.writeDotFile(summary_app_dir + ConstantUtils.STORYFOLDETR, dotname, model.getStoryAtgModelWithoutFrag());
        GraphUtils.generateDotFile(summary_app_dir + ConstantUtils.STORYFOLDETR + dotname, "pdf");
        FileUtils.copyFile(model.getStoryFilePath(), summary_app_dir + ConstantUtils.STORYFOLDETR
                + Global.v().getAppModel().getAppName() + ".txt");


        /** Intent **/
        outer.writeIntentSummaryModel(summary_app_dir + ConstantUtils.STORYFOLDETR, ConstantUtils.SINGLEOBJECT_ENTRY, true);
        outer.writeIntentSummaryModel(summary_app_dir + ConstantUtils.STORYFOLDETR, ConstantUtils.SINGLEOBJECT_ALL, false);
    }

}