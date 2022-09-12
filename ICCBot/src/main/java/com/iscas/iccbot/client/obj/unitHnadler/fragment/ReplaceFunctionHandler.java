package com.iscas.iccbot.client.obj.unitHnadler.fragment;

import com.iscas.iccbot.analyze.model.analyzeModel.ObjectSummaryModel;
import com.iscas.iccbot.analyze.model.sootAnalysisModel.Context;
import com.iscas.iccbot.analyze.model.sootAnalysisModel.Counter;
import com.iscas.iccbot.analyze.utils.ValueObtainer;
import com.iscas.iccbot.client.obj.model.fragment.FragmentSummaryModel;
import com.iscas.iccbot.client.obj.unitHnadler.UnitHandler;
import soot.*;

public class ReplaceFunctionHandler extends UnitHandler {
    Context context;
    FragmentSummaryModel singleFrag;

    @Override
    public void handleSingleObject(ObjectSummaryModel singleObject) {
        this.handleSingleObject(new Context(), singleObject);
    }

    @Override
    public void handleSingleObject(Context context, ObjectSummaryModel singleObject) {
        this.context = context;
        this.singleFrag = (FragmentSummaryModel) singleObject;
        this.singleFrag.getDataHandleList().add(unit);
        replaceAnalyze();
    }

    @Override
    public void handleSingleObject(Context oldContextwithRealValue, ObjectSummaryModel singleObject, Unit targetUnit) {
        this.oldContextwithRealValue = oldContextwithRealValue;
        this.singleFrag = (FragmentSummaryModel) singleObject;
        this.singleFrag.getDataHandleList().add(unit);
        this.targetUnit = targetUnit;
        replaceAnalyze();
    }

    private void replaceAnalyze() {
        int id = 1;
        Value inputVar = getInputVar(id, unit);
        if (inputVar == null)
            return;
        Context objContextInner = new Context();
        if (oldContextwithRealValue != null) {
            objContextInner = constructContextObj(id + 1, unit);
        }
        ValueObtainer vo = new ValueObtainer(methodSig, "", objContextInner, new Counter());
        for (String res : vo.getValueOfVar(inputVar, unit, 0).getValues()) {
            if (res.contains("new ")) {
                transformFragmentMethod(methodUnderAnalyze.getDeclaringClass(), unit, res.replace("new ", ""),
                        methodUnderAnalyze);
            }
        }

    }

    private void transformFragmentMethod(SootClass sourceCls, Unit u, String res, SootMethod sm) {
        for (SootClass sc : Scene.v().getApplicationClasses()) {
            if (!sc.getName().equals(res))
                continue;
            singleFrag.getReplaceList().add(sc.getName());
            singleFrag.addSetDestinationList(sc.getName());
        }
        return;
    }
}
