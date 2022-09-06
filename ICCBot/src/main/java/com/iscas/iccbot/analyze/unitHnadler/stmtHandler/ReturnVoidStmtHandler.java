package com.iscas.iccbot.analyze.unitHnadler.stmtHandler;

import soot.Unit;
import soot.Value;
import soot.jimple.ReturnVoidStmt;

import java.util.ArrayList;
import java.util.List;

public class ReturnVoidStmtHandler extends StmtHandler {
    private ReturnVoidStmt returnVoidStmt;

    @Override
    public void init(Unit stmt) {
        returnVoidStmt = (ReturnVoidStmt) stmt;
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
