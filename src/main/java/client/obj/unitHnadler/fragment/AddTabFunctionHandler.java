package main.java.client.obj.unitHnadler.fragment;

import java.io.IOException;
import main.java.analyze.model.analyzeModel.SingleObjectModel;
import main.java.analyze.model.sootAnalysisModel.Context;
import main.java.analyze.model.sootAnalysisModel.Counter;
import main.java.analyze.utils.ValueObtainer;
import main.java.client.obj.model.fragment.SingleFragmentModel;
import main.java.client.obj.unitHnadler.UnitHandler;
import soot.Unit;
import soot.Value;

public class AddTabFunctionHandler extends UnitHandler {
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
		try {
			addTabAnalyze();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleSingleObject(Context oldContextwithRealValue, SingleObjectModel singleObject, Unit targetUnit) {
		this.oldContextwithRealValue = oldContextwithRealValue;
		this.singleFrag = (SingleFragmentModel) singleObject;
		this.singleFrag.getDataHandleList().add(unit);
		this.targetUnit = targetUnit;
		try {
			addTabAnalyze();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addTabAnalyze() throws IOException {
		int id = 1;
		Value inputVar = getInputVar(id, unit);
		if (inputVar == null)
			return;
		Context objContextInner = new Context();
		if (oldContextwithRealValue != null) {
			objContextInner = constructContextObj(id + 1, unit);
		}
		ValueObtainer vo = new ValueObtainer(methodSig, "", objContextInner, new Counter());
		String className = vo.getValueofVar(inputVar, unit, 0).getValues().get(0);
		className = className.replace("/", ".").replace("class L", "").replace(";", "");
		className = className.split("@")[0];
		this.singleFrag.getSetDestinationList().add(className);
	}

}
