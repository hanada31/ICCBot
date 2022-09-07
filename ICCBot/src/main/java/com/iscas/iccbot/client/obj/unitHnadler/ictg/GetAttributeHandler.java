package com.iscas.iccbot.client.obj.unitHnadler.ictg;

import com.iscas.iccbot.analyze.model.analyzeModel.Attribute;
import com.iscas.iccbot.analyze.model.analyzeModel.ObjectSummaryModel;
import com.iscas.iccbot.analyze.model.sootAnalysisModel.Context;
import com.iscas.iccbot.analyze.model.sootAnalysisModel.NestableObj;
import com.iscas.iccbot.analyze.utils.*;
import com.iscas.iccbot.analyze.utils.output.PrintUtils;
import com.iscas.iccbot.client.obj.dataHnadler.DataHandler;
import com.iscas.iccbot.client.obj.model.ctg.IntentSummaryModel;
import com.iscas.iccbot.client.obj.model.ctg.SendOrReceiveICCInfo;
import com.iscas.iccbot.client.obj.unitHnadler.UnitHandler;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.internal.AbstractInstanceInvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.shimple.PhiExpr;
import soot.toolkits.scalar.UnitValueBoxPair;

import java.util.*;
import java.util.Map.Entry;

public class GetAttributeHandler extends UnitHandler {
    Context context;
    IntentSummaryModel intentSummary;
    SootMethod sootMethod;
    Unit unit;
    public GetAttributeHandler(SootMethod sootMethod, Unit unit) {
        super();
        this.sootMethod = sootMethod;
        this.unit = unit;
    }


    @Override
    public void handleSingleObject(ObjectSummaryModel singleObject) {
        this.handleSingleObject(new Context(), singleObject);
    }

    @Override
    public void handleSingleObject(Context context, ObjectSummaryModel singleObject) {
        this.context = context;
        this.intentSummary = (IntentSummaryModel) singleObject;
        this.intentSummary.getDataHandleList().add(unit);
        getAttriAPIAnalyze(); // acid
    }

    /**
     * getAttriAPIAnalyze
     *
     */
    void getAttriAPIAnalyze() {
        Set<String> resSet = new HashSet<String>();
        if (unit.toString().contains("getAction(")) {
            DataHandler handler = DataHandler.getDataHandler("getAction");
            resSet = getAttriStr(unit, "action");
            generatehandler(handler, resSet);
            SendOrReceiveICCInfo info = new SendOrReceiveICCInfo(unit, sootMethod.getSignature(), SootUtils.getIdForUnit(unit, sootMethod));
            info.setKeyAndValue("action",resSet);
            intentSummary.getReceiveTriple().add(info);
        }
        else if (unit.toString().contains("getCategories(")) {
            DataHandler handler = DataHandler.getDataHandler("getCategory");
            resSet.addAll(getAttriStr(unit, "category"));
            generatehandler(handler, resSet);
            SendOrReceiveICCInfo info = new SendOrReceiveICCInfo(unit, sootMethod.getSignature(), SootUtils.getIdForUnit(unit, sootMethod));
            info.setKeyAndValue("category",resSet);
            intentSummary.getReceiveTriple().add(info);
        }
        else if (unit.toString().contains("getData(") || unit.toString().contains("getDataString(")) {
            DataHandler handler = DataHandler.getDataHandler("getData");
            resSet = getAttriStr(unit, "data");
            resSet.addAll(getSubAttriofData(unit));
            generatehandler(handler, resSet);
            SendOrReceiveICCInfo info = new SendOrReceiveICCInfo(unit, sootMethod.getSignature(), SootUtils.getIdForUnit(unit, sootMethod));
            info.setKeyAndValue("data",resSet);
            intentSummary.getReceiveTriple().add(info);
        }
        else if (unit.toString().contains("getType(")) {
            DataHandler handler = DataHandler.getDataHandler("getType");
            resSet = getAttriStr(unit, "type");
            generatehandler(handler, resSet);
            SendOrReceiveICCInfo info = new SendOrReceiveICCInfo(unit, sootMethod.getSignature(), SootUtils.getIdForUnit(unit, sootMethod));
            info.setKeyAndValue("type",resSet);
            intentSummary.getReceiveTriple().add(info);
        }
    }

    private void generatehandler(DataHandler dataHandler, Set<String> resSet) {
        Set<String> resSet2 = new HashSet<>();
        for (String ele : resSet) {
            if (ele != null) resSet2.add(ele);
        }
        dataHandler.handleData(intentSummary, className, resSet2);
    }

    /**
     * get Attri Str
     *
     * @param u
     * @param attr_type
     * @return
     */
    Set<String> getAttriStr(Unit u, String attr_type) {
        Set<String> resSet = new HashSet<String>();
        // get its string
        List<UnitValueBoxPair> use_var_list = SootUtils.getUseOfLocal(methodSig, u);
        Map<List, String> todoUnit2Condition = new HashMap<List, String>();
        // get obtained data may be transfered first before compare
        resSet.addAll(completeTodoList("", u, use_var_list, todoUnit2Condition, new HashSet<Unit>(), attr_type));
        todoUnit2Condition.put(use_var_list, "");
        for (Entry<List, String> en : todoUnit2Condition.entrySet()) {
            List<UnitValueBoxPair> unitList = en.getKey();
            String leftCondition = en.getValue();
            for (UnitValueBoxPair useUnitBox : unitList) {
                Unit useUnit = useUnitBox.getUnit();
                Value inputVar = useUnitBox.getValueBox().getValue();
                // ============bool type==========================
                if (useUnit instanceof JAssignStmt) {
                    if (useUnit.toString().contains("contains")) {
                        Set<String> candidates = getValueofUnit2Set(useUnit, inputVar, attr_type);
                        Attribute attr = new Attribute(methodSig + "," + useUnit.toString() + "," + useUnit.hashCode(),
                                attr_type, PrintUtils.printSet(candidates), "contains", leftCondition, u);
                        CollectionUtils.add_attribute_to_map(attr.getId(), attr, appModel.getUnit2Attribute());
                        resSet.addAll(candidates);
                    } else if (useUnit.toString().contains("equals") || useUnit.toString().contains("contentEquals")) { // equals
                        Set<String> candidates = getValueofUnit2Set(useUnit, inputVar, attr_type);
                        Attribute attr = new Attribute(methodSig + "," + useUnit.toString() + "," + useUnit.hashCode(),
                                attr_type, PrintUtils.printSet(candidates), "equals", leftCondition, u);
                        // same key in jimple

                        CollectionUtils.add_attribute_to_map(attr.getId(), attr, appModel.getUnit2Attribute());
                        resSet.addAll(candidates);
                    } else if (useUnit.toString().contains("startsWith")) {
                        Set<String> candidates = getValueofUnit2Set(useUnit, inputVar, attr_type);
                        Attribute attr = new Attribute(methodSig + "," + useUnit.toString() + "," + useUnit.hashCode(),
                                attr_type, PrintUtils.printSet(candidates), "startsWith", leftCondition, u);
                        CollectionUtils.add_attribute_to_map(attr.getId(), attr, appModel.getUnit2Attribute());
                        resSet.addAll(candidates);
                    } else if (useUnit.toString().contains("endsWith")) {
                        Set<String> candidates = getValueofUnit2Set(useUnit, inputVar, attr_type);
                        Attribute attr = new Attribute(methodSig + "," + useUnit.toString() + "," + useUnit.hashCode(),
                                attr_type, PrintUtils.printSet(candidates), "endsWith", leftCondition, u);
                        CollectionUtils.add_attribute_to_map(attr.getId(), attr, appModel.getUnit2Attribute());
                        resSet.addAll(candidates);
                    } else if (useUnit.toString().contains("isEmpty")) {
                        Attribute attr = new Attribute(methodSig + "," + useUnit.toString() + "," + useUnit.hashCode(),
                                attr_type, "Empty", "isEmpty", leftCondition, u);
                        CollectionUtils.add_attribute_to_map(attr.getId(), attr, appModel.getUnit2Attribute());
                        resSet.add("notEmpty");
                    }
                } else if (useUnit instanceof JIfStmt) {
                    if (useUnit.toString().contains("!= null")) {
                        Attribute attr = new Attribute(methodSig + "," + useUnit.toString() + "," + useUnit.hashCode(),
                                attr_type, "null", "nullChecker", leftCondition, u);
                        CollectionUtils.add_attribute_to_map(attr.getId(), attr, appModel.getUnit2Attribute());
                        resSet.add("notEmpty");
                    } else if (useUnit.toString().contains("== null")) {
                        Attribute attr = new Attribute(methodSig + "," + useUnit.toString() + "," + useUnit.hashCode(),
                                attr_type, "null", "nullChecker", leftCondition, u);
                        CollectionUtils.add_attribute_to_map(attr.getId(), attr, appModel.getUnit2Attribute());
                        resSet.add("notEmpty");
                    } else if (useUnit.toString().contains("== ")) { // equals
                        Set<String> candidates = getValueofUnit2Set(useUnit, inputVar, attr_type);
                        Attribute attr = new Attribute(methodSig + "," + useUnit.toString() + "," + useUnit.hashCode(),
                                attr_type, PrintUtils.printSet(candidates), "equals", leftCondition, u);
                        CollectionUtils.add_attribute_to_map(attr.getId(), attr, appModel.getUnit2Attribute());
                        resSet.addAll(candidates);
                    } else if (useUnit.toString().contains("!= ")) { // equals
                        Set<String> candidates = getValueofUnit2Set(useUnit, inputVar, attr_type);
                        Attribute attr = new Attribute(methodSig + "," + useUnit.toString() + "," + useUnit.hashCode(),
                                attr_type, PrintUtils.printSet(candidates), "equals", leftCondition, u);
                        CollectionUtils.add_attribute_to_map(attr.getId(), attr, appModel.getUnit2Attribute());
                        resSet.addAll(candidates);
                    }
                }
            }
        }
        if (resSet.size() == 0) {
            String key = methodUnderAnalyze.getSignature() + "," + u.toString() + "," + u.hashCode();
            Attribute attr = new Attribute(key, attr_type, "Empty", "isEmpty", -1);
            CollectionUtils.add_attribute_to_map(attr.getId(), attr, appModel.getUnit2Attribute());
            resSet.add("notEmpty");
        }
        return resSet;
    }

    /**
     * complete Todo List
     *
     * @param condition
     * @param defUnit
     * @param use_var_list
     * @param todoUnit2Condition
     * @param historySet
     * @param attr_type
     * @return
     */
    private Set<String> completeTodoList(String condition, Unit defUnit, List<UnitValueBoxPair> use_var_list,
                                         Map<List, String> todoUnit2Condition, HashSet<Unit> historySet, String attr_type) {
        Set<String> res = new HashSet<String>();
        List<UnitValueBoxPair> resList = SootUtils.getComparedUnit(use_var_list);
        todoUnit2Condition.put(resList, condition);
        for (UnitValueBoxPair it : use_var_list) {
            Unit useUnit = it.getUnit();
            if (historySet.contains(useUnit))
                continue;
            else
                historySet.add(useUnit);
            String newCon = getConditionFromUnit(useUnit);
            if (newCon != null) {
                res.addAll(completeTodoList(condition + "," + newCon, useUnit,
                        SootUtils.getUseOfLocal(methodSig, useUnit), todoUnit2Condition, historySet, attr_type));
            }
        }
        return res;
    }

    /**
     * get Condition From Unit
     *
     * @param useUnit
     * @return
     */
    private String getConditionFromUnit(Unit useUnit) {
        if (useUnit instanceof PhiExpr)
            return "Phi";
        else if (useUnit.toString().contains("toString("))
            return "";
        else if (useUnit.toString().contains("valueOf("))
            return "";
        else if (useUnit.toString().contains("toLowerCase("))
            return "";
        else if (useUnit.toString().contains("toUpperCase("))
            return "";
        else if (useUnit.toString().contains("trim("))
            return "";
        else if (useUnit.toString().contains("substring(")) {
            if (useUnit instanceof JAssignStmt) {
                JAssignStmt jas1 = (JAssignStmt) useUnit;
                AbstractInstanceInvokeExpr invokeStmt = (AbstractInstanceInvokeExpr) jas1.getRightOpBox().getValue();
                ValueObtainer vo = new ValueObtainer(methodSig, ConstantUtils.FLAGATTRI);
                int b = 0, e = 0;
                NestableObj res0 = vo.getValueOfVar(invokeStmt.getArg(0), useUnit, 0);
                if (res0.getValues().size() > 0) {
                    String str_b = res0.getValues().get(0);
                    if (StringUtils.isInteger(str_b)) {
                        try {
                            b = Integer.parseInt(str_b);
                        } catch (Exception NumberFormatException) {
                        }
                        if (invokeStmt.getArgCount() == 1) {
                            return "substring " + b;
                        }
                        if (invokeStmt.getArgCount() == 2) {
                            NestableObj res1 = vo.getValueOfVar(invokeStmt.getArg(1), useUnit, 0);
                            if (res1.getValues().size() > 0) {
                                String str_e = res1.getValues().get(0);
                                if (StringUtils.isInteger(str_e)) {
                                    try {
                                        e = Integer.parseInt(str_e);
                                    } catch (Exception NumberFormatException) {
                                    }
                                    return "substring " + b + " " + (e - b);
                                }
                            }
                        }
                    }
                }
                return "";
            }
        } else if (useUnit.toString().contains("charAt(")) {
            JAssignStmt jas1 = (JAssignStmt) useUnit;
            AbstractInstanceInvokeExpr invokeStmt = (AbstractInstanceInvokeExpr) jas1.getRightOpBox().getValue();
            ValueObtainer vo = new ValueObtainer(methodSig, ConstantUtils.FLAGATTRI);

            int b = 0;
            NestableObj res1s0 = vo.getValueOfVar(invokeStmt.getArg(0), useUnit, 0);
            if (res1s0.getValues().size() > 0) {
                String str_b = res1s0.getValues().get(0);
                if (StringUtils.isInteger(str_b)) {
                    try {
                        b = Integer.parseInt(str_b);
                    } catch (Exception NumberFormatException) {
                    }
                    if (invokeStmt.getArgCount() == 1) {
                        return "charAt " + b;
                    }
                }
            }
            return "";
        } else if (useUnit.toString().contains("concat(")) {
            JAssignStmt jas1 = (JAssignStmt) useUnit;
            AbstractInstanceInvokeExpr invokeStmt = (AbstractInstanceInvokeExpr) jas1.getRightOpBox().getValue();
            ValueObtainer vo = new ValueObtainer(methodSig, ConstantUtils.FLAGATTRI);
            List<String> vals = vo.getValueOfVar(invokeStmt.getArg(0), useUnit, 0).getValues();
            if (vals.size() > 0) {
                String str = vals.get(0);
                return "concat " + str;
            }
            return "";
        }
        return null;
    }

    /**
     * get Sub Attri of Data
     *
     * @param u
     * @return
     */
    private Set<String> getSubAttriofData(Unit u) {
        Set<String> resSet = new HashSet<String>();
        Set<String> schemeSet = new HashSet<String>();
        Set<String> hostSet = new HashSet<String>();
        Set<String> portSet = new HashSet<String>();
        Set<String> pathSet = new HashSet<String>();
        Set<String> authoritySet = new HashSet<String>();
        List<UnitValueBoxPair> use_var_list = SootUtils.getUseOfLocal(methodSig, u);
        for (UnitValueBoxPair useUnitBox : use_var_list) {
            Unit useUnit = useUnitBox.getUnit();
            if (useUnit.toString().contains("getScheme")) {
                schemeSet.addAll(getAttriStr(useUnit, "scheme"));
            } else if (useUnit.toString().contains("getHost")) {
                hostSet.addAll(getAttriStr(useUnit, "host"));
            } else if (useUnit.toString().contains("getPort")) {
                portSet.addAll(getAttriStr(useUnit, "port"));
            } else if (useUnit.toString().contains("getPath")) {
                pathSet.addAll(getAttriStr(useUnit, "path"));
            } else if (useUnit.toString().contains("getAuthority")) {
                authoritySet.addAll(getAttriStr(useUnit, "authority"));
            } else if (useUnit.toString().contains("getEncodedPath")) {
                pathSet.addAll(getAttriStr(useUnit, "path"));
            }
        }
        if (schemeSet.size() == 0)
            schemeSet.add("mSheme");
        if (hostSet.size() == 0)
            hostSet.add("mHost");
        if (portSet.size() == 0)
            portSet.add("mPort");
        if (pathSet.size() == 0)
            pathSet.add("mPath");
        if (authoritySet.size() == 0)
            authoritySet.add("mAuthority");

        // <scheme>:(<authority>)|(<host>:<port>)/<path>
        for (String p1 : schemeSet)
            for (String p2 : hostSet)
                for (String p3 : portSet)
                    for (String p4 : pathSet)
                        resSet.add(StringUtils.refineString(p1) + "://" + StringUtils.refineString(p2) + ":"
                                + StringUtils.refineString(p3) + "/" + StringUtils.refineString(p4));
        resSet.remove("mSheme://mHost:mPort/mPath");
        for (String p1 : schemeSet)
            for (String p2 : authoritySet)
                for (String p3 : pathSet)
                    resSet.add(StringUtils.refineString(p1) + ":" + StringUtils.refineString(p2) + "/"
                            + StringUtils.refineString(p3));
        resSet.remove("mSheme:mAuthority/mPath");
        return resSet;
    }

    /**
     * get Value of Unit to Set
     *
     * @param useUnit
     * @param inputVar
     * @param attr_type
     * @return
     */
    private Set<String> getValueofUnit2Set(Unit useUnit, Value inputVar, String attr_type) {
        Set<String> resSet = new HashSet<String>();
        if (useUnit instanceof JAssignStmt) {
            // action equals xxx or xxx equals action, make sure which var is
            // target
            JAssignStmt jas1 = (JAssignStmt) useUnit;
            Value strVal = null;
            if (jas1.getRightOpBox().getValue() instanceof AbstractInstanceInvokeExpr) {
                AbstractInstanceInvokeExpr invokeStmt = (AbstractInstanceInvokeExpr) jas1.getRightOpBox().getValue();
                if (invokeStmt.getArg(0).equals(inputVar)) // actrionVar equals
                    // xxx
                    strVal = invokeStmt.getBase();
                else
                    // xxx equals actrionVar
                    strVal = invokeStmt.getArg(0);
            } else if (jas1.getRightOpBox().getValue() instanceof JStaticInvokeExpr) {
                JStaticInvokeExpr invokeStmt = (JStaticInvokeExpr) jas1.getRightOpBox().getValue();
                if (invokeStmt.getArg(0).equals(inputVar)) // actrionVar equals
                    // xxx
                    strVal = invokeStmt.getArg(1);
                else
                    // xxx equals actrionVar
                    strVal = invokeStmt.getArg(0);
            }
            ValueObtainer vo = new ValueObtainer(methodSig, ConstantUtils.FLAGATTRI);
            for (String res : vo.getValueOfVar(strVal, useUnit, 0).getValues())
                resSet.add(res);
        } else if (useUnit instanceof JIfStmt) {
            JIfStmt jif = (JIfStmt) useUnit;
            if (jif.getCondition().getUseBoxes().size() > 1) {
                String res = jif.getCondition().getUseBoxes().get(1).getValue().toString();
                resSet.add(res);
            }
        }
        return resSet;
    }

}
