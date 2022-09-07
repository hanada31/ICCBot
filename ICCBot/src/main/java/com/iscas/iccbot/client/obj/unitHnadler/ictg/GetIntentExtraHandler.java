package com.iscas.iccbot.client.obj.unitHnadler.ictg;

import com.iscas.iccbot.analyze.model.analyzeModel.Attribute;
import com.iscas.iccbot.analyze.model.analyzeModel.ObjectSummaryModel;
import com.iscas.iccbot.analyze.utils.CollectionUtils;
import com.iscas.iccbot.analyze.utils.SootUtils;
import com.iscas.iccbot.analyze.utils.ValueObtainer;
import com.iscas.iccbot.client.obj.model.component.BundleType;
import com.iscas.iccbot.client.obj.model.component.ExtraData;
import com.iscas.iccbot.client.obj.model.ctg.IntentSummaryModel;
import com.iscas.iccbot.client.obj.model.ctg.SendOrReceiveICCInfo;
import com.iscas.iccbot.client.obj.target.ctg.CTGAnalyzerHelper;
import com.iscas.iccbot.client.obj.unitHnadler.UnitHandler;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JCastExpr;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.toolkits.scalar.UnitValueBoxPair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class GetIntentExtraHandler extends UnitHandler {
    IntentSummaryModel intentSummary;
    SootMethod sootMethod;
    Unit unit;
    public GetIntentExtraHandler(SootMethod sootMethod, Unit unit) {
        super();
        this.sootMethod = sootMethod;
        this.unit = unit;
    }


    @Override
    public void handleSingleObject(ObjectSummaryModel singleObject) {
        this.intentSummary = (IntentSummaryModel) singleObject;
        this.intentSummary.getDataHandleList().add(unit);
        getExtraAPIAnalyze(); // extra
    }

    /**
     * getExtraAPIAnalyze
     *
     */
    void getExtraAPIAnalyze() {
        // for each unit contains extras
        String type = CTGAnalyzerHelper.getTypeOfIntentExtra(unit.toString());
        Map<String, List<ExtraData>> param_list;
        param_list = getParamList(unit);
        if (param_list == null)
            return;
        if (SootUtils.isBundleExtra(type)) {
            for (Entry<String, List<ExtraData>> en : param_list.entrySet()) {
                for (ExtraData ed : en.getValue()) {
                    BundleType bundle_type = genBundleType(unit, ed, 0);
                    if (bundle_type == null) {
                        return;
                    }
                    bundle_type.setType(type);
                    ed.setType(bundle_type);
                }
            }
        } else if (SootUtils.isExtrasExtra(type)) {
            BundleType bundle_type = null;
            try {
                bundle_type = genBundleType(unit, null, 0);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            if (bundle_type == null) {
                return;
            }
            List<ExtraData> bundleList = new ArrayList<ExtraData>(bundle_type.getExtraDatas());
            param_list.put(unit.toString(), bundleList);
        } else if (SootUtils.isParOrSerExtra(type)) {
            for (Entry<String, List<ExtraData>> en : param_list.entrySet()) {
                for (ExtraData ed : en.getValue()) {
                    ed.setType(type);
                    ed.setObjName(getObjectNameforReflection(unit));
                }
            }
        } else {
            // String type
            for (Entry<String, List<ExtraData>> en : param_list.entrySet()) {
                for (ExtraData ed : en.getValue())
                    ed.setType(type);
            }
        }
        // entry class
        addExtraValue2Set( param_list, intentSummary.getGetExtrasCandidateList());
    }

    /**
     * get param list
     *
     * @param u
     * @return
     */
    public Map<String, List<ExtraData>> getParamList(Unit u) {
        Value var = null;
        try {
            var = getVarInExtraStmt(u);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Map<String, List<ExtraData>> param_list = new HashMap<String, List<ExtraData>>();
        try {
            ValueObtainer vo = new ValueObtainer(methodSig, "");
            param_list = new HashMap<>();
            // same u -->multiple str
            List<ExtraData> eds = new ArrayList<ExtraData>();
            param_list.put(u.toString(), eds);
            if (var != null) {
                List<String> reslist = vo.getValueOfVar(var, u, 0).getValues();
                for (String res : reslist) {
                    ExtraData ed = new ExtraData();
                    ed.setName(res);
                    eds.add(ed);
                    SendOrReceiveICCInfo getTriple = new SendOrReceiveICCInfo(u, sootMethod.getSignature(), SootUtils.getIdForUnit(u, sootMethod));
                    getTriple.setKey(res);
                    intentSummary.getReceiveTriple().add(getTriple);
                }
            } else {
                ExtraData ed = new ExtraData();
                eds.add(ed);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (param_list == null || param_list.size() == 0) {
            return null;
        }
        return param_list;
    }

    /**
     * get object name of the par and ser objs
     *
     * @param u
     * @return
     */
    private String getObjectNameforReflection(Unit u) {
        String res = "";
        List<UnitValueBoxPair> use_var_list = SootUtils.getUseOfLocal(methodSig, u);

        for (UnitValueBoxPair vb : use_var_list) {
            if (vb.getUnit().getDefBoxes().size() > 0 && vb.getUnit().getUseBoxes().size() > 0) {
                if (vb.getUnit().getUseBoxes().get(0).getValue() instanceof JCastExpr) {
                    JCastExpr cast = (JCastExpr) vb.getUnit().getUseBoxes().get(0).getValue();
                    if (cast.getCastType().toString().endsWith("[]"))
                        res = cast.getCastType().toString().replace("[]", "");
                    else
                        res = cast.getCastType().toString();
                }
            }
        }
        return res;
    }

    /**
     * @param u
     * @return
     * @throws Exception
     */
    private Value getVarInExtraStmt(Unit u) throws Exception {
        Value res = null;
        if (u instanceof JAssignStmt) {
            JAssignStmt jas = (JAssignStmt) u;
            ValueBox ads = jas.getRightOpBox();
            Value v = ads.getValue();
            if (v instanceof JVirtualInvokeExpr) {
                JVirtualInvokeExpr jvie = (JVirtualInvokeExpr) v;
                if (jvie.getArgCount() == 0)
                    res = null;
                else
                    res = jvie.getArg(0);
            }
        } else if (u instanceof JVirtualInvokeExpr) {
            JVirtualInvokeExpr jvie = (JVirtualInvokeExpr) u;
            if (jvie.getArgCount() == 0)
                res = null;
            else
                res = jvie.getArg(0);
        } else if (u instanceof JInvokeStmt) {
            JInvokeStmt jis = (JInvokeStmt) u;
            if (jis.getInvokeExpr().getArgCount() == 0)
                res = null;
            else
                res = jis.getInvokeExpr().getArg(0);
        }
        return res;
    }

    /**
     * gen_bundle_type
     *
     * @param u
     * @return
     * @throws Exception
     */
    public BundleType genBundleType(Unit u, ExtraData parent, int depth) {
        BundleType bt = new BundleType();
        if (depth > 10)
            return bt;
        List<UnitValueBoxPair> use_var_list = SootUtils.getUseOfLocal(methodSig, u);
        if (use_var_list == null)
            return bt;
        for (int i = 0; i < use_var_list.size(); i++) {
            Unit useUnit = use_var_list.get(i).getUnit();
            if (!CTGAnalyzerHelper.isGetBundleExtraMethod(useUnit.toString()))
                continue;
            Map<String, List<ExtraData>> param_list = getParamList(useUnit);
            if (param_list == null)
                continue;
            String type = CTGAnalyzerHelper.getTypeOfGetBundleExtra(useUnit.toString());

            if (!SootUtils.isBundleExtra(type) && !SootUtils.isExtrasExtra(type)) {
                for (Entry<String, List<ExtraData>> en : param_list.entrySet()) {
                    for (ExtraData ed : en.getValue()) {
                        ed.setType(type);
                        ed.setParent(parent);
                    }
                    bt.put(en.getKey(), en.getValue());
                }
            } else {
                BundleType bundle_type = null;
                bundle_type = genBundleType(useUnit, parent, depth + 1);
                if (bundle_type == null) {
                    continue;
                }
                bundle_type.setType(type);
                for (Entry<String, List<ExtraData>> en : param_list.entrySet()) {
                    for (ExtraData ed : en.getValue()) {
                        ed.setType(bundle_type);
                        ed.setParent(parent);
                    }
                    bt.put(en.getKey(), en.getValue());
                }
            }
        }
        return bt;
    }

    public void addExtraValue2Set( Map<String, List<ExtraData>> param_list,
                                  BundleType bundleType) {
        String res = "";
        for (Entry<String, List<ExtraData>> en : param_list.entrySet()) {
            bundleType.put(en.getKey(), en.getValue());
            for (ExtraData ed : en.getValue()) {
                res += ed.toString().trim();
            }
        }
        if (res.endsWith(","))
            res = res.substring(0, res.length() - 1);
        // key and value
        Attribute attr = new Attribute(methodUnderAnalyze.getSignature() + "," + unit.toString() + "," +unit.hashCode(), "extra", res,
                "equals");
        CollectionUtils.add_attribute_to_map(attr.getId(), attr, appModel.getUnit2Attribute());
    }

}
