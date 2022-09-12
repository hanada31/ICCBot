package com.iscas.iccbot.client.obj.unitHnadler.fragment;

import com.iscas.iccbot.Global;
import com.iscas.iccbot.analyze.model.analyzeModel.ObjectSummaryModel;
import com.iscas.iccbot.analyze.model.sootAnalysisModel.Context;
import com.iscas.iccbot.analyze.model.sootAnalysisModel.Counter;
import com.iscas.iccbot.analyze.utils.ValueObtainer;
import com.iscas.iccbot.client.obj.model.ctg.IntentSummaryModel;
import com.iscas.iccbot.client.obj.model.fragment.FragmentSummaryModel;
import com.iscas.iccbot.client.obj.unitHnadler.UnitHandler;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.infoflow.android.resources.ARSCFileParser;
import soot.jimple.infoflow.android.resources.ARSCFileParser.AbstractResource;

import java.io.IOException;
import java.util.List;

public class SetContentFunctionHandler extends UnitHandler {
    Context context;
    ObjectSummaryModel objectModel;

    @Override
    public void handleSingleObject(ObjectSummaryModel singleObject) {
        this.objectModel = singleObject;
        this.handleSingleObject(new Context(), singleObject);
    }

    @Override
    public void handleSingleObject(Context context, ObjectSummaryModel singleObject) {
        this.context = context;
        try {
            setContentAnalyze();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleSingleObject(Context oldContextwithRealValue, ObjectSummaryModel singleObject, Unit targetUnit) {
        this.objectModel = singleObject;
        this.oldContextwithRealValue = oldContextwithRealValue;
        singleObject.getDataHandleList().add(unit);
        this.targetUnit = targetUnit;
        try {
            setContentAnalyze();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setContentAnalyze() throws IOException {
        int id = 0;
        Value inputVar = getInputVar(id, unit);
        // if(inputVar == null) return;
        Context objContextInner = new Context();
        if (oldContextwithRealValue != null) {
            objContextInner = constructContextObj(id + 1, unit);
        }
        boolean findTarget = false;
        ValueObtainer vo = new ValueObtainer(methodSig, "", objContextInner, new Counter());
        List<String> ids = vo.getValueOfVar(inputVar, unit, 0).getValues();
        if (ids != null && ids.size() > 0) {
            String resId = ids.get(0);
            ARSCFileParser resParser = new ARSCFileParser();
            resParser.parse(appModel.getAppPath());
            int resourceId;
            try {
                resourceId = Integer.parseInt(resId);
            } catch (NumberFormatException e) {
                return;
            }
            // Global.v().getFragmentModel().getXmlFragmentMap() is null !!!!!
            AbstractResource resource = resParser.findResource(resourceId);
            if (resource != null && Global.v().getFragmentModel().getXmlFragmentMap().containsKey(resource.toString())) {
                List<SootClass> scs = Global.v().getFragmentModel().getXmlFragmentMap().get(resource.toString());
                for (SootClass sc : scs) {
                    if (sc == null) continue;
                    if (objectModel instanceof FragmentSummaryModel)
                        ((FragmentSummaryModel) objectModel).addSetDestinationList(sc.getName());
                    else {
                        ((IntentSummaryModel) objectModel).addSetDestinationList(sc.getName());
                    }
                    findTarget = true;
                }
            }
        }
        if (findTarget) {
            if (objectModel instanceof FragmentSummaryModel)
                ((FragmentSummaryModel) objectModel).getSendFragment2Start().add(unit);
            else {
                ((IntentSummaryModel) objectModel).getSendIntent2ICCList().add(unit);
            }
        }
    }

}
