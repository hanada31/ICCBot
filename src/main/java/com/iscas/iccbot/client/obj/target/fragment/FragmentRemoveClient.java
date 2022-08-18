package com.iscas.iccbot.client.obj.target.fragment;

import com.iscas.iccbot.Global;
import com.iscas.iccbot.MyConfig;
import com.iscas.iccbot.analyze.utils.ConstantUtils;
import com.iscas.iccbot.analyze.utils.SootUtils;
import com.iscas.iccbot.analyze.utils.output.FileUtils;
import com.iscas.iccbot.client.BaseClient;
import com.iscas.iccbot.client.soot.SootAnalyzer;
import com.iscas.iccbot.client.statistic.model.StatisticResult;
import org.dom4j.DocumentException;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Analyzer Class
 *
 * @author hanada
 * @version 2.0
 */
public class FragmentRemoveClient extends BaseClient {

    /**
     * analyze logic for single app
     *
     * @return
     */
    @Override
    protected void clientAnalyze() {
        result = new StatisticResult();
        if (!MyConfig.getInstance().isSootAnalyzeFinish()) {
            SootAnalyzer sootAnalyzer = new SootAnalyzer();
            sootAnalyzer.start();
        }

        String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
                + File.separator + ConstantUtils.FRAGTMPFOLDETR + File.separator;
        FileUtils.createFolder(summary_app_dir);
        String content = "";
        for (SootClass sc : Scene.v().getApplicationClasses()) {
            for (SootMethod sMethod : sc.getMethods()) {
                List<Unit> units = SootUtils.getUnitListFromMethod(sMethod);
                for (Unit u : units) {
                    if (u.toString().contains("app.FragmentTransaction remove(")) {
                        content += sMethod.getSignature() + "\t" + u.toString() + "\n";
                    }

                }
            }
        }
        FileUtils.writeText2File(summary_app_dir + "remove.txt", content, false);
        System.out.println("Successfully analyze with FragmentRemoveClient.");
    }

    @Override
    public void clientOutput() throws IOException, DocumentException {


    }

}