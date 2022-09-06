package com.iscas.iccbot.analyze.unitHnadler.stmtHandler;

import soot.Unit;
import soot.Value;
import soot.jimple.RetStmt;

import java.util.ArrayList;
import java.util.List;

public class RetStmtHandler extends StmtHandler {

    private RetStmt retStmt;

    @Override
    public void init(Unit stmt) {
        retStmt = (RetStmt) stmt;
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
