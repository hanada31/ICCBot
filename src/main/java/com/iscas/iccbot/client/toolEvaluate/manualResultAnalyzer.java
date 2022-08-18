package com.iscas.iccbot.client.toolEvaluate;

import com.iscas.iccbot.Analyzer;
import com.iscas.iccbot.Global;
import com.iscas.iccbot.MyConfig;
import com.iscas.iccbot.analyze.utils.ConstantUtils;
import com.iscas.iccbot.analyze.utils.output.FileUtils;
import com.iscas.iccbot.client.obj.model.atg.ATGModel;
import com.iscas.iccbot.client.obj.model.atg.AtgEdge;
import com.iscas.iccbot.client.obj.model.atg.AtgNode;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class manualResultAnalyzer extends Analyzer {
    private String appName;
    private String oracleFolder;
    private String oracleLogPath;
    private String instrumentedLogPath;
    private String instrumentedFolder;

    public manualResultAnalyzer() {
        super();
        this.appName = appModel.getAppName();
        this.oracleFolder = MyConfig.getInstance().getResultFolder() + appName + File.separator
                + ConstantUtils.ORACLEFOLDETR;
        this.oracleLogPath = oracleFolder + appName + ConstantUtils.ORACLEMANU;

        this.instrumentedFolder = MyConfig.getInstance().getResultFolder() + appName + File.separator
                + ConstantUtils.INSTRUFOLDER;
        this.instrumentedLogPath = instrumentedFolder + appName + ConstantUtils.ORACLEMANU;
    }

    @Override
    public void analyze() {
        if (FileUtils.isFileExist(instrumentedLogPath)) {
            FileUtils.copyFile(instrumentedLogPath, oracleLogPath);
        }
        if (FileUtils.isFileExist(oracleLogPath)) {
            ATGModel model = constructModel();
            Global.v().getiCTGModel().setMannualModel(model);
        }
    }

    private ATGModel constructModel() {
        ATGModel model = new ATGModel();
        FileUtils.createFolder(oracleFolder);
        List<String> logs = FileUtils.getListFromFile(oracleLogPath);
        String spliter = " --> ";
        String comment = "##";
        for (String line : logs) {
            if (line.contains(spliter) && !line.contains(comment)) {
                String content = line.trim().substring(2);
                String classNameS = content.split(spliter)[0];
                String classNameT = content.split(spliter)[1];

                String op = line.trim().substring(0, 2);
                if (op.equals("- ")) {
                    boolean find = false;
                    for (Entry<String, Set<AtgEdge>> entry : Global.v().getiCTGModel().getDynamicModel().getAtgEdges()
                            .entrySet()) {
                        if (find)
                            break;
                        if (entry.getKey().equals(classNameS)) {
                            for (AtgEdge edge : entry.getValue()) {
                                if (edge.getDestnation().getClassName().equals(classNameT)) {
                                    entry.getValue().remove(edge);
                                    model.setFilteredNum(model.getFilteredNum() + 1);
                                    if (classNameS.contains("Service") || classNameT.contains("Service"))
                                        model.setFilteredServiceNum(model.getFilteredServiceNum() + 1);
                                    else if (classNameS.contains("Receiver") || classNameT.contains("Receiver"))
                                        model.setFilteredReceiverNum(model.getFilteredReceiverNum() + 1);
                                    find = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!find) {
                        FileUtils.writeText2File(Global.v().getAppModel().getAppName() + ".txt", line + "\n", true);
                    }
                }
                if (op.equals("+ ")) {
                    boolean find = false;
                    for (Entry<String, Set<AtgEdge>> entry : Global.v().getiCTGModel().getDynamicModel().getAtgEdges()
                            .entrySet()) {
                        if (find)
                            break;
                        if (entry.getKey().equals(classNameS)) {
                            for (AtgEdge edge : entry.getValue()) {
                                if (edge.getDestnation().getClassName().equals(classNameT)) {
                                    find = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!find) {
                        AtgEdge edge = new AtgEdge(new AtgNode(classNameS), new AtgNode(classNameT), "", -1, "");
                        if (model.addAtgEdges(classNameS, edge))
                            model.setEnhancedNum(model.getEnhancedNum() + 1);
                    } else {
                        FileUtils.writeText2File(Global.v().getAppModel().getAppName() + ".txt", line + "\n", true);
                    }
                }
            }
        }

        return model;
    }

}
