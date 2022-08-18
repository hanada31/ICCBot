package com.iscas.iccbot.analyze.unitHnadler.exprHandler;

import soot.Value;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;

import java.util.ArrayList;
import java.util.List;

public class InvokeExprHandler extends ExprHandler {
    private InvokeExpr exp;

    @Override
    public void init(Value val) {
        exp = (InvokeExpr) val;
    }

    @Override
    public Value getLeftValue() {
        return null;
    }

    @Override
    public List<Value> getRightValues() {
        List<Value> vals = new ArrayList<Value>();
        vals = exp.getArgs();
        if (exp instanceof InstanceInvokeExpr)
            vals.add(((InstanceInvokeExpr) exp).getBase());
        return vals;
    }

}
