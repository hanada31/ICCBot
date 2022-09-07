package com.iscas.iccbot.client.obj.target.fragment;

import com.iscas.iccbot.Global;
import com.iscas.iccbot.MyConfig;
import com.iscas.iccbot.analyze.model.analyzeModel.MethodSummaryModel;
import com.iscas.iccbot.analyze.model.analyzeModel.ObjectSummaryModel;
import com.iscas.iccbot.analyze.model.analyzeModel.UnitNode;
import com.iscas.iccbot.analyze.utils.ConstantUtils;
import com.iscas.iccbot.analyze.utils.SootUtils;
import com.iscas.iccbot.client.obj.ObjectAnalyzer;
import com.iscas.iccbot.client.obj.model.atg.AtgEdge;
import com.iscas.iccbot.client.obj.model.atg.AtgNode;
import com.iscas.iccbot.client.obj.model.component.ComponentModel;
import com.iscas.iccbot.client.obj.model.fragment.FragmentSummaryModel;
import com.iscas.iccbot.client.statistic.model.DoStatistic;
import com.iscas.iccbot.client.statistic.model.StatisticResult;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

import java.util.ArrayList;
import java.util.List;

public class FragmentAnalyzer extends ObjectAnalyzer {

    public FragmentAnalyzer(List<SootMethod> topoQueue, StatisticResult result) {
        super(topoQueue, new FragmentAnalyzerHelper(), result);
    }

    @Override
    public void assignForObjectName() {
        this.objectName = FragmentSummaryModel.class.getCanonicalName();
    }

    /**
     * analyzeCurrentSetMethods for a set of method
     */
    @Override
    public void drawATGandStatistic(MethodSummaryModel model) {
        if (model == null)
            return;
        makeStatistic(model);
        generateATGInfo(model);
    }

    /**
     * generateATGInfo
     *
     * @param methodSummary
     */
    private void generateATGInfo(MethodSummaryModel methodSummary) {
        SootMethod sootMtd = methodSummary.getMethod();
        SootClass cls = null;
        if (appModel.getEntryMethod2Component().containsKey(sootMtd))
            cls = appModel.getEntryMethod2Component().get(sootMtd);
        if (cls == null) {
            String currentClsName = SootUtils.getNameofClass(sootMtd.getDeclaringClass());
            if (appModel.getComponentMap().containsKey(currentClsName))
                cls = sootMtd.getDeclaringClass();
            else
                return;
        }
        List<SootClass> subClasses = new ArrayList<SootClass>();
        if (MyConfig.getInstance().getMySwitch().isPolymSwitch()
                || sootMtd.getName().startsWith(ConstantUtils.ENTRYID)) {
            try {
                subClasses = Scene.v().getActiveHierarchy().getSubclassesOfIncluding(cls);
            } catch (Exception e) {
                subClasses.add(cls);
            }
        } else {
            subClasses.add(cls);
        }

        for (SootClass sootCls : subClasses) {
            if (sootCls.getMethodUnsafe(sootMtd.getSubSignature()) == null || sootCls == sootMtd.getDeclaringClass()) {
                String src = sootCls.getName();
                for (ObjectSummaryModel singleFrag : methodSummary.getSingleObjects()) {
                    if (((FragmentSummaryModel) singleFrag).getSendFragment2Start().size() == 0)
                        continue;
                    getTargetOfSrc((FragmentSummaryModel) singleFrag, src);
                }
            }
        }
    }

    /**
     * getTargetOfSrc
     *
     * @param singleFrag
     * @param src
     */
    private void getTargetOfSrc(FragmentSummaryModel singleFrag, String src) {
        SootMethod method = singleFrag.getMethod();
        Unit unit = singleFrag.getSendFragment2Start().iterator().next();
        for (String des : singleFrag.getSetDestinationList()) {
            ComponentModel comp = appModel.getComponentMap().get(des);
            AtgEdge edge;
            if (comp != null) {
                edge = new AtgEdge(new AtgNode(src), new AtgNode(des), method.getSignature(), -2,
                        comp.getComponentType());
                Global.v().getFragmentModel().getAtgModel().addAtgEdges(src, edge);
            } else {
                edge = new AtgEdge(new AtgNode(src), new AtgNode(des), method.getSignature(), -2, "c");
                Global.v().getFragmentModel().getAtgModel().addAtgEdges(src, edge);
            }

            String name = SootUtils.getNameofClass(singleFrag.getPathSummary().getMethodSummary().getComponentName());
            ComponentModel sourceComponent = Global.v().getAppModel().getComponentMap().get(name);
            if (sourceComponent != null) {
                sourceComponent.getSendModel().getIccTargetSet().add(des);
            }
        }
    }

    /**
     * makeStatistic
     *
     * @param model
     */
    private void makeStatistic(MethodSummaryModel model) {

        DoStatistic.updateSummaryStatisticUseSummayMap(model, result);

        DoStatistic.updateMLSStatisticUseSummaryMapForFragment(true, model, result);
        DoStatistic.updateMLSStatisticUseSummaryMapForFragment(false, model, result);

    }
}
