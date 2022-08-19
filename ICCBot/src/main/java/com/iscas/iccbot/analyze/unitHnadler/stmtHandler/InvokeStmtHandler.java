package com.iscas.iccbot.analyze.unitHnadler.stmtHandler;

import com.iscas.iccbot.analyze.unitHnadler.exprHandler.ExprHandler;
import soot.Unit;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;

import java.util.List;

public class InvokeStmtHandler extends StmtHandler {

    private InvokeStmt invokeStmt;
    private InvokeExpr invokeExpr;

    @Override
    public void init(Unit stmt) {
        invokeStmt = (InvokeStmt) stmt;
        invokeExpr = invokeStmt.getInvokeExpr();
    }

    @Override
    public Value getLeftValue() {
        return ExprHandler.getExprHandler(invokeExpr).getLeftValue();
    }

    @Override
    public List<Value> getRightValues() {
        return ExprHandler.getExprHandler(invokeExpr).getRightValues();
    }

}
