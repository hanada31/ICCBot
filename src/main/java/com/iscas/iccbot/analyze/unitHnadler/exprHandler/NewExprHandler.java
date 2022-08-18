package com.iscas.iccbot.analyze.unitHnadler.exprHandler;

import soot.Value;
import soot.jimple.NewExpr;

import java.util.ArrayList;
import java.util.List;

public class NewExprHandler extends ExprHandler {
    private NewExpr exp;

    @Override
    public void init(Value val) {
        exp = (NewExpr) val;
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
