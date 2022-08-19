package com.iscas.iccbot.client.related.story;

import com.iscas.iccbot.Analyzer;
import com.iscas.iccbot.Global;
import com.iscas.iccbot.analyze.model.analyzeModel.MethodSummaryModel;
import com.iscas.iccbot.analyze.utils.ConstantUtils;
import com.iscas.iccbot.analyze.utils.output.FileUtils;
import com.iscas.iccbot.client.obj.model.atg.ATGModel;
import com.iscas.iccbot.client.obj.model.atg.AtgEdge;
import com.iscas.iccbot.client.obj.model.atg.AtgNode;
import com.iscas.iccbot.client.obj.model.atg.AtgType;
import com.iscas.iccbot.client.related.story.model.StoryModel;
import com.iscas.iccbot.client.statistic.model.StatisticResult;
import org.dom4j.DocumentException;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

public class StoryReader extends Analyzer {
    StoryModel model;
    protected StatisticResult result;
    protected Map<String, MethodSummaryModel> summaryMap;

    public StoryReader(StatisticResult result) {
        this.result = result;
        summaryMap = new HashMap<String, MethodSummaryModel>();
    }

    @Override
    public void analyze() {
        model = Global.v().getStoryModel();
//		System.out.println(ConstantUtils.STORYFOLDETR + appModel.getAppName() + ".txt");
        model.setStoryFilePath(ConstantUtils.STORYFOLDETR + appModel.getAppName() + ".txt");
        if (obtainATGfromFile()) {
            try {
                constructModel();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            getStoryOptModel();
        }
    }

    private boolean obtainATGfromFile() {
        File file = new File(model.getStoryFilePath());
        if (!file.exists()) {
            model.getStoryAtgModelWithoutFrag().setExist(false);
            return false;
        }
        return true;
    }

    private void constructModel() throws DocumentException {
        List<String> lines = FileUtils.getListFromFile(model.getStoryFilePath());
        for (String line : lines) {
            line = line.trim();
            if (line.split("-->").length == 2) {
                String source = line.split("-->")[0];
                String target = line.split("-->")[1];
                AtgNode sNode = new AtgNode(source);
                AtgNode tNode = new AtgNode(target);
                if (sNode != null && tNode != null) {
                    AtgEdge edge = new AtgEdge(sNode, tNode, "", -1, "");
                    model.getStoryAtgModel().addAtgEdges(sNode.getClassName(), edge);
                }
            }
        }
    }

    private void getStoryOptModel() {
        ATGModel optModel = model.getStoryAtgModelWithoutFrag();
        ATGModel mergedIctgModel = model.getStoryAtgModel();
        Map<String, Set<String>> desfrag2StratcomMap = new HashMap<String, Set<String>>();
        // System.out.println(PrintUtils.printMap(mergedIctgModel.getAtgEdges()));
        // handle comp2fragment and frag2fragment --> comp2fragment
        for (Entry<String, Set<AtgEdge>> entry : mergedIctgModel.getAtgEdges().entrySet()) {
            for (AtgEdge edge : entry.getValue()) {
                if (edge.getType().equals(AtgType.Act2Frag) || edge.getType().equals(AtgType.NonAct2Frag)) {
                    String souComp = edge.getSource().getName();
                    String desFrag = edge.getDestnation().getName();
                    if (!desfrag2StratcomMap.containsKey(desFrag))
                        desfrag2StratcomMap.put(desFrag, new HashSet<String>());
                    desfrag2StratcomMap.get(desFrag).add(souComp);
                }
            }
        }
        while (true) { // fix point
            boolean changed = false;
            for (Entry<String, Set<AtgEdge>> entry : mergedIctgModel.getAtgEdges().entrySet()) {
                for (AtgEdge edge : entry.getValue()) {
                    if (edge.getType().equals(AtgType.Frag2Frag)) {
                        String souFrag = edge.getSource().getName();
                        String desFrag = edge.getDestnation().getName();
                        if (desfrag2StratcomMap.containsKey(souFrag)) {
                            if (!desfrag2StratcomMap.containsKey(desFrag)) {
                                desfrag2StratcomMap.put(desFrag, new HashSet<String>());
                            }
                            for (String com : desfrag2StratcomMap.get(souFrag)) {
                                if (!desfrag2StratcomMap.get(desFrag).contains(com)) {
                                    desfrag2StratcomMap.get(desFrag).add(com);
                                    changed = true;
                                }
                            }
                        }
                    }
                }
            }
            if (changed == false)
                break;
        }

        for (Entry<String, Set<AtgEdge>> entry : mergedIctgModel.getAtgEdges().entrySet()) {
            for (AtgEdge edge : entry.getValue()) {
                // if(edge.getSource().getClassName().contains("ItemlistFragment"))
                // System.out.println(edge);
                switch (edge.getType()) {
                    case Act2Act:
                        optModel.addAtgEdges(entry.getKey(), edge);
                        break;
                    case NonAct2Act:
                        optModel.addAtgEdges(entry.getKey(), edge);
                        break;
                    case Act2NonAct:
                        optModel.addAtgEdges(entry.getKey(), edge);
                        break;
                    case NonAct2NonAct:
                        optModel.addAtgEdges(entry.getKey(), edge);
                        break;
                    case Frag2Act:
                        if (desfrag2StratcomMap.containsKey(edge.getSource().getClassName())) {
                            for (String startCom : desfrag2StratcomMap.get(edge.getSource().getClassName())) {
                                AtgEdge edgeCopy = new AtgEdge(edge);
                                edgeCopy.setSource(new AtgNode(startCom));
                                optModel.addAtgEdges(startCom, edgeCopy);
                            }
                        }
                        break;
                    case Frag2NonAct:
                        if (desfrag2StratcomMap.containsKey(edge.getSource().getClassName())) {
                            for (String startCom : desfrag2StratcomMap.get(edge.getSource().getClassName())) {
                                AtgEdge edgeCopy = new AtgEdge(edge);
                                edgeCopy.setSource(new AtgNode(startCom));
                                optModel.addAtgEdges(startCom, edgeCopy);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        model.setStoryAtgModelWithoutFrag(optModel);
    }
}
