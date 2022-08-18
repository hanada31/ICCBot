package com.iscas.iccbot.analyze.unitHnadler.stmtHandler;

import soot.JastAddJ.Expr;
import soot.JastAddJ.IfStmt;
import soot.Unit;
import soot.Value;

import java.util.ArrayList;
import java.util.List;

public class IfStmtHandler extends StmtHandler {
    private IfStmt ifStmt;

    @Override
    public void init(Unit stmt) {
        ifStmt = (IfStmt) stmt;
    }

    @Override
    public Value getLeftValue() {
        return null;

    }

    public Expr getCondition() {
        return ifStmt.getCondition();

    }

    @Override
    public List<Value> getRightValues() {
        List<Value> vals = new ArrayList<Value>();
        return vals;
    }
}
