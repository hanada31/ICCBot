package com.iscas.iccbot.analyze.unitHnadler.stmtHandler;

import soot.Unit;
import soot.Value;
import soot.jimple.*;

import java.util.List;

public abstract class StmtHandler {

    public void init(Unit stmt) {
    }

    ;

    public abstract Value getLeftValue();

    public abstract List<Value> getRightValues();

    // public abstract Expr getExpr();

    /**
     * get the correct handler of target unit stmt = breakpoint_stmt |
     * assign_stmt | enter_monitor_stmt | goto_stmt | if_stmt | invoke_stmt |
     * lookup_switch_stmt | nop_stmt | ret_stmt | return_stmt | return_void_stmt
     * | table_switch_stmt | throw_stmt;
     */
    public static StmtHandler getStmtHandler(Unit u) {
        if (u == null)
            return null;
        StmtHandler handler = null;
        if (u instanceof AssignStmt) {
            handler = new AssignStmtHandler();
        } else if (u instanceof IfStmt) {
            handler = new IfStmtHandler();
        } else if (u instanceof InvokeStmt) {
            handler = new InvokeStmtHandler();
        } else if (u instanceof RetStmt) {
            handler = new RetStmtHandler();
        } else if (u instanceof ReturnStmt) {
            handler = new ReturnStmtHandler();
        } else if (u instanceof ReturnVoidStmt) {
            handler = new ReturnVoidStmtHandler();
        } else if (u instanceof ThrowStmt) {
            handler = new ThrowStmtHandler();
        }
        if (handler != null)
            handler.init(u);
        return handler;
    }

}
