package com.iscas.iccbot.analyze.unitHnadler.stmtHandler;

import com.iscas.iccbot.analyze.unitHnadler.exprHandler.ExprHandler;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.jimple.*;

import java.util.ArrayList;
import java.util.List;

public class AssignStmtHandler extends StmtHandler {

    private AssignStmt assign;

    @Override
    public void init(Unit stmt) {
        assign = (AssignStmt) stmt;
    }

    @Override
    public Value getLeftValue() {
        return assign.getLeftOp();

    }

    @Override
    public List<Value> getRightValues() {
        // rvalue = constant | expr | local | instance_field_ref | array_ref |
        // static_field_ref |next_next_stmt_address;
        List<Value> vals = new ArrayList<Value>();
        if (assign.getRightOp() instanceof Expr) {
            ExprHandler handler = ExprHandler.getExprHandler(assign.getRightOp());
            return handler.getRightValues();
        } else if (assign.getRightOp() instanceof Constant) {
            return vals;
        } else if (assign.getRightOp() instanceof Local) {
            vals.add(assign.getRightOp());
            return vals;
        } else if (assign.getRightOp() instanceof StaticFieldRef) {
            return vals;
        } else if (assign.getRightOp() instanceof InstanceFieldRef) {
            return vals;
        } else if (assign.getRightOp() instanceof ArrayRef) {
            return vals;
        }
        return vals;
    }

}
