package com.iscas.iccbot.analyze.unitHnadler.exprHandler;

import soot.Value;
import soot.jimple.NewArrayExpr;

import java.util.ArrayList;
import java.util.List;

public class NewArrayExprHandler extends ExprHandler {
    private NewArrayExpr exp;

    @Override
    public void init(Value val) {
        exp = (NewArrayExpr) val;
    }

    @Override
    public Value getLeftValue() {
        return null;
    }

    @Override
    public List<Value> getRightValues() {
        List<Value> vals = new ArrayList<Value>();
        return vals;
    }

}
