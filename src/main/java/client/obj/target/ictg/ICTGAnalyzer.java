package main.java.client.obj.target.ictg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import afu.org.checkerframework.checker.units.qual.s;
import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.model.analyzeModel.SingleMethodModel;
import main.java.analyze.model.analyzeModel.SingleObjectModel;
import main.java.analyze.model.analyzeModel.UnitNode;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.SootUtils;
import main.java.analyze.utils.output.PrintUtils;
import main.java.client.obj.ObjectAnalyzer;
import main.java.client.obj.model.atg.AtgEdge;
import main.java.client.obj.model.atg.AtgNode;
import main.java.client.obj.model.component.ActivityModel;
import main.java.client.obj.model.component.ComponentModel;
import main.java.client.obj.model.component.Data;
import main.java.client.obj.model.component.ExtraData;
import main.java.client.obj.model.component.IntentFilterModel;
import main.java.client.obj.model.ictg.SingleIntentModel;
import main.java.client.statistic.model.DoStatistic;
import main.java.client.statistic.model.StatisticResult;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

public class ICTGAnalyzer extends ObjectAnalyzer {

	public ICTGAnalyzer(List<SootMethod> topoQueue, StatisticResult result) {
		super(topoQueue, new ICTGAnalyzerHelper(), result);
	}

	@Override
	public void assignForObjectName() {
		this.objectName = "main.java.client.obj.model.ictg.SingleIntentModel";
	}

	/**
	 * getSingleClassAnalyze
	 */
	@Override
	public void getSingleComponent(SingleMethodModel singleMethod) {
		ComponentModel model = appModel.getComponentMap().get(
				SootUtils.getNameofClass(methodUnderAnalysis.getDeclaringClass().getName()));
		if (model == null)
			return;
		for (SingleObjectModel singleObject : singleMethod.getSingleObjects()) {
			SingleIntentModel singleIntent = (SingleIntentModel) singleObject;
			model.getReceiveModel().getReceivedActionSet().addAll(singleIntent.getGetActionCandidateList());
			model.getReceiveModel().getReceivedCategorySet().addAll(singleIntent.getGetCategoryCandidateList());
			model.getReceiveModel().getReceivedDataSet().addAll(singleIntent.getGetDataCandidateList());
			model.getReceiveModel().getReceivedTypeSet().addAll(singleIntent.getGetTypeCandidateList());
			for (Entry<String, List<ExtraData>> entry : singleIntent.getGetExtrasCandidateList().getBundle().entrySet())
				model.getReceiveModel().getReceivedExtraData().getBundle().put(entry.getKey(), entry.getValue());

			model.getSendModel().getSendActionSet().addAll(singleIntent.getSetActionValueList());
			model.getSendModel().getSendCategorySet().addAll(singleIntent.getSetCategoryValueList());
			model.getSendModel().getSendDataSet().addAll(singleIntent.getSetDataValueList());
			model.getSendModel().getSendTypeSet().addAll(singleIntent.getSetTypeValueList());
			model.getSendModel().getSendFlagSet().addAll(singleIntent.getSetFlagsList());
			for (Entry<String, List<ExtraData>> entry : singleIntent.getSetExtrasValueList().getBundle().entrySet())
				model.getSendModel().getSendExtraData().getBundle().put(entry.getKey(), entry.getValue());

		}
	}

	/**
	 * analyzeCurrentSetMethods for a set of method
	 */
	@Override
	public void drawATGandStatistic(SingleMethodModel model) {
		if (model == null)
			return;
		generateATGInfo(model);
		makeStatistic(model);
	}

	/**
	 * generateATGInfo
	 * 
	 * @param model
	 */
	private void generateATGInfo(SingleMethodModel singleMethod) {
		SootMethod sootMtd = singleMethod.getMethod();
		if (MyConfig.getInstance().getMySwithch().isImplicitLaunchSwitch()) {
			implicitDestinationAnalyze(singleMethod);
		}
		SootClass cls = null;
		if (appModel.getEntryMethod2Component().containsKey(sootMtd))
			cls = appModel.getEntryMethod2Component().get(sootMtd);
		// trick method
		if (cls == null) {
			String currentClsName = SootUtils.getNameofClass(sootMtd.getDeclaringClass());
			if (appModel.getComponentMap().containsKey(currentClsName))
				cls = sootMtd.getDeclaringClass();
			else
				return;
		}
		List<SootClass> subClasses = new ArrayList<SootClass>();
		boolean isEntry = appModel.getEntryMethods().contains(sootMtd);// ||
																		// sootMtd.getName().startsWith(ConstantUtils.ENTRYID
		if (MyConfig.getInstance().getMySwithch().isDummyMainSwitch() && isEntry == false)
			return;
		if (MyConfig.getInstance().getMySwithch().isPolymSwitch()) {
			try {
				subClasses = Scene.v().getActiveHierarchy().getSubclassesOfIncluding(cls);
			} catch (Exception e) {
				subClasses.add(cls);
			}
		} else {
			subClasses.add(cls);
		}
		for (SootClass sootCls : subClasses) {
			if (sootCls.getMethodUnsafe(sootMtd.getSubSignature()) == null || sootCls == sootMtd.getDeclaringClass()) {
				String src = sootCls.getName();
				for (SingleObjectModel singleIntent : singleMethod.getSingleObjects()) {
					if (((SingleIntentModel) singleIntent).getSendIntent2ICCList().size() == 0)
						continue;
					getTargetOfSrc((SingleIntentModel) singleIntent, src);
				}
			}
		}
	}

	/**
	 * getTargetOfSrc
	 * 
	 * @param singleIntent
	 * @param src
	 */
	private void getTargetOfSrc(SingleIntentModel singleIntent, String src) {
		SootMethod method = singleIntent.getMethod();
		Unit unit = singleIntent.getSendIntent2ICCList().iterator().next();
		int instructionId = SootUtils.getIdForUnit(unit, method);
		for (String des : singleIntent.getSetDestinationList()) {
			ComponentModel comp = appModel.getComponentMap().get(des);
			AtgEdge edge;
			if (comp == null || !method.getActiveBody().getUnits().contains(unit))
				edge = new AtgEdge(new AtgNode(src), new AtgNode(des), method.getSignature(), -1, "c");
			else
				edge = new AtgEdge(new AtgNode(src), new AtgNode(des), method.getSignature(), instructionId,
						comp.getComponentType());

			edge.setSingleIntent(singleIntent);
			Global.v().getiCTGModel().getOptModel().addAtgEdges(src, edge);

			String name = SootUtils.getNameofClass(src);
			ComponentModel sourceComponent = Global.v().getAppModel().getComponentMap().get(name);
			if (sourceComponent != null) {
				sourceComponent.getSendModel().getIccTargetSet().add(des);
			}
		}
	}

	/**
	 * implicitDestinationAnalyze
	 * 
	 * @param intentSummary
	 */
	private void implicitDestinationAnalyze(SingleMethodModel intentSummary) {
		for (SingleObjectModel singleObject : intentSummary.getSingleObjects()) {
			SingleIntentModel singleIntent = (SingleIntentModel) singleObject;

			if (singleIntent.getSendIntent2ICCList().size() == 0)
				continue;
			if (singleIntent.getSetDestinationList().size() > 0)
				continue;
			List<String> actionSet = singleIntent.getSetActionValueList();
			List<String> cateSet = singleIntent.getSetCategoryValueList();
			List<String> dataSet = singleIntent.getSetDataValueList();
			List<String> typeSet = singleIntent.getSetTypeValueList();
			if (actionSet.size() + cateSet.size() + dataSet.size() + typeSet.size() > 0) {
				analyzeDesinationByACDT(singleIntent);
				/** add destination match **/
			}
		}
	}

	/**
	 * for implicit ICC destination match ICC-intent filter match rule
	 * 
	 * @param singleIntent
	 * @param acdtSet
	 * @param set
	 */
	private void analyzeDesinationByACDT(SingleIntentModel singleIntent) {
		List<String> summaryActionSet = singleIntent.getSetActionValueList();
		List<String> summaryCateSet = singleIntent.getSetCategoryValueList();
		List<String> summaryDataSet = singleIntent.getSetDataValueList();
		List<String> summaryTypeSet = singleIntent.getSetTypeValueList();
		List<String> resSet = singleIntent.getSetDestinationList();
		boolean findTarget = false;
		for (ComponentModel component : appModel.getComponentMap().values()) {
			for (IntentFilterModel filter : component.getIntentFilters()) {
				Set<String> filterActionSet = filter.getAction_list();
				Set<String> filterCateSet = filter.getCategory_list();
				Set<Data> filterDataSet = filter.getData_list();
				Set<String> filterTypeSet = filter.getDatatype_list();
				if (filterActionSet.size() == 0 && filterCateSet.size() == 0)
					continue;

				boolean actionTarget = false, cateTarget = true, dataTarget = false, typeTarget = true;
				// if a action is find same with one action in filer, matched
				// usually, only one action in summary
				for (String action : summaryActionSet) {
					if (filterActionSet.contains(action))
						actionTarget = true;
				}
				/**
				 * android will add android.intent.category.DEFAULT to all
				 * implicit Activity ICC.
				 * https://developer.android.com/guide/components
				 * /intents-filters.html
				 **/
				if (component instanceof ActivityModel) {
					if (!filterCateSet.contains("android.intent.category.DEFAULT"))
						cateTarget = false;
				}
				// all the category in a summary must find a match one in filter
				for (String category : summaryCateSet) {
					if (!filterCateSet.contains(category))
						cateTarget = false;
				}
				if (filterDataSet.size() == 0)
					dataTarget = true;
				else {
					for (String data : summaryDataSet) {
						if (dataTarget == true)
							break;
						for (Data ifData : filterDataSet) {
							boolean ifMatch = true;
							if (ifData.getHost().length() > 0 && !data.toString().contains(ifData.getHost()))
								ifMatch = false;
							else if (ifData.getPath().length() > 0 && !data.toString().contains(ifData.getPath()))
								ifMatch = false;
							else if (ifData.getPort().length() > 0 && !data.toString().contains(ifData.getPort()))
								ifMatch = false;
							else if (ifData.getScheme().length() > 0 && !data.toString().contains(ifData.getScheme()))
								ifMatch = false;
							else if (ifData.getMime_type().length() > 0
									&& !data.toString().contains(ifData.getMime_type()))
								ifMatch = false;
							if (ifMatch == true) {
								dataTarget = true;
								break;
							}
						}
					}
				}
				for (String type : summaryTypeSet) {
					if (!filterTypeSet.contains(type))
						typeTarget = false;
				}
				boolean flag1 = actionTarget && cateTarget && dataTarget && typeTarget;
				boolean flag2 = (summaryActionSet.size() == 0) && cateTarget && dataTarget && typeTarget;
				if (flag1 || flag2) {
					if (component.getComponentType().contains(singleIntent.getTargetType())) {
						findTarget = true;
						resSet.add(component.getComponetName());
					}
				}
			}
		}
		if (!findTarget) {
			String res = "interICC_" + PrintUtils.printList(summaryActionSet) + ", "
					+ PrintUtils.printList(summaryCateSet);
			res = res.substring(0, res.length() - 2);
			resSet.add(res);
		}
	}

	/**
	 * makeStatistic
	 * 
	 * @param model
	 */
	private void makeStatistic(SingleMethodModel model) {
		// computeTraceDepth();
		DoStatistic.updateTraceStatisticUseSummayMap(false, model, result);
		DoStatistic.updateTraceStatisticUseSummayMap(true, model, result);

		DoStatistic.updateICCStatisticUseSummayMap(false, model, result);
		DoStatistic.updateICCStatisticUseSummayMap(true, model, result);

		DoStatistic.updateSummaryStatisticUseSummayMap(model, result);

		DoStatistic.updateXMLStatisticUseSummayMap(true, model, result);
		DoStatistic.updateXMLStatisticUseSummayMap(false, model, result);

	}

	/**
	 * computeTraceDepth
	 * 
	 */
	public void computeTraceDepth() {
		for (Entry<String, SingleMethodModel> en : currentSummaryMap.entrySet()) {
			SingleMethodModel intentSummary = en.getValue();
			computeTraceDepthForOne(intentSummary, intentSummary, 1);
		}
	}

	/**
	 * used in computeTraceDepth
	 * 
	 * @param currentSummary
	 * @param topSummary
	 * @param i
	 */
	private void computeTraceDepthForOne(SingleMethodModel currentSummary, SingleMethodModel topSummary, int i) {
		List<UnitNode> list = currentSummary.getNodePathList();
		for (UnitNode n : list) {
			if (n.getInterFunNode() != null) {
				if (topSummary.getMaxMethodTraceDepth() < i)
					topSummary.setMaxMethodTraceDepth(i);
				computeTraceDepthForOne(n.getInterFunNode(), topSummary, i + 1);
			}
		}
	}

}
