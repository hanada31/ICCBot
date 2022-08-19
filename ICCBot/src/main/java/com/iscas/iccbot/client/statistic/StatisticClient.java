package com.iscas.iccbot.client.statistic;

import com.iscas.iccbot.Global;
import com.iscas.iccbot.MyConfig;
import com.iscas.iccbot.analyze.utils.ConstantUtils;
import com.iscas.iccbot.analyze.utils.output.FileUtils;
import com.iscas.iccbot.client.BaseClient;
import com.iscas.iccbot.client.obj.target.ctg.CTGClient;
import org.dom4j.DocumentException;

import java.io.File;
import java.io.IOException;

/**
 * Analyzer Class
 *
 * @author hanada
 * @version 2.0
 */
public class StatisticClient extends BaseClient {

    /**
     * analyze logic for single app
     *
     * @return
     */
    @Override
    protected void clientAnalyze() {
        CTGClient client = new CTGClient();
        result = client.getResult();
        client.start();
        System.out.println("Successfully analyze with StatisticClient.");
    }

    @Override
    public void clientOutput() throws IOException, DocumentException {
        String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
                + File.separator;
        FileUtils.createFolder(summary_app_dir + ConstantUtils.STATFOLDETR);

        StatisticClienOutput outer = new StatisticClienOutput(this.result);

        /** statistic **/
        outer.writeSatisticModel(summary_app_dir + ConstantUtils.STATFOLDETR, ConstantUtils.STATISTIC, false);
        outer.writeSatisticModel(MyConfig.getInstance().getResultFolder(), ConstantUtils.STATISTIC, true);
    }

}