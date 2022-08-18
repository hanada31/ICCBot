package com.iscas.iccbot.client.obj.target.ctg;

import com.iscas.iccbot.Analyzer;
import com.iscas.iccbot.Global;
import com.iscas.iccbot.analyze.utils.RAICCUtils;
import com.iscas.iccbot.analyze.utils.SootUtils;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

import java.util.List;
import java.util.Map;

/**
 * Analyzer Class
 *
 * @author hanada
 * @version 2.0
 */
public class ATGPreprocess extends Analyzer {

    @Override
    public void analyze() {
        Map<String, String> method2InstructionMap = Global.v().getAppModel().getMethod2InstructionMap();
        for (SootClass sc : Scene.v().getApplicationClasses()) {
            for (SootMethod sm : sc.getMethods()) {
                int i = 0;
                List<Unit> units = SootUtils.getUnitListFromMethod(sm);
                for (Unit u : units) {
                    if (CTGAnalyzerHelper.isSendIntent2IccMethod(u) || RAICCUtils.isWrapperMethods(u)) {
                        String res = method2InstructionMap.get(sm.getSignature());
                        if (res == null) {
                            res = i + "";
                            method2InstructionMap.put(sm.getSignature(), res);
                        } else {
                            res += "," + i;
                            method2InstructionMap.put(sm.getSignature(), res);
                        }
                    }
                    i++;
                }

            }
        }
        System.out.println("ATGPreprocess finish\n");
    }

}