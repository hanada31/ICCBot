package main.java.client.obj.unitHnadler.fragment;

import main.java.analyze.model.analyzeModel.SingleObjectModel;
import main.java.analyze.model.sootAnalysisModel.Context;
import main.java.analyze.model.sootAnalysisModel.Counter;
import main.java.analyze.utils.SootUtils;
import main.java.analyze.utils.ValueObtainer;
import main.java.client.obj.model.fragment.SingleFragmentModel;
import main.java.client.obj.unitHnadler.UnitHandler;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.internal.JVirtualInvokeExpr;

public class DialogShowHandler extends UnitHandler {
	Context context;
	SingleFragmentModel singleFrag;

	@Override
	public void handleSingleObject(SingleObjectModel singleObject) {
		this.handleSingleObject(new Context(), singleObject);
	}

	@Override
	public void handleSingleObject(Context context, SingleObjectModel singleObject) {
		this.context = context;
		this.singleFrag = (SingleFragmentModel) singleObject;
		this.singleFrag.getSendFragment2Start().add(unit);
		showAnalyze();
	}

	@Override
	public void handleSingleObject(Context oldContextwithRealValue, SingleObjectModel singleObject, Unit targetUnit) {
		this.oldContextwithRealValue = oldContextwithRealValue;
		this.singleFrag = (SingleFragmentModel) singleObject;
		this.singleFrag.getDataHandleList().add(unit);
		this.targetUnit = targetUnit;
		showAnalyze();
	}

	private void showAnalyze() {
		InvokeExpr invMethod = SootUtils.getSingleInvokedMethod(unit);
		if (invMethod instanceof JVirtualInvokeExpr) {
			JVirtualInvokeExpr jv = (JVirtualInvokeExpr) invMethod;
			ValueObtainer vo = new ValueObtainer(methodSig, "", new Context(), new Counter());
			for (String res : vo.getValueofVar(jv.getBase(), unit, 0).getValues()) {
				if (res.contains("new ")) {
					transformFragmentMethod(methodUnderAnalyze.getDeclaringClass(), unit, res.replace("new ", ""),
							methodUnderAnalyze);
				}
			}
		}

	}

	private void transformFragmentMethod(SootClass sourceCls, Unit u, String res, SootMethod sm) {
		for (SootClass sc : Scene.v().getApplicationClasses()) {
			if (!sc.getName().equals(res))
				continue;
			singleFrag.getAddList().add(sc.getName());
			singleFrag.getSetDestinationList().add(sc.getName());
		}
		return;

	}

}
