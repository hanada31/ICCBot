package com.iscas.iccbot.analyze.unitHnadler.exprHandler;

import soot.Value;
import soot.jimple.*;

import java.util.List;

public abstract class ExprHandler {
    public abstract void init(Value val);

    public abstract Value getLeftValue();

    public abstract List<Value> getRightValues();

    /**
     * get the correct handler of target expr value
     */
    public static ExprHandler getExprHandler(Value v) {
        if (v == null)
            return null;
        ExprHandler handler = null;
        if (v instanceof BinopExpr) {
            handler = new BinopExprHandler();
        } else if (v instanceof CastExpr) {
            handler = new CastExprHandler();
        } else if (v instanceof InstanceOfExpr) {
            handler = new InstanceOfExprHandler();
        } else if (v instanceof InvokeExpr) {
            handler = new InvokeExprHandler();
        } else if (v instanceof NewArrayExpr) {
            handler = new NewArrayExprHandler();
        } else if (v instanceof NewExpr) {
            handler = new NewExprHandler();
        } else if (v instanceof UnopExpr) {
            handler = new UnopExprHandler();
        } else if (v instanceof NewMultiArrayExpr) {
            handler = new NewMultiArrayExprHandler();
        }
        if (handler != null)
            handler.init(v);
        return handler;
    }

}
