package com.iscas.iccbot.analyze.unitHnadler.stmtHandler;

import soot.Unit;
import soot.Value;
import soot.jimple.ReturnStmt;

import java.util.ArrayList;
import java.util.List;

public class ReturnStmtHandler extends StmtHandler {
    private ReturnStmt returnStmt;

    @Override
    public void init(Unit stmt) {
        returnStmt = (ReturnStmt) stmt;
    }

    @Override
    public Value getLeftValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Value> getRightValues() {
        List<Value> vals = new ArrayList<Value>();
        return vals;
    }

}
