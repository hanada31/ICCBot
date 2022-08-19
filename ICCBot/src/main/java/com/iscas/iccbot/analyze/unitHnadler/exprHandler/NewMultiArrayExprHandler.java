package com.iscas.iccbot.analyze.unitHnadler.exprHandler;

import soot.Value;
import soot.jimple.NewMultiArrayExpr;

import java.util.ArrayList;
import java.util.List;

public class NewMultiArrayExprHandler extends ExprHandler {
    private NewMultiArrayExpr exp;

    @Override
    public void init(Value val) {
        exp = (NewMultiArrayExpr) val;
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
