package com.iscas.iccbot.analyze.unitHnadler.exprHandler;

import soot.Value;
import soot.jimple.CastExpr;

import java.util.ArrayList;
import java.util.List;

public class CastExprHandler extends ExprHandler {
    private CastExpr exp;

    @Override
    public void init(Value val) {
        exp = (CastExpr) val;
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
