package com.iscas.iccbot.client.obj;

import com.iscas.iccbot.client.obj.unitHnadler.UnitHandler;
import soot.SootMethod;
import soot.Unit;

import java.util.List;

public interface AnalyzerHelper {

    public abstract List<String> getObjectIdentifier();

    public abstract boolean isTopTargetUnit(Unit unit);

    public abstract boolean isWrapperTopTargetUnit(Unit unit);

    public abstract String getTypeofUnit(SootMethod m, Unit unit);

    public abstract UnitHandler getUnitHandler(SootMethod sootMethod, Unit u);

    public abstract boolean isCreateMethod(Unit unit);

    public abstract boolean isStaticCreateMethod(Unit unit);

    public abstract boolean isReceiveFromParaMethod(Unit unit);

    public abstract boolean isPassOutMethod(Unit u);

    public abstract boolean isReceiveFromRetValue(Unit u);

    public abstract boolean isMyTarget(Unit u);

}
