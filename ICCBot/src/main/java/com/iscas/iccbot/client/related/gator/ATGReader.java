package com.iscas.iccbot.client.related.gator;

import com.iscas.iccbot.Analyzer;
import com.iscas.iccbot.Global;
import com.iscas.iccbot.analyze.utils.output.FileUtils;
import com.iscas.iccbot.client.obj.model.atg.ATGModel;
import com.iscas.iccbot.client.obj.model.atg.AtgEdge;
import com.iscas.iccbot.client.obj.model.atg.AtgNode;
import soot.Scene;
import soot.SootClass;

import java.io.File;
import java.util.List;

public class ATGReader extends Analyzer {
    ATGModel model;

    public ATGReader(ATGModel model) {
        this.model = model;
    }

    @Override
    public void analyze() {
    }

    public boolean obtainATGfromFile() {
        File file = new File(model.getATGFilePath());
        if (!file.exists()) {
            model.setExist(false);
            return false;
        }
        return true;
    }

    public void constructModelForGator() {
        List<String> lines = FileUtils.getListFromFile(model.getATGFilePath());
        for (String line : lines) {
            line = line.trim();
            line = line.replace("Node(", "");
            line = line.replace(")", "");
            line = line.replace(";", "");
            line = line.replace(" -> ", "->");
            if (line.split("->").length == 2) {
                String source = line.split("->")[0];
                String target = line.split("->")[1];
                AtgNode sNode = null, tNode = null;
                for (String name : Global.v().getAppModel().getComponentMap().keySet()) {
                    if (name.endsWith(source))
                        sNode = new AtgNode(name);
                    if (name.endsWith(target))
                        tNode = new AtgNode(name);
                }
                if (sNode != null && tNode != null) {
                    AtgEdge edge = new AtgEdge(sNode, tNode, "", -1, "");
                    model.addAtgEdges(sNode.getClassName(), edge);
                }
            }
        }
    }

    public void constructModelForICCBot() {
        List<String> lines = FileUtils.getListFromFile(model.getATGFilePath());
        for (String line : lines) {
            line = line.trim();
            line = line.replace(";", "");
            line = line.replace(" -> ", "->");
            if (line.split("->").length == 2) {
                String source = line.split("->")[0];
                String target = line.split("->")[1];
                AtgNode sNode = null, tNode = null;
                for (SootClass sClass : Scene.v().getApplicationClasses()) {
                    String name = sClass.getName();
                    if (name.endsWith(source))
                        sNode = new AtgNode(name);
                    if (name.endsWith(target))
                        tNode = new AtgNode(name);
                }
                if (sNode != null && tNode != null) {
                    AtgEdge edge = new AtgEdge(sNode, tNode, "", -1, "");
                    model.addAtgEdges(sNode.getClassName(), edge);
                }
            }
        }
    }

}
