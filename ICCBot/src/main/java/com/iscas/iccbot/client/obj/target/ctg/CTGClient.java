package com.iscas.iccbot.client.obj.target.ctg;

import com.iscas.iccbot.Global;
import com.iscas.iccbot.MyConfig;
import com.iscas.iccbot.SummaryLevel;
import com.iscas.iccbot.analyze.utils.ConstantUtils;
import com.iscas.iccbot.analyze.utils.GraphUtils;
import com.iscas.iccbot.analyze.utils.output.FileUtils;
import com.iscas.iccbot.client.BaseClient;
import com.iscas.iccbot.client.cg.CallGraphClient;
import com.iscas.iccbot.client.cg.DynamicReceiverCGAnalyzer;
import com.iscas.iccbot.client.manifest.ManifestClient;
import com.iscas.iccbot.client.obj.ObjectAnalyzer;
import com.iscas.iccbot.client.obj.model.atg.ATGModel;
import com.iscas.iccbot.client.obj.model.atg.AtgEdge;
import com.iscas.iccbot.client.obj.model.atg.AtgNode;
import com.iscas.iccbot.client.obj.model.atg.AtgType;
import com.iscas.iccbot.client.obj.target.fragment.FragmentClient;
import com.iscas.iccbot.client.soot.IROutputClient;
import com.iscas.iccbot.client.statistic.model.StatisticResult;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import soot.SootMethod;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/**
 * Analyzer Class
 *
 * @author hanada
 * @version 2.0
 */
@Slf4j
public class CTGClient extends BaseClient {
    /**
     * analyze CTG for single app
     */
    @Override
    protected void clientAnalyze() {
        result = new StatisticResult();

        if (!MyConfig.getInstance().isManifestAnalyzeFinish()) {
            new ManifestClient().start();
        }
        if (MyConfig.getInstance().isWriteSootOutput()) {
            new IROutputClient().start();
        }
        if (!MyConfig.getInstance().isCallGraphAnalyzeFinish()) {
            new CallGraphClient().start();
        }
        if (!MyConfig.getInstance().isStaticValueAnalyzeFinish()) {
            if (MyConfig.getInstance().getMySwitch().isStaticFieldSwitch()) {
                StaticValueAnalyzer staticValueAnalyzer = new StaticValueAnalyzer();
                staticValueAnalyzer.start();
            }
        }
        if (MyConfig.getInstance().getMySwitch().isDynamicBCSwitch()) {
            DynamicReceiverCGAnalyzer dynamicIntentFilterAnalyzer = new DynamicReceiverCGAnalyzer();
            dynamicIntentFilterAnalyzer.start();
        }

        if (MyConfig.getInstance().getMySwitch().isFragmentSwitch()) {
            if (!MyConfig.getInstance().isFragmentAnalyzeFinish()) {
                new FragmentClient().start();
            }
        }
        setMySwitch1();
        for (List<SootMethod> topoQueue : Global.v().getAppModel().getTopoMethodQueueSet()) {
            ObjectAnalyzer analyzer = new CTGAnalyzer(topoQueue, result);
            analyzer.start();
        }
        log.info("Successfully analyze with CTGClient");
    }

    protected void setMySwitch1() {
        MyConfig.getInstance().getMySwitch().setSetAttributeStrategy(true);
        MyConfig.getInstance().getMySwitch().setGetAttributeStrategy(true);
        MyConfig.getInstance().getMySwitch().setSummaryStrategy(SummaryLevel.object);
    }


    @Override
    public void clientOutput() throws IOException, DocumentException {
        outputCTGInfo();
        if (!MyConfig.getInstance().isOracleConstructionClientFinish()) {
//			new ToolEvaluateClient().start();
        }
    }

    private void outputCTGInfo() {
        String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
                + File.separator;
        FileUtils.createFolder(summary_app_dir + ConstantUtils.CGFOLDETR);
        FileUtils.createFolder(summary_app_dir + ConstantUtils.FRAGFOLDETR);
        FileUtils.createFolder(summary_app_dir + ConstantUtils.CGFOLDETR);

        CTGClientOutput outer = new CTGClientOutput(this.result);

        String ictgFolder = summary_app_dir + ConstantUtils.ICTGFOLDETR;
        String fragFolder = summary_app_dir + ConstantUtils.FRAGFOLDETR;
        log.info("writeComponentModel");
        /** Component **/
        outer.writeComponentModel(ictgFolder, ConstantUtils.COMPONENTMODEL);
        /** Method **/
//        log.info("writeMethodSummaryModel");
//        outer.writeMethodSummaryModel(ictgFolder, ConstantUtils.SINGLEMETHOD_ENTRY, true);
//		outer.writeMethodSummaryModel(ictgFolder, ConstantUtils.SINGLEMETHOD_ALL, false);
//        if (MyConfig.getInstance().getMySwitch().isFragmentSwitch()) {
//			outer.appendInfo(ictgFolder, fragFolder, ConstantUtils.SINGLEMETHOD_ENTRY);
//			outer.appendInfo(ictgFolder, fragFolder, ConstantUtils.SINGLEMETHOD_ALL);
//            FileUtils.copyFile(fragFolder + ConstantUtils.SINGLEMETHOD_ENTRY, ictgFolder + ConstantUtils.SINGLEMETHODFRAG_ENTRY);
//        }

//        /** Path **/
//        log.info("writePathSummaryModel");
//        outer.writePathSummaryModel(ictgFolder, ConstantUtils.SINGLEPATH_ENTRY, true);
//		outer.writePathSummaryModel(ictgFolder, ConstantUtils.SINGLEPATH_ALL, false);
//        if (MyConfig.getInstance().getMySwitch().isFragmentSwitch()) {
//			outer.appendInfo(ictgFolder, fragFolder, ConstantUtils.SINGLEPATH_ENTRY);
//			outer.appendInfo(ictgFolder, fragFolder, ConstantUtils.SINGLEPATH_ALL);
//            FileUtils.copyFile(fragFolder + ConstantUtils.SINGLEPATH_ENTRY, ictgFolder + ConstantUtils.SINGLEPATHFRAG_ENTRY);
//        }

        /** Intent **/
//        log.info("writeIntentSummaryModel");
//        outer.writeIntentSummaryModel(ictgFolder, ConstantUtils.SINGLEOBJECT_ENTRY, true);
//		outer.writeIntentSummaryModel(ictgFolder, ConstantUtils.SINGLEOBJECT_ALL, false);
//        if (MyConfig.getInstance().getMySwitch().isFragmentSwitch()) {
//			outer.appendInfo(ictgFolder, fragFolder, ConstantUtils.SINGLEOBJECT_ENTRY);
//			outer.appendInfo(ictgFolder, fragFolder, ConstantUtils.SINGLEOBJECT_ALL);
//            FileUtils.copyFile(fragFolder + ConstantUtils.SINGLEOBJECT_ENTRY, ictgFolder + ConstantUtils.SINGLEOBJECTFRAG_ENTRY);
//        }
        log.info("write" + ConstantUtils.ICCBOTOUTPUT);
        outer.writeIntentSummaryModel(ictgFolder, ConstantUtils.ICCBOTOUTPUT, true);

        /** ICTG **/
        // merge frag and component
        ATGModel.mergeNodels2newOne(null, Global.v().getFragmentModel()
                .getAtgModel(), Global.v().getiCTGModel().getOptModel());
        ATGModel ictgMergedModel = Global.v().getiCTGModel().getOptModel();
        // ictgMergedModel
        log.info("writeAtgModeTxtFile");
        String txtName = Global.v().getAppModel().getAppName() + "_" + ConstantUtils.ICTGMERGE + ".txt";
        outer.writeAtgModeTxtFile(ictgFolder, txtName, ictgMergedModel, false);
        log.info("writeDotFile");
        String dotname = Global.v().getAppModel().getAppName() + "_" + ConstantUtils.ICTGMERGE;
        outer.writeDotFile(ictgFolder, dotname, ictgMergedModel, true);
//        log.info("writeATGModel");
//        outer.writeATGModel(ictgFolder, ConstantUtils.ICTGMERGE + ".xml",
//                ictgMergedModel);


//		// ictgOptModel
        log.info("writeATGModel2");
        outer.writeATGModel(ictgFolder, ConstantUtils.ICTGOPT + ".xml", getIctgOptModel(false));

        Global.v().getiCTGModel().setOptModelwithoutFrag(getIctgOptModel(true));
        ATGModel ictgOptModel = Global.v().getiCTGModel().getOptModelwithoutFrag();
        log.info("writeAtgModeTxtFile2");
        String txtName2 = Global.v().getAppModel().getAppName() + "_" + ConstantUtils.ICTGOPT + ".txt";
        outer.writeAtgModeTxtFile(ictgFolder, txtName2, ictgOptModel, false);

        log.info("writeDotFile2");
        String dotname2 = Global.v().getAppModel().getAppName() + "_" + ConstantUtils.ICTGOPT;
        outer.writeDotFile(ictgFolder, dotname2, ictgOptModel, false);
//		if (ictgMergedModel.getComp2CompSize() < 1800)


        GraphUtils.generateDotFile(ictgFolder + dotname2, "pdf");
        GraphUtils.generateDotFile(ictgFolder + dotname, "pdf");
        // outer.writeIccLinksConfigFile(summary_app_dir +
        // ConstantUtils.ICTGFOLDETR, ConstantUtils.LINKFILE, ictgOptModel);

    }


    private ATGModel getIctgOptModel(boolean removeClassEdges) {
        ATGModel ictgOptModel = new ATGModel();
        ATGModel mergedIctgModel = Global.v().getiCTGModel().getOptModel();
        Map<String, Set<String>> desfrag2StratcomMap = new HashMap<String, Set<String>>();
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

        List<Entry<String, Set<AtgEdge>>> entryList = new ArrayList<>(mergedIctgModel.getAtgEdges().entrySet());
        int i = 0;
        while (i < entryList.size()) {
            Entry<String, Set<AtgEdge>> entry = entryList.get(i);
            for (AtgEdge edge : entry.getValue()) {
                switch (edge.getType()) {
                    case Act2Act:
                        ictgOptModel.addAtgEdges(entry.getKey(), edge);
                        break;
                    case NonAct2Act:
                        ictgOptModel.addAtgEdges(entry.getKey(), edge);
                        break;
                    case Act2NonAct:
                        ictgOptModel.addAtgEdges(entry.getKey(), edge);
                        break;
                    case NonAct2NonAct:
                        ictgOptModel.addAtgEdges(entry.getKey(), edge);
                        break;
                    case Frag2Act:
                    case Frag2NonAct:
                        if (desfrag2StratcomMap.containsKey(edge.getSource().getClassName())) {
                            for (String startCom : desfrag2StratcomMap.get(edge.getSource().getClassName())) {
                                AtgEdge edgeCopy = new AtgEdge(edge);
                                edgeCopy.setSource(new AtgNode(startCom));
                                ictgOptModel.addAtgEdges(startCom, edgeCopy);
                            }
                        }
                        break;
                    case Act2Class:
                    case NonAct2Class:
                    case Class2Any:
                        if(!removeClassEdges)
                            ictgOptModel.addAtgEdges(entry.getKey(), edge);
                        break;
                    default:
                        break;
                }
            }
            i++;
        }
//        for (Entry<String, Set<AtgEdge>> entry : mergedIctgModel.getAtgEdges().entrySet()) {
//            for (AtgEdge edge : entry.getValue()) {
//                switch (edge.getType()) {
//                    case Act2Act:
//                        ictgOptModel.addAtgEdges(entry.getKey(), edge);
//                        break;
//                    case NonAct2Act:
//                        ictgOptModel.addAtgEdges(entry.getKey(), edge);
//                        break;
//                    case Act2NonAct:
//                        ictgOptModel.addAtgEdges(entry.getKey(), edge);
//                        break;
//                    case NonAct2NonAct:
//                        ictgOptModel.addAtgEdges(entry.getKey(), edge);
//                        break;
//                    case Frag2Act:
//                    case Frag2NonAct:
//                        if (desfrag2StratcomMap.containsKey(edge.getSource().getClassName())) {
//                            for (String startCom : desfrag2StratcomMap.get(edge.getSource().getClassName())) {
//                                AtgEdge edgeCopy = new AtgEdge(edge);
//                                edgeCopy.setSource(new AtgNode(startCom));
//                                ictgOptModel.addAtgEdges(startCom, edgeCopy);
//                            }
//                        }
//                        break;
//                    case Act2Class:
//                    case NonAct2Class:
//                    case Class2Any:
//                        if(!removeClassEdges)
//                            ictgOptModel.addAtgEdges(entry.getKey(), edge);
//                        break;
//                    default:
//                        break;
//                }
//            }
//        }

        return ictgOptModel;
    }

    protected void setMySwitch() {
        // TODO Auto-generated method stub

    }
}