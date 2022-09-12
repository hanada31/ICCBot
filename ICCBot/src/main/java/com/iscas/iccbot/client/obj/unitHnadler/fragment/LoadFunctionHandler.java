package com.iscas.iccbot.client.obj.unitHnadler.fragment;

import com.iscas.iccbot.Global;
import com.iscas.iccbot.analyze.model.analyzeModel.ObjectSummaryModel;
import com.iscas.iccbot.analyze.model.sootAnalysisModel.Context;
import com.iscas.iccbot.analyze.model.sootAnalysisModel.Counter;
import com.iscas.iccbot.analyze.utils.SootUtils;
import com.iscas.iccbot.analyze.utils.ValueObtainer;
import com.iscas.iccbot.client.obj.model.fragment.FragmentSummaryModel;
import com.iscas.iccbot.client.obj.unitHnadler.UnitHandler;
import soot.SootClass;
import soot.jimple.InvokeExpr;
import soot.jimple.infoflow.android.resources.ARSCFileParser;
import soot.jimple.infoflow.android.resources.ARSCFileParser.AbstractResource;

import java.io.IOException;
import java.util.List;

public class LoadFunctionHandler extends UnitHandler {
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
        this.singleFrag.getSendFragment2Start().add(unit);
        try {
            loadAnalyze();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAnalyze() throws IOException {
        InvokeExpr invMethod = SootUtils.getSingleInvokedMethod(unit);
        ValueObtainer vo = new ValueObtainer(methodSig, "", new Context(), new Counter());
        int resourceId = 0;
        ARSCFileParser resParser = new ARSCFileParser();
        resParser.parse(appModel.getAppPath());
        try {
            String id = vo.getValueOfVar(invMethod.getArg(0), unit, 0).getValues().get(0);
            resourceId = Integer.parseInt(id);
        } catch (Exception NumberFormatException) {
            return;
        }
        AbstractResource resource = resParser.findResource(resourceId);
        if (Global.v().getFragmentModel().getXmlFragmentMap().containsKey(resource.toString())) {
            List<SootClass> scs = Global.v().getFragmentModel().getXmlFragmentMap().get(resource.toString());
            for (SootClass sc : scs)
                this.singleFrag.addSetDestinationList(sc.getName());
        }

    }

}
