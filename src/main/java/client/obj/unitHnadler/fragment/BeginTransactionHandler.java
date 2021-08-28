package main.java.client.obj.unitHnadler.fragment;

import main.java.analyze.model.analyzeModel.SingleObjectModel;
import main.java.analyze.model.sootAnalysisModel.Context;
import main.java.client.obj.model.fragment.SingleFragmentModel;
import main.java.client.obj.unitHnadler.UnitHandler;

public class BeginTransactionHandler extends UnitHandler {
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
		this.singleFrag.getDataHandleList().add(unit);
	}

}
