package main.java.client.statistic.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import main.java.Global;
import main.java.analyze.model.analyzeModel.MethodSummaryModel;
import main.java.analyze.model.analyzeModel.ObjectSummaryModel;
import main.java.analyze.model.analyzeModel.PathSummaryModel;
import main.java.analyze.model.analyzeModel.UnitNode;
import main.java.analyze.utils.SootUtils;
import main.java.analyze.utils.output.PrintUtils;
import main.java.client.obj.model.component.BundleType;
import main.java.client.obj.model.fragment.SingleFragmentModel;
import main.java.client.obj.model.ictg.SingleIntentFeatureExtractor;
import main.java.client.obj.model.ictg.SingleIntentModel;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

public class DoStatistic {

	public static void updateXMLStatisticUseSummayMapForFragment(boolean entryMethod, MethodSummaryModel singleMethod,
			StatisticResult result) {
		XmlStatistic statistic = result.getXmlStatistic();
		if (entryMethod) {
			SootMethod sm = singleMethod.getMethod();
			boolean flag = Global.v().getAppModel().getEntryMethod2Component().containsKey(sm);
			if (!flag)
				return;
		}
		if (singleMethod != null) {
			writeForSingleMethod(singleMethod, entryMethod, statistic);
			writeForSinglePath(singleMethod, entryMethod, statistic);
			writeForSingleFragment(singleMethod, entryMethod, statistic);
		}
	}

	public static void updateXMLStatisticUseSummayMap(boolean entryMethod, MethodSummaryModel singleMethod,
			StatisticResult result) {
		XmlStatistic statistic = result.getXmlStatistic();

		if (entryMethod) {
			SootMethod sm = singleMethod.getMethod();
			SootClass sc = sm.getDeclaringClass();
			String source = SootUtils.getNameofClass(sc.getName());
			boolean flag = Global.v().getAppModel().getComponentMap().containsKey(source);
			if (!flag)
				return;
		}
		if (singleMethod != null) {
			writeForSingleMethod(singleMethod, entryMethod, statistic);
			writeForSinglePath(singleMethod, entryMethod, statistic);
			writeForSingleIntent(singleMethod, entryMethod, statistic);
		}
	}

	/**
	 * writeForSingleIntent
	 * 
	 * @param singleMethod
	 * @param entryMethod
	 * @param statistic
	 */
	private static void writeForSingleIntent(MethodSummaryModel singleMethod, boolean entryMethod, XmlStatistic statistic) {
		Element singleIntentEle = new DefaultElement("Component");
		String sourceStr = SootUtils.getNameofClass(singleMethod.getComponentName());
		singleIntentEle.addAttribute("source", sourceStr);
		Set<SingleIntentModel> history = new HashSet<SingleIntentModel>();
		// for (PathSummaryModel singlePath : singleMethod.getPathSet()) {
		// for(ObjectSummaryModel singleObject: singlePath.getSingleObjectSet()){
		for (ObjectSummaryModel singleObject : singleMethod.getSingleObjects()) {
			SingleIntentModel singleIntent = (SingleIntentModel) singleObject;
			if (history.contains(singleIntent))
				continue;
			if (singleIntent.getSendIntent2ICCList().size() == 0 && singleIntent.getSetDestinationList().size() == 0)
				continue;
			history.add(singleIntent);
			writeSingleIntent(singleIntent, singleIntentEle, singleObject.getSinglePath(), singleMethod);
			// }
		}
		if (singleIntentEle.hasContent()) {
			if (entryMethod)
				statistic.addEntrySingleIntentEleList(singleIntentEle);
			else
				statistic.addAllSingleIntentEleList(singleIntentEle);
		}
	}

	/**
	 * writeForSingleIntent
	 * 
	 * @param singleMethod
	 * @param entryMethod
	 * @param statistic
	 */
	private static void writeForSingleFragment(MethodSummaryModel singleMethod, boolean entryMethod,
			XmlStatistic statistic) {
		Element singleIntentEle = new DefaultElement("Component");
		String sourceStr = singleMethod.getMethod().getDeclaringClass().getName();
		singleIntentEle.addAttribute("source", sourceStr);
		Set<ObjectSummaryModel> history = new HashSet<ObjectSummaryModel>();
		for (ObjectSummaryModel singleObject : singleMethod.getSingleObjects()) {
			SingleFragmentModel Singlefrag = (SingleFragmentModel) singleObject;
			if (history.contains(Singlefrag))
				continue;
			if (Singlefrag.getSendFragment2Start().size() == 0)
				continue;
			history.add(Singlefrag);
			writeSingleFragment(Singlefrag, singleIntentEle, singleObject.getSinglePath(), singleMethod);
		}
		if (singleIntentEle.hasContent()) {
			if (entryMethod)
				statistic.addEntrySingleIntentEleList(singleIntentEle);
			else
				statistic.addAllSingleIntentEleList(singleIntentEle);
		}
	}

	/**
	 * writeForSinglePath
	 * 
	 * @param singleMethod
	 * @param entryMethod
	 * @param statistic
	 */
	private static void writeForSinglePath(MethodSummaryModel singleMethod, boolean entryMethod, XmlStatistic statistic) {
		if (singleMethod.getPathSet().size() == 0)
			return;

		Element singlePathEle = new DefaultElement("singleMethod");
		singlePathEle.addAttribute("source", singleMethod.getMethod().getSignature());

		for (PathSummaryModel singlePath : singleMethod.getPathSet()) {
			writeSinglePath(singlePath, singlePathEle);
		}

		if (singlePathEle.element("singlePath") != null) {
			if (entryMethod)
				statistic.addEntrySinglePathEleList(singlePathEle);
			else
				statistic.addAllSinglePathEleList(singlePathEle);
		}
	}

	/**
	 * writeForSingleMethod
	 * 
	 * @param singleMethod
	 * @param entryMethod
	 * @param statistic
	 */
	private static void writeForSingleMethod(MethodSummaryModel singleMethod, boolean entryMethod, XmlStatistic statistic) {
		Element singleMethodEle = new DefaultElement("singleMethod");
		singleMethodEle.addAttribute("source", singleMethod.getMethod().getSignature());

		writeSingleMethod(singleMethodEle, singleMethod);

		if (singleMethodEle.element("node") != null) {
			if (entryMethod) {
				statistic.addEntrySingleMethodEleList(singleMethodEle);
			} else {
				statistic.addAllSingleMethodEleList(singleMethodEle);
			}
		}

	}

	/**
	 * write Single Node Xml in IntentSummaryModel
	 * 
	 * @param stack
	 * @param path
	 * @param singleMethod
	 * @param topSummary
	 * @param i
	 */
	private static void writeSingleMethod(Element path, MethodSummaryModel singleMethod) {
		List<UnitNode> list = singleMethod.getNodePathList();
		for (UnitNode n : list) {
			if (n.getUnit() == null)
				continue;
			Element node = path.addElement("node");
			node.addAttribute("unit", n.getUnit().toString());
			if (n.getType().length() > 0)
				node.addAttribute("type", n.getType());
			if (n.getCondition() != null) {
				Element condInfo = node.addElement("condInfo");
				condInfo.addAttribute("value", n.getCondition().toString());
			}
		}
	}

	/**
	 * writeSinglePath
	 * 
	 * @param singlePath
	 * @param summary
	 */
	private static void writeSinglePath(PathSummaryModel singlePath, Element summary) {
		if (singlePath.getNodes().size() == 0)
			return;
		Element icc = summary.addElement("singlePath");
		writeMethod(icc, singlePath);
		writeSinglePathICCNode(new ArrayList<String>(), singlePath, icc);
	}

	/**
	 * writeSinglePath
	 * 
	 * @param singleIntent
	 * @param summary
	 * @param singlePath
	 */
	private static void writeSingleIntent(SingleIntentModel singleIntent, Element summary, PathSummaryModel singlePath,
			MethodSummaryModel singleMethod) {
		Element icc = new DefaultElement("singleIntent");
		// writeICCType(singleIntent, icc);
		writeMethod(icc, singlePath, singleMethod);
		writeSource(singleIntent, icc, singleMethod);
		writeDestnition(singleIntent, icc);
		writeICCSendReceive(singleIntent, icc);
		// writeICCFlow(singleIntent, icc);
		writeSingleObjectICCNode(new ArrayList<String>(), singleIntent, icc);
		if (icc.element("nodes") != null || icc.element("destinition") != null)
			summary.add(icc);
	}

	private static void writeMethod(Element icc, PathSummaryModel singlePath, MethodSummaryModel singleMethod) {
		Element desElement = icc.addElement("method");
		desElement.addAttribute("method", singleMethod.getMethod().getSubSignature());
		if (singlePath != null)
			desElement.addAttribute("methodtrace", PrintUtils.printList(singlePath.getMethodTrace(), "\n"));

	}

	private static void writeMethod(Element icc, PathSummaryModel singlePath) {
		Element desElement = icc.addElement("method");
		// desElement.addAttribute("method",
		// singleMethod.getMethod().getSignature());
		desElement.addAttribute("methodtrace", PrintUtils.printList(singlePath.getMethodTrace(), "\n"));

	}

	private static void writeSingleFragment(SingleFragmentModel singlefrag, Element singleIntentEle,
			PathSummaryModel singlePath, MethodSummaryModel singleMethod) {
		if (singlefrag.getSendFragment2Start().size() == 0)
			return;
		Element frag = new DefaultElement("SingleFragment");
		writeMethod(frag, singlePath, singleMethod);
		writeSource(singlefrag, frag, singleMethod);
		writeDestnition(singlefrag, frag);
		// writeFragmentFlow(singlefrag, frag);
		writeSingleObjectICCNode(new ArrayList<String>(), singlefrag, frag);
		if (frag.element("nodes") != null)
			singleIntentEle.add(frag);

	}

	private static void writeFragmentFlow(main.java.client.obj.model.fragment.SingleFragmentModel singlefrag,
			Element frag) {
		Element flow = new DefaultElement("flow");
		List<Unit> getCreateList = singlefrag.getCreateList();
		for (Unit u : getCreateList) {
			Element createList = flow.addElement("createList");
			createList.addAttribute("value", u.toString());
		}
		List<Unit> getReceiveFromParaList = singlefrag.getReceiveFromParaList();
		for (Unit u : getReceiveFromParaList) {
			Element receiveFromParaList = flow.addElement("receiveFromParaList");
			receiveFromParaList.addAttribute("value", u.toString());
		}
		List<Unit> getFragmentFromOutList = singlefrag.getGetFragmentFromOut();
		for (Unit u : getFragmentFromOutList) {
			Element fragmentFromOutList = flow.addElement("fragmentFromOutList");
			fragmentFromOutList.addAttribute("value", u.toString());
		}
		List<Unit> getDataHandleList = singlefrag.getDataHandleList();
		for (Unit u : getDataHandleList) {
			Element dataHandleList = flow.addElement("dataHandleList");
			dataHandleList.addAttribute("value", u.toString());
		}
		List<Unit> getSendIntent2FunList = singlefrag.getSendIntent2FunList();
		for (Unit u : getSendIntent2FunList) {
			Element sendIntent2FunList = flow.addElement("sendIntent2FunList");
			sendIntent2FunList.addAttribute("value", u.toString());
		}
		List<Unit> getSendFragment2Start = singlefrag.getSendFragment2Start();
		for (Unit u : getSendFragment2Start) {
			Element sendFragment2Start = flow.addElement("sendFragment2Start");
			sendFragment2Start.addAttribute("value", u.toString());
		}

		if (flow.hasContent())
			frag.add(flow);

	}

	private static void writeICCFlow(SingleIntentModel singleIntent, Element icc) {
		Element flow = new DefaultElement("flow");
		List<Unit> getCreateList = singleIntent.getCreateList();
		for (Unit u : getCreateList) {
			Element createList = flow.addElement("createList");
			createList.addAttribute("value", u.toString());
		}
		List<Unit> getReceiveFromParaList = singleIntent.getReceiveFromParaList();
		for (Unit u : getReceiveFromParaList) {
			Element receiveFromParaList = flow.addElement("receiveFromParaList");
			receiveFromParaList.addAttribute("value", u.toString());
		}
		List<Unit> getReceiveFromOutList = singleIntent.getReceiveFromOutList();
		for (Unit u : getReceiveFromOutList) {
			Element receiveFromOutList = flow.addElement("receiveFromOutList");
			receiveFromOutList.addAttribute("value", u.toString());
		}
		List<Unit> getDataHandleList = singleIntent.getDataHandleList();
		for (Unit u : getDataHandleList) {
			Element dataHandleList = flow.addElement("dataHandleList");
			dataHandleList.addAttribute("value", u.toString());
		}
		List<Unit> getSendIntent2ICCList = singleIntent.getSendIntent2ICCList();
		for (Unit u : getSendIntent2ICCList) {
			Element sendIntent2ICCList = flow.addElement("sendIntent2ICCList");
			sendIntent2ICCList.addAttribute("value", u.toString());
		}
		List<Unit> getSendIntent2FunList = singleIntent.getSendIntent2FunList();
		for (Unit u : getSendIntent2FunList) {
			Element sendIntent2FunList = flow.addElement("sendIntent2FunList");
			sendIntent2FunList.addAttribute("value", u.toString());
		}
		if (flow.hasContent())
			icc.add(flow);
	}

	private static void writeSinglePathICCNode(List<String> context, PathSummaryModel singlePath, Element icc) {
		List<UnitNode> nodeList = singlePath.getNodes();
		// how to print node with its context??/
		int nodeId = 0;
		for (UnitNode node : nodeList) {
			Element ele = icc.addElement("node");
			if (node.getType().length() > 0)
				ele.addAttribute("type", node.getType());
			ele.addAttribute("method", node.getMethod().getDeclaringClass().getShortName() + " "
					+ node.getMethod().getName());
			ele.addAttribute("unit", node.getUnit().toString());

			String predsStr = "";
			if (node.getPreds().size() > 0) {
				for (UnitNode predNode : node.getPreds()) {
					predsStr += predNode.hashCode() + ", ";
				}
				predsStr = predsStr.substring(0, predsStr.length() - 2);
			}
			String succsStr = "";
			if (node.getSuccs().size() > 0) {
				for (UnitNode succNode : node.getSuccs()) {
					succsStr += succNode.hashCode() + ", ";
				}
				succsStr = succsStr.substring(0, succsStr.length() - 2);
			}
			Element basic = ele.addElement("nodeInfo");
			basic.addAttribute("nodeId", node.hashCode() + "");
			basic.addAttribute("predsStr", predsStr);
			basic.addAttribute("succsStr", succsStr);

			context = singlePath.getNode2TraceMap().get(nodeId++);
			basic.addAttribute("context", PrintUtils.printList(context));
			if (node.getBaseNodePointToMap().containsKey(context) && node.getBaseNodePointedTo(context) != null) {
				Element unitPointTo = ele.addElement("baseNodePointTo");
				unitPointTo.addAttribute("value", node.getBaseNodePointedTo(context).hashCode() + "");
				unitPointTo.addAttribute("context", PrintUtils.printList(context) + "");
			}
			if (node.getNodeSetPointToMeMap().containsKey(context) && node.getNodeSetPointToMe(context) != null) {
				Element unitPointTo = ele.addElement("nodeSetPointToMe");
				String res = "";
				for (UnitNode tempNode : node.getNodeSetPointToMe(context)) {
					res += tempNode.hashCode() + ",";
				}
				unitPointTo.addAttribute("value", res);
				unitPointTo.addAttribute("context", PrintUtils.printList(context) + "");
			}
		}

	}

	private static void writeSingleObjectICCNode(List<String> context, ObjectSummaryModel singleObject, Element icc) {
		// if(singleObject.getCreateList().size()==0 &&
		// singleObject.getReceiveFromFromRetValueList().size()==0 &&
		// singleObject.getReceiveFromParaList().size()==0) return;
		Element nodes = new DefaultElement("nodes");
		List<UnitNode> nodeList = singleObject.getNodes();
		for (UnitNode node : nodeList) {
			Element ele = nodes.addElement("node");
			ele.addAttribute("method", node.getMethod().getDeclaringClass().getShortName() + " "
					+ node.getMethod().getName());
			if (node.getType().length() > 0)
				ele.addAttribute("type", node.getType());
			ele.addAttribute("unit", node.getUnit().toString());

			String predsStr = "";
			if (node.getPreds().size() > 0) {
				for (UnitNode predNode : node.getPreds()) {
					predsStr += predNode.hashCode() + ", ";
				}
				predsStr = predsStr.substring(0, predsStr.length() - 2);
			}
			String succsStr = "";
			if (node.getSuccs().size() > 0) {
				for (UnitNode succNode : node.getSuccs()) {
					succsStr += succNode.hashCode() + ", ";
				}
				succsStr = succsStr.substring(0, succsStr.length() - 2);
			}
		}
		if (nodes.hasContent())
			icc.add(nodes);

	}

	private static void writeICCSendReceive(SingleIntentModel singleIntent, Element icc) {
		List<String> actions = singleIntent.getSetActionValueList();
		List<String> category = singleIntent.getSetCategoryValueList();
		List<String> data = singleIntent.getSetDataValueList();
		List<String> type = singleIntent.getSetTypeValueList();
		BundleType extras = singleIntent.getSetExtrasValueList();
		List<String> flags = singleIntent.getSetFlagsList();
		boolean finish = singleIntent.isFinishFlag();
		Element sender = new DefaultElement("sender");
		if (actions.size() > 0)
			sender.addAttribute("action", PrintUtils.printList(actions));
		if (category.size() > 0)
			sender.addAttribute("category", PrintUtils.printList(category));
		if (data.size() > 0)
			sender.addAttribute("data", PrintUtils.printList(data));
		if (type.size() > 0)
			sender.addAttribute("type", PrintUtils.printList(type));
		if (extras.toString().length() > 0)
			sender.addAttribute("extras", extras.toString());
		if (flags.size() > 0)
			sender.addAttribute("flags", PrintUtils.printList(flags));
		if (finish)
			sender.addAttribute("componentFinish", "true");
		if (sender.attributeCount() > 0)
			icc.add(sender);

		// do not output receiver
		// List<String> actions2 = singleIntent.getGetActionCandidateList();
		// List<String> category2 = singleIntent.getGetCategoryCandidateList();
		// List<String> data2 = singleIntent.getGetDataCandidateList();
		// List<String> type2 = singleIntent.getGetTypeCandidateList();
		// BundleType extras2 = singleIntent.getGetExtrasCandidateList();
		// Element receiver = new DefaultElement("receiver");
		// if (actions2.size() > 0)
		// receiver.addAttribute("action", PrintUtils.printList(actions2));
		// if (category2.size() > 0)
		// receiver.addAttribute("category", PrintUtils.printList(category2));
		// if (data2.size() > 0)
		// receiver.addAttribute("data", PrintUtils.printList(data2));
		// if (type2.size() > 0)
		// receiver.addAttribute("type", PrintUtils.printList(type2));
		// if (extras2.toString().length() > 0)
		// receiver.addAttribute("extras", extras2.toString());
		// if(receiver.attributeCount()>0)
		// icc.add(receiver);
	}

	private static void writeSource(ObjectSummaryModel singleObj, Element icc, MethodSummaryModel singleMethod) {
		Element desElement = icc.addElement("source");
		desElement.addAttribute("name", SootUtils.getNameofClass(singleMethod.getComponentName()));

	}

	private static void writeDestnition(ObjectSummaryModel singleObj, Element icc) {
		List<String> des = null;
		if (singleObj instanceof SingleIntentModel)
			des = ((SingleIntentModel) singleObj).getSetDestinationList();
		if (singleObj instanceof SingleFragmentModel)
			des = ((SingleFragmentModel) singleObj).getSetDestinationList();
		if (des.size() > 0) {
			Element desElement = icc.addElement("destinition");
			desElement.addAttribute("name", PrintUtils.printList(des));
		}
	}

	private static void writeICCType(SingleIntentModel singleIntent, Element icc) {
		Element typeElement = new DefaultElement("summaryType");
		String summaryType = SingleIntentFeatureExtractor.getSummaryStr(singleIntent);
		if (summaryType.length() > 0)
			typeElement.addAttribute("summaryType", summaryType);
		String summaryReceiveType = SingleIntentFeatureExtractor.getReceiveStr(singleIntent);
		if (summaryReceiveType.length() > 0)
			typeElement.addAttribute("receiveType", summaryReceiveType);
		String summaryNewType = SingleIntentFeatureExtractor.getNewStr(singleIntent);
		if (summaryNewType.length() > 0)
			typeElement.addAttribute("newType", summaryNewType);
		String summaryUsedType = SingleIntentFeatureExtractor.getUseAttributeStr(singleIntent);
		if (summaryUsedType.length() > 0)
			typeElement.addAttribute("usedType", summaryUsedType);
		String summarySetType = SingleIntentFeatureExtractor.getSetAttributeStr(singleIntent);
		if (summarySetType.length() > 0)
			typeElement.addAttribute("setType", summarySetType);
		String summarySendType = SingleIntentFeatureExtractor.getSendStr(singleIntent);
		if (summarySendType.length() > 0)
			typeElement.addAttribute("sendType", summarySendType);
		if (typeElement.attributeCount() > 0)
			icc.add(typeElement);

	}

	/**
	 * countICCNumber
	 * 
	 * @param AppModel
	 *            .getInstance()
	 * @param entryMethod
	 *            only count entry method
	 */
	public static int countICCRelatedMethodNumber(boolean entryMethod, Map<String, MethodSummaryModel> summaryMap) {
		int sum = 0;
		for (Entry<String, MethodSummaryModel> en : summaryMap.entrySet()) {
			if (entryMethod) {
				SootMethod sm = SootUtils.getSootMethodBySignature(en.getKey());
				boolean flag = Global.v().getAppModel().getEntryMethod2Component().containsKey(sm);
				if (flag == false)
					continue;
			}
			sum++;
		}
		return sum;
	}

	/**
	 * updateSummaryStatisticUseSummayMap
	 * 
	 * @param summaryMap
	 * @param result
	 * @return
	 */
	public static void updateSummaryStatisticUseSummayMap(MethodSummaryModel model, StatisticResult result) {
		SummaryStatistic statistic = result.getSummaryStatistic();
		statistic.getSummariedMethods().add(model.getMethod().getSignature());
		if (isICCRelatedEntryMethod(model))
			statistic.getSummariedEntryMethods().add(model.getMethod().getSignature());
		for (String me : statistic.getSummariedEntryMethods()) {
			if (SootUtils.isLifeCycleMethods(me)) {
				statistic.addSummariedEntryLifeCycleMethods(me);
			} else {
				statistic.addSummariedEntryListenerMethods(me);
			}
		}
	}

	/**
	 * updateTraceStatisticUseSummayMap
	 * 
	 * @param entryMethod
	 * @param summaryMap
	 * @param result
	 */
	public static void updateTraceStatisticUseSummayMap(boolean entryMethod, MethodSummaryModel intentSummary,
			StatisticResult result) {
		TraceStatistic statistic;
		if (entryMethod) {
			statistic = result.getEntryTraceStatistic();
		} else {
			statistic = result.getAllTraceStatistic();
		}

		int traceNum = 0, traceNumSum = 0;
		int traceDepth = 0;
		if (entryMethod) {
			SootMethod sm = intentSummary.getMethod();
			boolean flag = Global.v().getAppModel().getEntryMethod2Component().containsKey(sm);
			if (flag == false)
				return;
		}
		traceDepth += intentSummary.getMaxMethodTraceDepth();
		for (PathSummaryModel si : intentSummary.getPathSet()) {
			traceNum += si.getMethodTrace().size() - 1;
			traceNumSum++;
		}

		// traceDepth = (en.getValue().size() == 0 ? 0 : traceDepth /
		// en.getValue().size());
		// if
		// (!statistic.getMethodTraceDepth2MethodSet().containsKey(traceDepth))
		// statistic.getMethodTraceDepth2MethodSet().put(Integer.valueOf(traceDepth),
		// new HashSet<String>());
		// statistic.getMethodTraceDepth2MethodSet().get(Integer.valueOf(traceDepth)).add(en.getKey());
		//
		// traceNum = (traceNumSum == 0 ? 0 : traceNum / traceNumSum);
		// if (!statistic.getMethodTraceNum2MethodSet().containsKey(traceNum))
		// statistic.getMethodTraceNum2MethodSet().put(Integer.valueOf(traceNum),
		// new HashSet<String>());
		// statistic.getMethodTraceNum2MethodSet().get(Integer.valueOf(traceNum)).add(en.getKey());
	}

	/**
	 * updateICCStatisticUseSummayMap add entry and non-entry node information
	 * to statistic xml file
	 * 
	 * @param entryMethod
	 * @param summaryMap
	 * @param result
	 */
	public static void updateICCStatisticUseSummayMap(boolean entryMethod, MethodSummaryModel intentSummary,
			StatisticResult result) {
		ICCStatistic statistic;
		if (entryMethod) {
			statistic = result.getEntryICCStatistic();
		} else {
			statistic = result.getAllICCStatistic();
		}

		// int entryNumber = countICCRelatedMethodNumber(entryMethod,
		// summaryMap);
		// statistic.addICCFlowNum(entryNumber);

		SootMethod sm = intentSummary.getMethod();
		if (entryMethod) {
			boolean flag = Global.v().getAppModel().getEntryMethod2Component().containsKey(sm);
			if (flag == false)
				return;
		}
		if (!statistic.getDestinationMap().containsKey(sm.getSignature())) {
			statistic.getDestinationMap().put(sm.getSignature(), new HashSet<String>());
		}
		for (ObjectSummaryModel singleObject : intentSummary.getSingleObjects()) {
			SingleIntentModel singleIntent = (SingleIntentModel) singleObject;
			Set<String> newDestinationSet = new HashSet<String>();
			for (String des : singleIntent.getSetDestinationList()) {
				newDestinationSet.add(des);
			}
			statistic.getDestinationMap().get(sm.getSignature()).addAll(newDestinationSet);

			String summaryType = SingleIntentFeatureExtractor.getSummaryStr(singleIntent);
			addSummaryType2Map(singleIntent, summaryType, statistic.getIntentSummaryTypeMap());

			String summaryReceiveType = SingleIntentFeatureExtractor.getReceiveStr(singleIntent);
			addSummaryType2Map(singleIntent, summaryReceiveType, statistic.getIntentSummaryReceiveTypeMap());

			String summaryNewType = SingleIntentFeatureExtractor.getNewStr(singleIntent);
			addSummaryType2Map(singleIntent, summaryNewType, statistic.getIntentSummaryNewTypeMap());

			String summaryUsedType = SingleIntentFeatureExtractor.getUseAttributeStr(singleIntent);
			addSummaryType2Map(singleIntent, summaryUsedType, statistic.getIntentSummaryUsedTypeMap());

			String summarySetType = SingleIntentFeatureExtractor.getSetAttributeStr(singleIntent);
			addSummaryType2Map(singleIntent, summarySetType, statistic.getIntentSummarySetTypeMap());

			String summarySendType = SingleIntentFeatureExtractor.getSendStr(singleIntent);
			addSummaryType2Map(singleIntent, summarySendType, statistic.getIntentSummarySendTypeMap());
		}

		for (Entry<String, Set<String>> en : statistic.getDestinationMap().entrySet()) {
			for (String des : en.getValue()) {
				if (des.startsWith(Global.v().getAppModel().getPackageName()))
					statistic.addIntraDestinationNum();
				else
					statistic.addInterDestinationNum();
			}
			int len = en.getValue().size();
			if (!statistic.getDestinationNum2MethodSet().containsKey(len))
				statistic.getDestinationNum2MethodSet().put(Integer.valueOf(len), new HashSet<String>());
			statistic.getDestinationNum2MethodSet().get(Integer.valueOf(len)).add(en.getKey());
		}

	}

	/**
	 * addSummaryType2Map
	 * 
	 * @param singleIntent
	 * @param type
	 * @param map
	 */
	private static void addSummaryType2Map(SingleIntentModel singleIntent, String type,
			Map<String, Set<SingleIntentModel>> map) {
		if (type.length() > 0) {
			if (!map.containsKey(type)) {
				map.put(type, new HashSet<SingleIntentModel>());
			}
			map.get(type).add(singleIntent);
		}
	}

	private static boolean isICCRelatedEntryMethod(MethodSummaryModel intentSummary) {
		if (intentSummary.getPathSet().size() == 0)
			return false;
		SootMethod sm = null;
		try {
			sm = SootUtils.getSootMethodBySignature(intentSummary.getMethod().getSignature());
		} catch (Exception e) {
		}
		if (sm == null)
			return false;
		return Global.v().getAppModel().getEntryMethod2Component().containsKey(sm);
	}

}
