package com.iscas.iccbot.analyze.unitHnadler.exprHandler;

import soot.Value;
import soot.jimple.UnopExpr;

import java.util.ArrayList;
import java.util.List;

public class UnopExprHandler extends ExprHandler {
    private UnopExpr exp;

    @Override
    public void init(Value val) {
        exp = (UnopExpr) val;
    }

    @Override
    public Value getLeftValue() {
        return null;
    }

    @Override
    public List<Value> getRightValues() {
        List<Value> vals = new ArrayList<Value>();
        vals.add(exp.getOp());
        return vals;
    }

}
