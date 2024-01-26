package com.iscas.iccbot.client.obj.target.ctg;

import com.iscas.iccbot.Analyzer;
import com.iscas.iccbot.MyConfig;
import com.iscas.iccbot.analyze.model.analyzeModel.StaticFiledInfo;
import com.iscas.iccbot.analyze.model.sootAnalysisModel.Context;
import com.iscas.iccbot.analyze.model.sootAnalysisModel.Counter;
import com.iscas.iccbot.analyze.utils.ConstantUtils;
import com.iscas.iccbot.analyze.utils.SootUtils;
import com.iscas.iccbot.analyze.utils.ValueObtainer;
import soot.*;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.tagkit.ConstantValueTag;
import soot.tagkit.Tag;

import java.util.HashSet;
import java.util.List;

public class StaticValueAnalyzer extends Analyzer {

    public StaticValueAnalyzer() {
        super();
    }

    /**
     * store static value information get the init value of each static
     * non-final variable
     */
    @Override
    public void analyze() {
        for (SootClass sc : Scene.v().getApplicationClasses()) {
            if (!MyConfig.getInstance().getMySwitch().allowLibCodeSwitch()) {
                if (!SootUtils.isNonLibClass(sc.getName()))
                    continue;
            }
            for (SootField field : sc.getFields()) {
                for (Tag tag : field.getTags()) {
                    if (tag instanceof ConstantValueTag) {
                        if (tag.toString().contains(": ") && tag.toString().split(": ").length > 1) {
                            String tagVal = tag.toString().split(": ")[1];
                            appModel.getStaticRefSignature2initAssignMap().put(field.getSignature(), tagVal);
                        }
                    }
                }
            }
            for (SootMethod sm : sc.getMethods()) {
                if (!SootUtils.hasSootActiveBody(sm)) continue;

                List<Unit> units = SootUtils.getUnitListFromMethod(sm);
                for (Unit u : units) {
                    if (u instanceof JAssignStmt) {
                        JAssignStmt jas = (JAssignStmt) u;
                        SootField field = null;
                        if (jas.getLeftOp() instanceof StaticFieldRef) {
                            StaticFieldRef sf = (StaticFieldRef) jas.getLeftOp();
                            field = sf.getField();
                        }
                        if (jas.getLeftOp() instanceof JInstanceFieldRef) {
                            JInstanceFieldRef jif = (JInstanceFieldRef) jas.getLeftOp();
                            field = jif.getField();
                        }
                        if (field != null) {
                            if (!appModel.getStaticRefSignature2UnitMap().containsKey(field.getSignature()))
                                appModel.getStaticRefSignature2UnitMap().put(field.getSignature(), new HashSet<>());
                            StaticFiledInfo fInfo = new StaticFiledInfo(sm, u, jas.getRightOp());
                            appModel.getStaticRefSignature2UnitMap().get(field.getSignature()).add(fInfo);
                            try {
                                Counter ct = new Counter();
                                ValueObtainer vo = new ValueObtainer(sm.getSignature(), ConstantUtils.FLAGSTATIC,
                                        new Context(), sm.getDeclaringClass().getName(), ct);
                                for (String val : vo.getValueOfVar(jas.getRightOp(), jas, 0).getValues())
                                    appModel.getStaticRefSignature2initAssignMap().put(field.getSignature(), val);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        System.out.println("StaticValueAnalyzer finished");
        MyConfig.getInstance().setStaticValueAnalyzeFinish(true);
    }
}