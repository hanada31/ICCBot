package main.java.client.obj.unitHnadler.fragment;

import java.io.IOException;
import java.util.List;

import main.java.Global;
import main.java.analyze.model.analyzeModel.SingleObjectModel;
import main.java.analyze.model.sootAnalysisModel.Context;
import main.java.analyze.model.sootAnalysisModel.Counter;
import main.java.analyze.utils.ValueObtainer;
import main.java.client.obj.model.fragment.SingleFragmentModel;
import main.java.client.obj.unitHnadler.UnitHandler;
import soot.SootClass;
import soot.Unit;
import soot.Value;
import soot.jimple.infoflow.android.resources.ARSCFileParser;
import soot.jimple.infoflow.android.resources.ARSCFileParser.AbstractResource;

public class SetContentFunctionHandler extends UnitHandler {
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
			setContentAnalyze();
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
			setContentAnalyze();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setContentAnalyze() throws IOException {
		int id = 0;
		Value inputVar = getInputVar(id, unit);
		// if(inputVar == null) return;
		Context objContextInner = new Context();
		if (oldContextwithRealValue != null) {
			objContextInner = constructContextObj(id + 1, unit);
		}
		ValueObtainer vo = new ValueObtainer(methodSig, "", objContextInner, new Counter());
		List<String> ids = vo.getValueofVar(inputVar, unit, 0).getValues();
		if (ids != null && ids.size() > 0) {
			String resId = ids.get(0);
			ARSCFileParser resParser = new ARSCFileParser();
			resParser.parse(appModel.getAppPath());
			int resourceId;
			try {
				resourceId = Integer.parseInt(resId);
			} catch (NumberFormatException e) {
				return;
			}

			// Global.v().getFragmentModel().getXmlFragmentMap() is null !!!!!
			AbstractResource resource = resParser.findResource(resourceId);
			if (resource != null && Global.v().getFragmentModel().getXmlFragmentMap().containsKey(resource.toString())) {
				List<SootClass> scs = Global.v().getFragmentModel().getXmlFragmentMap().get(resource.toString());
				for (SootClass sc : scs)
					this.singleFrag.getSetDestinationList().add(sc.getName());
			}
		}

	}

}
