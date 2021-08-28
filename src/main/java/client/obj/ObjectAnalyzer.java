package main.java.client.obj;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import java_cup.internal_error;


import main.java.Analyzer;
import main.java.Global;
import main.java.MyConfig;
import main.java.SummaryLevel;
import main.java.analyze.model.analyzeModel.Attribute;
import main.java.analyze.model.analyzeModel.Condition;
import main.java.analyze.model.analyzeModel.ConditionLeaf;
import main.java.analyze.model.analyzeModel.SingleMethodModel;
import main.java.analyze.model.analyzeModel.SingleObjectModel;
import main.java.analyze.model.analyzeModel.SinglePathModel;
import main.java.analyze.model.analyzeModel.StaticFiledInfo;
import main.java.analyze.model.analyzeModel.UnitNode;
import main.java.analyze.model.sootAnalysisModel.Context;
import main.java.analyze.unitHnadler.exprHandler.ExprHandler;
import main.java.analyze.unitHnadler.stmtHandler.StmtHandler;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.GraphUtils;
import main.java.analyze.utils.SootUtils;
import main.java.analyze.utils.ValueObtainer;
import main.java.client.obj.model.ictg.SingleIntentModel;
import main.java.client.obj.target.fragment.FragmentAnalyzerHelper;
import main.java.client.obj.target.ictg.ICTGAnalyzer;
import main.java.client.obj.unitHnadler.UnitHandler;
import main.java.client.statistic.model.StatisticResult;
import soot.Body;
import soot.Local;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.ValueBox;
import soot.jimple.InvokeExpr;
import soot.jimple.RetStmt;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JLookupSwitchStmt;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.ExceptionalBlockGraph;
import soot.toolkits.scalar.UnitValueBoxPair;

public abstract class ObjectAnalyzer extends Analyzer {
	public abstract void assignForObjectName();

	public abstract void drawATGandStatistic(SingleMethodModel model);

	private Set<SootMethod> analyzedMethodSet;
	protected SootMethod methodUnderAnalysis;
	protected List<SootMethod> topoQueue;
	protected AnalyzerHelper helper;
	protected Map<String, SingleMethodModel> currentSummaryMap;
	protected List<String> objectIdentier;
	protected String objectName = "";
	protected StatisticResult result;

	public ObjectAnalyzer(List<SootMethod> topoQueue, AnalyzerHelper helper, StatisticResult result) {
		super();
		this.analyzedMethodSet = new HashSet<SootMethod>();
		this.topoQueue = topoQueue;
		this.helper = helper;
		this.currentSummaryMap = new HashMap<String, SingleMethodModel>();
		this.result = result;
		this.objectIdentier = helper.getObjectIdentier();
		assignForObjectName();
	}

	@Override
	public void analyze() {
		/** according to the topology order **/
		Global.v().id = 0;
		for (SootMethod m : topoQueue) {
			if (this instanceof ICTGAnalyzer) {
				Global.v().id++;
				if (Global.v().id % 200 == 0)
					System.out.println("This is the method #" + Global.v().id + "/"
							+ Global.v().getAppModel().getTopoMethodQueue().size());
			}
			SingleMethodModel model = analyzeSingleMethod(m);
			drawATGandStatistic(model);
			if (MyConfig.getInstance().isStopFlag())
				return;
		}
	}

	/**
	 * analyzeSingleMethod to get its summary
	 * 
	 * @param mc
	 */
	protected SingleMethodModel analyzeSingleMethod(SootMethod methodUnderAnalysis) {
		appModel.addMethod(methodUnderAnalysis);
		if (initMethodCheck(methodUnderAnalysis) == false)
			return null;
		analyzedMethodSet.add(methodUnderAnalysis);
		this.methodUnderAnalysis = methodUnderAnalysis;

		String className = methodUnderAnalysis.getDeclaringClass().getName();
		SingleMethodModel singleMethod = new SingleMethodModel(className, methodUnderAnalysis);
		String tag = methodUnderAnalysis.getSignature();
		if (tag.contains("AllergiesActivity: void showSearchView"))
			System.err.println(methodUnderAnalysis.getSignature());
		// System.out.println(methodUnderAnalysis.getSignature());

		if (methodUnderAnalysis.getSignature().contains(ConstantUtils.DUMMYMAIN)) {
			analyzeDummyMain(singleMethod);
			return singleMethod;
		}
		/** get target units -- ICC related units **/
		Map<Unit, List<Unit>> targetMap = getTargetUnitsOfMethod();
		if (targetMap.size() == 0)
			return singleMethod;

		// System.out.println("getSinglePath");
		/** analyze singlePaths **/
		UnitNode treeRoot = getNodeTreeByCFGAnalysis(targetMap);

		Set<List<UnitNode>> nodeListSet = getUnitNodePaths(treeRoot);
		getSinglePath(singleMethod, nodeListSet);
		// System.out.println("getSingleObject");
		/** analyze SingleObject **/
		getSingleObject(singleMethod);

		// System.out.println("getSingleComponent");
		/** analyze SingleClass **/
		getSingleComponent(singleMethod);

		return singleMethod;
	}

	/**
	 * do not expand dummy method
	 * 
	 * @param singleMethod
	 */
	private void analyzeDummyMain(SingleMethodModel singleMethod) {
		Iterator<Unit> it = SootUtils.getSootActiveBody(methodUnderAnalysis).getUnits().iterator();
		while (it.hasNext()) {
			Unit u = it.next();
			InvokeExpr exp = SootUtils.getInvokeExp(u);
			if (exp == null)
				continue;
			SootMethod invokedMethod = exp.getMethod();
			if (hasAnalyzeResutltOfCurrentMehtod(invokedMethod)) {
				Set<SinglePathModel> paths = currentSummaryMap.get(invokedMethod.getSignature()).getPathSet();
				if (paths.size() > 0) {
					singleMethod.getPathSet().addAll(paths);
				}
			}
		}

		if (singleMethod.getPathSet().size() > 0) {
			if (!MyConfig.getInstance().getMySwithch().getSummaryStrategy().equals(SummaryLevel.none))
				currentSummaryMap.put(methodUnderAnalysis.getSignature(), singleMethod);
		}
	}

	/**
	 * getNodeTreeByCFGAnalysis
	 * 
	 * @param targetMap
	 * @return
	 */
	private UnitNode getNodeTreeByCFGAnalysis(Map<Unit, List<Unit>> targetMap) {
		/** build and simplify graph **/
		Set<Block> targetBlocks = new HashSet<Block>();
		Body b = SootUtils.getSootActiveBody(methodUnderAnalysis);
		BlockGraph briefGraph = new ExceptionalBlockGraph(b);
		Map<Block, Condition> conditionMap = GraphUtils.getConditionOfCFG(briefGraph);
		BlockGraph vfg = createVFGofGraph(targetMap, briefGraph, targetBlocks, conditionMap);
		removeCycle(vfg);
		/** build node tree with vfg and target units **/
		BlockGraph graphForMethod;
		if (MyConfig.getInstance().getMySwithch().isVfgStrategy()) {
			graphForMethod = vfg;
		} else {
			graphForMethod = briefGraph;
		}
		UnitNode treeRoot = generateNodeTree(targetMap, graphForMethod, targetBlocks, conditionMap);
		// treeRoot.printTree(new HashSet<UnitNode>());
		return treeRoot;
	}

	/**
	 * creatSingleObject by reflect
	 * 
	 * @param singlePath
	 * @return
	 */
	private SingleObjectModel creatSingleObject(SinglePathModel singlePath) {
		SingleObjectModel singleObj = null;
		try {
			Class<?> clazz = Class.forName(objectName);
			@SuppressWarnings("unchecked")
			Constructor<SingleObjectModel> constructor = (Constructor<SingleObjectModel>) clazz
					.getConstructor(SinglePathModel.class);
			singleObj = constructor.newInstance(singlePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return singleObj;
	}

	/**
	 * getSingleObjects
	 * 
	 * @param singleMethod
	 * @param targetMap
	 */
	protected void getSingleObject(SingleMethodModel singleMethod) {
		if (MyConfig.getInstance().getMySwithch().getSummaryStrategy().equals(SummaryLevel.object)) {
			getSingleObject_objectLevel(singleMethod);
		} else if (MyConfig.getInstance().getMySwithch().getSummaryStrategy().equals(SummaryLevel.path)) {
			getSingleObject_pathLevel(singleMethod);
		} else if (MyConfig.getInstance().getMySwithch().getSummaryStrategy().equals(SummaryLevel.flow)) {
			getSingleObject_flowLevel(singleMethod);
		}
	}

	/**
	 * getSingleObject_pathLevel
	 * 
	 * @param singleMethod
	 */
	private void getSingleObject_pathLevel(SingleMethodModel singleMethod) {
		for (SinglePathModel singlePath : singleMethod.getPathSet()) {
			Set<SingleObjectModel> addedSet = new HashSet<SingleObjectModel>();
			SingleObjectModel singleObj = creatSingleObject(singlePath);
			if (singleObj == null)
				continue;
			addedSet.add(singleObj);

			List<UnitNode> workList = singlePath.getNodes();
			handleWorkList(singlePath, singleObj, workList, addedSet);
			for (UnitNode node : workList) {
				// for invoke without intent para
				if (node.getInterFunNode() != null) {
					SootMethod invokedMethod = node.getInterFunNode().getMethod();
					if (invokedMethod != null) {
						if (hasAnalyzeResutltOfCurrentMehtod(invokedMethod)) {
							SingleMethodModel summary = getSummaryFromStorage(invokedMethod.getSignature());
							singleMethod.getReuseModelSet().add(summary);
						}
					}
				}
			}
			removeInvalidSingleObject(addedSet);
			singleMethod.getSingleObjectSet().addAll(addedSet);
		}
	}

	/**
	 * getSingleObject_flowLevel
	 * 
	 * @param singleMethod
	 * @param targetMap
	 */
	private void getSingleObject_flowLevel(SingleMethodModel singleMethod) {
		SinglePathModel singlePath = new SinglePathModel(singleMethod);
		singlePath.setNodes(singleMethod.getNodePathList());
		Set<SingleObjectModel> addedSet = new HashSet<SingleObjectModel>();
		Set<UnitNode> history = new HashSet<UnitNode>();
		int nodeId = 0;
		for (UnitNode node : singlePath.getNodes()) {
			List<String> context = singlePath.getNode2TraceMap().get(nodeId);
			// avoid repeate add single object to single path
			if (context == null)
				context = new ArrayList<String>();
			if (node.getNodeSetPointToMeMap().containsKey(context) && node.getNodeSetPointToMe(context) != null) {
				SingleObjectModel singleObj = creatSingleObject(singlePath);
				if (singleObj == null)
					continue;
				addedSet.add(singleObj);
				List<UnitNode> workList = singlePath.getNodes();
				handleWorkList(singlePath, singleObj, workList, addedSet);
				if (node.getInterFunNode() != null) {
					SingleMethodModel interFunMethod = node.getInterFunNode();
					for (SingleObjectModel model : interFunMethod.getSingleObjects()) {
						SingleObjectModel copy = creatSingleObject(singlePath);
						copy.merge(model);
						addedSet.add(copy);
					}
				}
			}
			nodeId++;
		}
		for (UnitNode node : singlePath.getNodes()) {
			if (history.contains(node))
				continue;
			// for invoke without intent para
			if (node.getInterFunNode() != null) {
				SootMethod invokedMethod = node.getInterFunNode().getMethod();
				if (invokedMethod != null) {
					if (hasAnalyzeResutltOfCurrentMehtod(invokedMethod)) {
						SingleMethodModel summary = getSummaryFromStorage(invokedMethod.getSignature());
						singleMethod.getReuseModelSet().add(summary);
					}
				}
			}
		}
		removeInvalidSingleObject(addedSet);
		singleMethod.getSingleObjectSet().addAll(addedSet);
	}

	/**
	 * getSingleObject_objectLevel
	 * 
	 * @param singleMethod
	 */
	private void getSingleObject_objectLevel(SingleMethodModel singleMethod) {
		for (SinglePathModel singlePath : singleMethod.getPathSet()) {
			Set<SingleObjectModel> addedSet = new HashSet<SingleObjectModel>();
			Set<UnitNode> history = new HashSet<UnitNode>();
			int nodeId = 0;
			for (UnitNode node : singlePath.getNodes()) {
				List<String> context = singlePath.getNode2TraceMap().get(nodeId);
				// avoid repeate add single object to single path
				if (context == null)
					context = new ArrayList<String>();
				if (node.getNodeSetPointToMeMap().containsKey(context) && node.getNodeSetPointToMe(context) != null) {
					SingleObjectModel singleObj = creatSingleObject(singlePath);
					if (singleObj == null)
						continue;
					addedSet.add(singleObj);
					analyzeSingleObject(singlePath, node, singleObj, nodeId, history, 0, addedSet);
				}
				nodeId++;
			}
			if (!MyConfig.getInstance().getMySwithch().isFunctionExpandAllSwitch()) {
				for (UnitNode node : singlePath.getNodes()) {
					if (history.contains(node))
						continue;
					// for invoke without intent para
					if (node.getInterFunNode() != null) {
						SootMethod invokedMethod = node.getInterFunNode().getMethod();
						if (invokedMethod != null) {
							if (hasAnalyzeResutltOfCurrentMehtod(invokedMethod)) {
								SingleMethodModel summary = getSummaryFromStorage(invokedMethod.getSignature());
								// addedSet.addAll(summary.getSingleObjectSet());
								singleMethod.getReuseModelSet().add(summary);
							}
						}
					}
				}
			}
			// to be tested

			removeInvalidSingleObject(addedSet);
			singleMethod.getSingleObjectSet().addAll(addedSet);
		}
	}

	/**
	 * remove invalid singleObject
	 * 
	 * @param inputSet
	 */
	private void removeInvalidSingleObject(Set<SingleObjectModel> inputSet) {
		Set<SingleObjectModel> addedSet = new HashSet<SingleObjectModel>();
		for (SingleObjectModel obj : inputSet) {
			if (obj instanceof SingleIntentModel) {
				SingleIntentModel intentObj = (SingleIntentModel) obj;
				if (intentObj.getCreateList().size() > 0 || intentObj.getReceiveFromFromRetValueList().size() > 0
						|| intentObj.getReceiveFromParaList().size() > 0
						|| intentObj.getReceiveFromOutList().size() > 0) {
					if (intentObj.getDataHandleList().size() > 0 || intentObj.getSendIntent2FunList().size() > 0) {
						addedSet.add(intentObj);
					}
				}
			} else {
				addedSet = inputSet;
				break;
			}
		}
		inputSet = addedSet;
	}

	/**
	 * analyzeSingleObject
	 * 
	 * @param singlePath
	 * @param node
	 * @param singleObject
	 * @param singleObject
	 * @param nodeId
	 * @param history
	 * @param addedSet
	 */
	protected void analyzeSingleObject(SinglePathModel singlePath, UnitNode node, SingleObjectModel singleObject,
			int nodeId, Set<UnitNode> history, int depth, Set<SingleObjectModel> addedSet) {
		// System.out.println("analyzeSingleObject "+ node.getUnit());
		List<String> context = singlePath.getNode2TraceMap().get(nodeId);
		if (context == null)
			return;
		history.add(node);
		// static fields
		if (MyConfig.getInstance().getMySwithch().isStaticFieldSwitch()) {
			String type = helper.getTypeofUnit(methodUnderAnalysis, node.getUnit());
			if (type.equals("StaticCreateMethod")) {
				handleStaticFieldInitUnits(node.getUnit(), singleObject);
			}
		}

		List<UnitNode> workList = getWorkListofObjectAnalysis(node, singlePath, singleObject, context, addedSet);
		handleWorkList(singlePath, singleObject, workList, addedSet);

		if (MyConfig.getInstance().getMySwithch().isScenario_stack()) {
			for (UnitNode pointedToMeNode : singlePath.getNodes()) {
				if (pointedToMeNode.getType().equals("componentReturn")) {
					singleObject.getNodes().add(pointedToMeNode);
					singleObject.setFinishFlag(true);
				}
			}
		}
	}

	private List<UnitNode> getWorkListofObjectAnalysis(UnitNode node, SinglePathModel singlePath,
			SingleObjectModel singleObject, List<String> context, Set<SingleObjectModel> addedSet) {
		// parameter passing between functions
		boolean findPassing = false;
		List<UnitNode> workList;
		if (MyConfig.getInstance().getMySwithch().isFunctionExpandSwitch())
			findPassing = handleParameterPassing(node, singlePath, singleObject);

		if (!findPassing) {
			// List<UnitNode> tempList = new ArrayList<UnitNode>();
			workList = node.getNodeSetPointToMe(context);
			// for(UnitNode n: workList){
			// if(n.getInterFunNode()!=null){
			// tempList.addAll(n.getInterFunNode().getNodePathList());
			// }
			// }
			// workList.addAll(tempList);
		} else {
			workList = singlePath.getNodes();
			if (node.getInterFunNode() != null) {
				SingleMethodModel interFunMethod = node.getInterFunNode();
				for (SingleObjectModel model : interFunMethod.getSingleObjects()) {
					SingleObjectModel copy = creatSingleObject(singlePath);
					copy.merge(model);
					addedSet.add(copy);
				}
			}
		}
		return workList;
	}

	private void handleWorkList(SinglePathModel singlePath, SingleObjectModel singleObject, List<UnitNode> workList,
			Set<SingleObjectModel> addedSet) {
		if (workList == null)
			return;
		for (UnitNode pointedToMeNode : workList) {
			if (MyConfig.getInstance().getMySwithch().isStaticFieldSwitch()) {
				String type = helper.getTypeofUnit(methodUnderAnalysis, pointedToMeNode.getUnit());
				if (type.equals("StaticCreateMethod")) {
					handleStaticFieldInitUnits(pointedToMeNode.getUnit(), singleObject);
				}
			}
			if (!singlePath.getNodes().contains(pointedToMeNode))
				continue;
			UnitHandler handler = helper.getUnitHandler(pointedToMeNode.getUnit());
			if (handler != null) {
				handler.init(methodUnderAnalysis, pointedToMeNode.getUnit());
				handler.handleSingleObject(singleObject);
				singleObject.getNodes().add(pointedToMeNode);
			}
		}
	}

	// private List<UnitNode> getWorkListofObjectAnalysisInner(UnitNode node,
	// SinglePathModel singlePath, SingleObjectModel singleObject, List<String>
	// context) {
	// //parameter passing between functions
	// boolean findPassing =false;
	// List<UnitNode> workList;
	// if(MyConfig.getInstance().getMySwithch().isContextSensiSwitch())
	// findPassing = handleParameterPassing(node, singlePath, singleObject);
	//
	// if(!findPassing)
	// workList = null;
	// else
	// workList = node.getNodeSetPointToMe(context);
	// return workList;
	// }
	/**
	 * handleParameterPassing
	 * 
	 * @param node
	 * @param singlePath
	 * @param singleObject
	 */
	private boolean handleParameterPassing(UnitNode node, SinglePathModel singlePath, SingleObjectModel singleObject) {
		boolean findPassing = false;
		if (node.getInterFunNode() == null)
			return false;
		UnitNode handleTarget = node;
		SootMethod targetMethod = node.getMethod();
		String targetMethodSig = targetMethod.getSignature();
		Unit targetUnit = node.getUnit();
		InvokeExpr targetInv = SootUtils.getInvokeExp(targetUnit);
		ValueObtainer vo = new ValueObtainer(targetMethodSig, "");
		Context objContext = vo.getContextValue(targetUnit, targetInv, targetMethod, targetMethodSig, 0);
		if (objContext.isEmpty())
			return false;
		if (helper.getUnitHandler(handleTarget.getUnit()) != null)
			return false;
		Set<Unit> targetHistory = new HashSet<Unit>();
		while (helper.getUnitHandler(handleTarget.getUnit()) == null) {
			if (targetHistory.contains(handleTarget.getUnit()))
				break;
			targetHistory.add(handleTarget.getUnit());
			boolean findPs = false;
			if (!handleTarget.getNodeSetPointToMeMap().containsKey((new ArrayList<String>())))
				return false;
			for (UnitNode innerNode : handleTarget.getNodeSetPointToMe(new ArrayList<String>())) {
				if (innerNode.toString().equals(handleTarget.toString()))
					continue;
				if (appModel.getUnit2ParameterSource().containsKey(innerNode.getUnit())) {
					findPs = true;
					handleTarget = innerNode;
					if (singlePath.getNodes().contains(handleTarget)) {
						UnitHandler handler2 = helper.getUnitHandler(handleTarget.getUnit());
						if (handler2 != null) {
							handler2.init(handleTarget.getMethod(), handleTarget.getUnit());
							handler2.handleSingleObject(objContext, singleObject, targetUnit);
							singleObject.getNodes().add(handleTarget);
							findPassing = true;
						}
					}
				}
			}
			if (findPs == false)
				break;
		}
		return findPassing;
	}

	/**
	 * handleStaticFieldInitUnits
	 * 
	 * @param u
	 * @param singleObject
	 */
	private void handleStaticFieldInitUnits(Unit u, SingleObjectModel singleObject) {
		List<Unit> useList = new ArrayList<>();
		JAssignStmt jas = (JAssignStmt) u;
		if (jas.containsFieldRef()) {
			SootField field = jas.getFieldRef().getField();
			Set<StaticFiledInfo> infos = appModel.getStaticRefSignature2UnitMap().get(field.getSignature());
			if (infos == null)
				return;
			for (StaticFiledInfo info : infos) {
				for (Unit unit : info.getSootMethod().getActiveBody().getUnits()) {
					for (ValueBox valBox : unit.getUseAndDefBoxes()) {
						Value value = valBox.getValue();
						if (value == info.getValue()) {
							useList.add(unit);
						}
					}
				}
			}
			for (Unit useUnit : useList) {
				if (helper.getTypeofUnit(methodUnderAnalysis, useUnit).length() == 0)
					continue;
				UnitNode node = new UnitNode(useUnit, methodUnderAnalysis, helper.getTypeofUnit(methodUnderAnalysis,
						useUnit));
				UnitHandler handler = helper.getUnitHandler(useUnit);
				if (handler != null) {
					handler.init(methodUnderAnalysis, useUnit);
					handler.handleSingleObject(singleObject);
					singleObject.getNodes().add(node);
				}
			}
		}
	}

	protected void getSingleComponent(SingleMethodModel singleMethod) {

	}

	/**
	 * initMethodCheck
	 * 
	 * @param m
	 * @return
	 */
	private boolean initMethodCheck(SootMethod m) {
		if (m == null || SootUtils.hasSootActiveBody(m) == false)
			return false;
		if (hasAnalyzeResutltOfCurrentMehtod(m))
			return false;
		if (!MyConfig.getInstance().getMySwithch().allowLibCodeSwitch()) {
			if (!SootUtils.isNonLibClass(m.getDeclaringClass().getName()))
				return false;
		}
		return true;
	}

	/**
	 * Convert CFG to VFG
	 * 
	 * @param targetUnits
	 * @param briefGraph
	 * @param pdg
	 * @param targetBlocks
	 * @param conditionMap
	 * @return
	 */
	private BlockGraph createVFGofGraph(Map<Unit, List<Unit>> targetUnitsMehodLevel, BlockGraph briefGraph,
			Set<Block> targetBlocks, Map<Block, Condition> conditionMap) {
		while (true) {
			int checkcode = briefGraph.hashCode();
			for (Block current : briefGraph.getBlocks()) {
				boolean findTarget = false;
				Iterator<Unit> it = current.iterator();
				while (it.hasNext()) {
					Unit u = it.next();
					findTarget |= targetUnitsMehodLevel.containsKey(u);
				}
				// do not delete heads, and keep them in target
				if (briefGraph.getHeads().contains(current)) {
					if (findTarget)
						targetBlocks.add(current);
					continue;
				}
				// do not delete tails
				if (briefGraph.getTails().contains(current)) {
					continue;
				}
				// do not contian target as well as not the final block
				if (!findTarget) {
					for (Block subpred : current.getPreds()) {
						Set<Block> newSuccs = new HashSet<Block>(subpred.getSuccs());
						newSuccs.addAll(current.getSuccs());
						newSuccs.remove(current);
						subpred.setSuccs(new ArrayList<Block>(newSuccs));
					}
					for (Block subsucc : current.getSuccs()) {
						Set<Block> newPreds = new HashSet<Block>(subsucc.getPreds());
						newPreds.addAll(current.getPreds());
						newPreds.remove(current);
						subsucc.setPreds(new ArrayList<Block>(newPreds));
						Condition oldsucc = conditionMap.get(subsucc);
						Condition oldcurr = conditionMap.get(current);
						if (oldcurr != null) {
							Condition newCond = new Condition(oldsucc, oldcurr, "interact");
							conditionMap.put(subsucc, newCond);
						}
					}
					current.setPreds(new ArrayList<Block>());
					current.setSuccs(new ArrayList<Block>());
				}
			}
			if (checkcode == briefGraph.hashCode())
				break;
		}
		return briefGraph;
	}

	// wrong path

	private void removeCycle(BlockGraph briefGraph) {
		for (Block current : briefGraph.getBlocks()) {
			// current.getSuccs().remove(current);
			List<Block> newSuccs = new ArrayList<Block>(current.getSuccs());
			newSuccs.remove(current);
			current.setSuccs(newSuccs);

			// current.getPreds().remove(current);
			List<Block> newPreds = new ArrayList<Block>(current.getPreds());
			newPreds.remove(current);
			current.setPreds(newPreds);

			for (Block currentSucc : current.getPreds()) {
				for (Block currentPred : current.getSuccs()) {
					if (currentSucc == currentPred) {
						// current.getPreds().remove(currentPred);
						newPreds = new ArrayList<Block>(current.getPreds());
						newPreds.remove(currentPred);
						current.setPreds(newPreds);

						// current.getSuccs().remove(currentPred);
						newSuccs = new ArrayList<Block>(current.getSuccs());
						newSuccs.remove(currentPred);
						current.setSuccs(newSuccs);
					}
				}
			}

		}
	}

	/**
	 * create a tree whose root is "root" node with condition and attribute
	 * analyze
	 * 
	 * @param targetUnitsMehodLevel
	 * @param targetBlocks
	 * @param conditionMap
	 * @param g
	 */
	private UnitNode generateNodeTree(Map<Unit, List<Unit>> targetUnitsMehodLevel, BlockGraph graph,
			Set<Block> targetBlocks, Map<Block, Condition> conditionMap) {
		UnitNode rootNode = new UnitNode(null, methodUnderAnalysis, helper.getTypeofUnit(methodUnderAnalysis, null));
		Map<String, UnitNode> history = new HashMap<String, UnitNode>();
		List<String> context = new ArrayList<String>();
		for (Block currentBlock : graph.getBlocks()) {
			if (targetBlocks.contains(currentBlock))
				iterAnalyzeBlockNodeToBuildNodeTree(context, targetUnitsMehodLevel, rootNode, graph, currentBlock,
						currentBlock, history, conditionMap);
			else if (currentBlock.getPreds().size() == 0 && currentBlock.getSuccs().size() > 0)
				iterAnalyzeBlockNodeToBuildNodeTree(context, targetUnitsMehodLevel, rootNode, graph, currentBlock,
						currentBlock, history, conditionMap);
		}
		return rootNode;
	}

	/**
	 * work with getICCNodeTree
	 * 
	 * @param targetUnitsMehodLevel
	 * @param root
	 * @param g
	 * @param currentBlock
	 * @param currentBlock
	 * @param history
	 */
	private void iterAnalyzeBlockNodeToBuildNodeTree(List<String> context, Map<Unit, List<Unit>> targetUnitsMehodLevel,
			UnitNode predNode, BlockGraph g, Block preBlock, Block currentBlock, Map<String, UnitNode> history,
			Map<Block, Condition> conditionMap) {
		if (history.containsKey(preBlock.hashCode() + " " + currentBlock.hashCode())) {
			// link to the first node of the block, which has been analyzed
			UnitNode firstNode = history.get(preBlock.hashCode() + " " + currentBlock.hashCode());
			predNode.getSuccs().add(firstNode);
			return;
		}
		Iterator<Unit> it = currentBlock.iterator();
		boolean isConditionModified = false;
		while (it.hasNext()) {
			Unit unit = it.next();
			boolean flag = isTargetUnitToBeAnalyzed(unit, currentBlock, g, targetUnitsMehodLevel);
			if (flag) {// || attr!=null
				UnitNode currentNode = new UnitNode(unit, methodUnderAnalysis, helper.getTypeofUnit(
						methodUnderAnalysis, unit));// init
													// node
				appModel.getUnit2NodeMap().put(unit, currentNode);
				currentNode.getPreds().add(predNode);
				predNode.getSuccs().add(currentNode);
				//
				// do not sure, if add ,some node will lose their target
				if (!history.containsKey(preBlock.hashCode() + " " + currentBlock.hashCode())) {
					history.put(preBlock.hashCode() + " " + currentBlock.hashCode(), currentNode);
				}

				List<Unit> targetUnits = targetUnitsMehodLevel.get(unit);
				if (targetUnits != null) {
					for (Unit targetUnit : targetUnits) {
						UnitNode targetNode = appModel.getUnit2NodeMap().get(targetUnit);
						if (targetNode == null) {
							targetNode = new UnitNode(targetUnit, methodUnderAnalysis, helper.getTypeofUnit(
									methodUnderAnalysis, targetUnit));
						}
						currentNode.addBaseNodePointToMap(context, targetNode);
						targetNode.addNodeSetPointToMeMap(context, currentNode);
					}
				}
				// else{
				// UnitNode targetNode = new UnitNode(null, methodUnderAnalysis,
				// helper.getTypeofUnit(methodUnderAnalysis, null));
				// currentNode.addBaseNodePointToMap(context, targetNode);
				// targetNode.addNodeSetPointToMeMap(context, currentNode);
				// }
				/** node with InterFunNode **/
				Stack<String> methodStack = new Stack<String>();
				methodStack.add(currentBlock.getBody().getMethod().getSignature());
				handleNodewithInnerFunction(methodStack, context, currentNode);

				/** condition **/
				if (SootUtils.getInvokeExp(currentNode.getUnit()) == null) {
					/** only record condition for non expression **/
					Condition c = conditionMap.get(currentBlock);
					if (c != null && c.toString() != null && !isConditionModified) {
						Condition condition = null;
						// get the condition of current block
						if (c instanceof ConditionLeaf) {
							condition = new ConditionLeaf((ConditionLeaf) c);
						} else {
							condition = new Condition(c);
						}
						// analyze the condition of each unit, update condition
						// attribute to it
						analyzeCondition(condition);
						// update to new condition
						currentNode.setCondition(condition.toString());
						isConditionModified = true;
					}
				}
				predNode = currentNode; // update for next unit
			}
		}
		for (int i = 0; i < g.getSuccsOf(currentBlock).size(); i++) {
			Block succBlock = g.getSuccsOf(currentBlock).get(i);
			iterAnalyzeBlockNodeToBuildNodeTree(context, targetUnitsMehodLevel, predNode, g, currentBlock, succBlock,
					history, conditionMap);
		}
	}

	/**
	 * handleNodewithInnerFunction
	 * 
	 * @param currentMtdcontext
	 * @param context
	 * @param currentNode
	 */
	private void handleNodewithInnerFunction(Stack<String> methodStack, List<String> currentMtdcontext,
			UnitNode currentNode) {
		InvokeExpr exp = SootUtils.getInvokeExp(currentNode.getUnit());
		if (exp == null)
			return;
		int id = -1;
		for (Value arg : exp.getArgs()) {
			id++;
			boolean flag = false;
			for (String s : objectIdentier) {
				if (arg.getType().toString().contains(s)) {
					flag = true;
				}
			}
			if (flag)
				break;
		}
		Set<SootMethod> smSet = SootUtils.getInvokedMethodSet(methodUnderAnalysis, currentNode.getUnit());
		for (SootMethod invokedMethod : smSet) {
			if (hasAnalyzeResutltOfCurrentMehtod(invokedMethod)) {
				String sig = invokedMethod.getSignature();
				if (methodStack.contains(sig))
					continue;
				else
					methodStack.push(sig);

				/** InterFunNode with formal parameter **/
				List<String> innerMtdContext = new ArrayList<String>(currentMtdcontext);
				String innerSig = currentNode.getUnit().toString() + currentNode.hashCode();
				innerMtdContext.add(innerSig);

				// TODO
				// filter out fragment load
				FragmentAnalyzerHelper helper = new FragmentAnalyzerHelper();
				if (helper.isLoadFunction(currentNode.getUnit())
						|| helper.isSetContentViewFunction(currentNode.getUnit())
						|| helper.isAddTabFunction(currentNode.getUnit())) {
					continue;
				}

				/** add summary of invoked method to current node **/
				// SingleMethodModel summaries =
				// currentSummaryMap.get(invokedMethod.getSignature());
				SingleMethodModel summaries = getSummaryFromStorage(invokedMethod.getSignature());
				currentNode.setInterFunNode(summaries);
				/**
				 * analyze the point to analyze under current context, i.e.,
				 * PointedBaseNode and NodeSetPointToMe
				 **/
				SingleMethodModel subModel = currentNode.getInterFunNode();
				boolean flag = false;
				for (UnitNode subNode : subModel.getNodePathList()) {
					/**
					 * change the point to relation according to the actual and
					 * formal parameter relationship
					 **/
					if (subNode.getUnit() instanceof JIdentityStmt
							&& subNode.getUnit().toString().contains("@parameter" + id + ": " + this.objectIdentier)) {
						flag = true;
						subNode.setFormalParameter(true);
						subNode.addBaseNodePointToMap(innerMtdContext,
								currentNode.getBaseNodePointedTo(currentMtdcontext));
					} else {
						subNode.addBaseNodePointToMap(innerMtdContext, subNode.getBaseNodePointedTo(currentMtdcontext));
					}
					// remove point to relation for invoke without object
					// transfer
					if (currentNode.getBaseNodePointedTo(currentMtdcontext) != null) {
						currentNode.getBaseNodePointedTo(currentMtdcontext).addNodeSetPointToMeMap(currentMtdcontext,
								subNode);
					}
				}
				if (!flag)
					continue;
				for (UnitNode subNode : subModel.getNodePathList()) {
					if (subNode.isFormalParameter())
						continue;
					UnitNode tempNode = subNode.getBaseNodePointedTo(new ArrayList<String>());
					/**
					 * change the point-to relation for units that point to @parameter
					 * id unit
					 **/
					if (tempNode != null) {
						if (tempNode.isFormalParameter())
							subNode.addBaseNodePointToMap(innerMtdContext,
									tempNode.getBaseNodePointedTo(innerMtdContext));
					}
					// TODO
					/** iteratively handle multiple level inner function **/
					handleNodewithInnerFunction(methodStack, innerMtdContext, subNode);
					// if (subNode.getNodeSetPointToMe(innerMtdContext) !=
					// null){
					// for(UnitNode subsub:
					// subNode.getNodeSetPointToMe(innerMtdContext))
					// currentNode.getBaseNodePointedTo(currentMtdcontext).addNodeSetPointToMeMap(currentMtdcontext,
					// subsub);
					// }
				}
			}
		}
	}

	private boolean isTargetUnitToBeAnalyzed(Unit u, Block currentBlock, BlockGraph g,
			Map<Unit, List<Unit>> targetUnitsMehodLevel) {
		// is target
		if (targetUnitsMehodLevel.containsKey(u))
			return true;
		// is ReturnStmtofTailBlockWithoutTarget
		// is tail
		if (!g.getTails().contains(currentBlock))
			return false;

		// without target
		Iterator<Unit> it = currentBlock.iterator();
		while (it.hasNext()) {
			Unit tempU = it.next();
			if (targetUnitsMehodLevel.containsKey(tempU)) {
				return false;
			}
		}
		// is return stmt
		return SootUtils.isMethodReturnUnit(u);
	}

	/**
	 * getUnitNodePaths using BriefBlockGraph
	 * 
	 * @param targetUnitsMehodLevel
	 * @param g
	 * @param conditionMap
	 * @param targetUnitListSet
	 * @return
	 */
	private Set<List<UnitNode>> getUnitNodePaths(UnitNode rootNode) {
		Set<List<UnitNode>> targetUnitPathSet = new HashSet<List<UnitNode>>();
		List<UnitNode> targetUnitPath = new ArrayList<UnitNode>();
		Set<String> history = new HashSet<String>();
		iterGenereatePath(0, rootNode, targetUnitPath, targetUnitPathSet, history);
		return targetUnitPathSet;
	}

	/**
	 * work with method getUnitNodePaths
	 * 
	 * @param targetUnitsMehodLevel
	 * @param mc
	 * @param g
	 * @param block
	 * @param targetUnitsCopy2
	 * @param targetUnitListSet
	 * @param history
	 * @param endFlag
	 * @param endFlag
	 * @param conditionMap
	 */
	private void iterGenereatePath(int depth, UnitNode node, List<UnitNode> targetUnitPath,
			Set<List<UnitNode>> targetUnitPathSet, Set<String> history) {
		if (targetUnitPathSet.size() > 100000)
			return;
		if (depth > 200)
			return;
		if (node.getUnit() != null && !SootUtils.isMethodReturnUnit(node.getUnit()))
			targetUnitPath.add(node);
		int num = 0;
		for (UnitNode succ : node.getSuccs()) {
			num++;
			if (history.contains(node.hashCode() + "," + succ.hashCode()))
				continue;
			if (num == node.getSuccs().size()) {
				history.add(node.hashCode() + "," + succ.hashCode());
				iterGenereatePath(depth + 1, succ, targetUnitPath, targetUnitPathSet, history);
			} else {
				List<UnitNode> targetUnitsCopy = new ArrayList<UnitNode>(targetUnitPath);
				Set<String> historyCopy = new HashSet<String>(history);
				historyCopy.add(node.hashCode() + "," + succ.hashCode());
				iterGenereatePath(depth + 1, succ, targetUnitsCopy, targetUnitPathSet, historyCopy);
			}
		}
		if (targetUnitPath.size() > 0)
			targetUnitPathSet.add(targetUnitPath);
	}

	/**
	 * analyze condition find solution of constraint
	 * 
	 * @param unit
	 * @param condition
	 */
	private void analyzeCondition(Condition condition) {
		if (condition == null)
			return;
		if (condition instanceof ConditionLeaf) {
			ConditionLeaf leaf = (ConditionLeaf) condition;
			ExprHandler handler = ExprHandler.getExprHandler(leaf.getConditionNode());
			for (Value target : handler.getRightValues()) {
				List<Unit> def_var_list = SootUtils.getDefOfLocal(methodUnderAnalysis.getSignature(), target,
						leaf.getUnit());
				if (def_var_list.size() > 0) {
					Unit defUnit = def_var_list.get(0);
					String key = methodUnderAnalysis.getSignature() + "," + defUnit.toString() + ","
							+ defUnit.hashCode();
					Attribute attr = appModel.getUnit2Attribute().get(key);
					if (attr != null) {
						Attribute attrCopy = new Attribute(attr);
						if (leaf.getConditionNode().toString().contains("== 0"))
							attrCopy.turnSatisfity();
						if (!leaf.isSatisfy())
							attrCopy.turnSatisfity(); // wrong
						leaf.setUpdatedAttri(attrCopy);
						if (attr.getPointToUnit() != null)
							leaf.getPointToUnit().add(attr.getPointToUnit());
					} else {
						// non-attribute constraint
						leaf.setPointToUnit(getDefRelatedUnits(defUnit));
					}
				}
			}
		}
		analyzeCondition(condition.getLeft());
		analyzeCondition(condition.getRight());
	}

	private List<Unit> getDefRelatedUnits(Unit defUnit) {
		List<Unit> res = new ArrayList<Unit>();
		StmtHandler stmtHandler = StmtHandler.getStmtHandler(defUnit);
		if (stmtHandler == null)
			return res;
		for (Value val : stmtHandler.getRightValues()) {
			if (val instanceof Local) {
				List<Unit> defList2 = SootUtils.getDefOfLocal(methodUnderAnalysis.getSignature(), val, defUnit);
				if (defList2.size() > 0)
					res.addAll(getDefRelatedUnits(defList2.get(0)));
			} else {
				res.add(defUnit);
			}
		}
		return res;
	}

	/**
	 * get target units in the method related units
	 * 
	 * @param methodUnderAnalysis
	 * 
	 * @param mc
	 * @return
	 */
	private Map<Unit, List<Unit>> getTargetUnitsOfMethod() {
		Map<Unit, List<Unit>> targetMap = new HashMap<Unit, List<Unit>>();
		Iterator<Unit> it = SootUtils.getSootActiveBody(methodUnderAnalysis).getUnits().iterator();
		List<Unit> unitList = new ArrayList<Unit>();
		while (it.hasNext()) {
			Unit u = it.next();
			if (u instanceof JLookupSwitchStmt) {
				JLookupSwitchStmt lookUp = (JLookupSwitchStmt) u;
				for (UnitBox temp : lookUp.getDefaultTarget().getUnitBoxes())
					unitList.add(temp.getUnit());
				for (Unit temp : lookUp.getTargets())
					unitList.add(temp);
			} else {
				unitList.add(u);
			}
		}
		for (Unit u : unitList) {
			if (helper.isTopTargetUnit(u)) {
				Unit pointTo = u;
				if (helper.isStaticCreateMethod(u)) {
					/**
					 * repeat show up $r2 =
					 * r0.<com.iscas.icc_ctrlflow_staticintent.MainActivity:
					 * android.content.Intent mIntent>; virtualinvoke
					 * $r2.<android.content.Intent: android.content.Intent
					 * putExtra
					 * (java.lang.String,java.lang.String)>("IntentFlow",
					 * "ctrlFlow_staticIntent"); $r2 =
					 * r0.<com.iscas.icc_ctrlflow_staticintent.MainActivity:
					 * android.content.Intent mIntent>; virtualinvoke
					 * r0.<com.iscas.icc_ctrlflow_staticintent.MainActivity:
					 * void startActivity(android.content.Intent)>($r2);
					 **/
					for (Unit tempUnit : targetMap.keySet()) {
						if (tempUnit instanceof JAssignStmt) {
							String s1 = ((JAssignStmt) tempUnit).getRightOp().toString();
							String s2 = ((JAssignStmt) u).getRightOp().toString();
							if (s1.equals(s2)) {
								pointTo = tempUnit;
								break;
							}
						}
					}
				}
				iterativeGetTarget(targetMap, u, pointTo, new HashSet<Unit>());
			}
			if (helper.isWarpperTopTargetUnit(u)) {
				if (targetMap.containsKey(u)) {
					for (Unit pointTo : targetMap.get(u))
						iterativeGetTarget(targetMap, u, pointTo, new HashSet<Unit>());
				}
			}
			if (helper.isMyTarget(u)) {
				if (!targetMap.containsKey(u))
					targetMap.put(u, new ArrayList<Unit>());
				if (!targetMap.get(u).contains(u))
					targetMap.get(u).add(u);
			}
			InvokeExpr exp = SootUtils.getInvokeExp(u);
			if (exp == null)
				continue;
			Set<SootMethod> smSet = SootUtils.getInvokedMethodSet(methodUnderAnalysis, u);
			addInvokedMethod2TargetMap(u, smSet, targetMap);
		}
		return targetMap;
	}

	private void addInvokedMethod2TargetMap(Unit u, Set<SootMethod> smSet, Map<Unit, List<Unit>> targetMap) {
		for (SootMethod invokedMethod : smSet) {
			/**
			 * if the invoked one does not be analyzed due to the incomplete of
			 * cg add analyze in this place
			 **/
			if (!hasAnalyzeResutltOfCurrentMehtod(invokedMethod)) {
				if (analyzedMethodSet.contains(invokedMethod))
					continue;

				if (SootUtils.hasSootActiveBody(invokedMethod)) {
					SootMethod oldMethodStore = methodUnderAnalysis;
					SingleMethodModel model = analyzeSingleMethod(invokedMethod);
					drawATGandStatistic(model);
					this.methodUnderAnalysis = oldMethodStore;
				}
			}
			if (!targetMap.containsKey(u)) {
				if (hasAnalyzeResutltOfCurrentMehtod(invokedMethod)) {
					SingleMethodModel model = getSummaryFromStorage(invokedMethod.getSignature());
					if (model.getSingleObjectSet().size() > 0 || model.getReuseModelSet().size() > 0) {
						if (!targetMap.containsKey(u))
							targetMap.put(u, new ArrayList<Unit>());
						if (!targetMap.get(u).contains(u))
							targetMap.get(u).add(u);
						break;
					}
				}
			}
		}

	}

	/**
	 * get target units in the method related units
	 * 
	 * @param resMap
	 * @param u
	 * @param hashSet
	 * @param mc
	 */
	protected void iterativeGetTarget(Map<Unit, List<Unit>> targetMap, Unit u, Unit pointTo, HashSet<Unit> hashSet) {
		hashSet.add(u);
		String type = helper.getTypeofUnit(methodUnderAnalysis, u);
		if (type.length() > 0) {
			if (!targetMap.containsKey(u))
				targetMap.put(u, new ArrayList<Unit>());
			if (!targetMap.get(u).contains(pointTo))
				targetMap.get(u).add(pointTo);
		}

		List<UnitValueBoxPair> use_var_list = SootUtils.getUseOfLocal(methodUnderAnalysis.getSignature(), u);
		for (UnitValueBoxPair useUnit : use_var_list) {
			if (!hashSet.contains(useUnit.getUnit()))
				iterativeGetTarget(targetMap, useUnit.getUnit(), pointTo, hashSet);
		}
	}

	/**
	 * analyze each SingleMehodModel to get several single pathes
	 * 
	 * @param singleMethod
	 * @param targetUnitListSet
	 */
	protected void getSinglePath(SingleMethodModel singleMethod, Set<List<UnitNode>> targetUnitListSet) {
		// node is single path is repeated
		Set<String> nodeHistoryForNotExpand = new HashSet<String>();
		for (List<UnitNode> nodelist : targetUnitListSet) {
			if (nodelist.size() == 0)
				continue;
			for (UnitNode newNode : nodelist) {
				if (!singleMethod.getNodePathList().contains(newNode))
					singleMethod.getNodePathList().add(newNode);
			}
			Set<SinglePathModel> pathSet = new HashSet<SinglePathModel>();
			SinglePathModel initPath = new SinglePathModel(singleMethod);
			initPath.getMethodTrace().add(methodUnderAnalysis.getSubSignature());
			pathSet.add(initPath);
			for (UnitNode n : nodelist) {
				if (n.getUnit() == null)
					continue;
				if (n.getInterFunNode() == null) {
					forNodeNotFunctionCall(n, initPath, pathSet);
				} else if (MyConfig.getInstance().getMySwithch().isFunctionExpandSwitch()) {
					if (!needExpand(n)) {
						String sig = n.getUnit().toString() + n.getMethod().getSignature();
						if (!nodeHistoryForNotExpand.contains(sig)) {
							nodeHistoryForNotExpand.add(sig);
							addAllPathsWithoutExpand(singleMethod, n, pathSet);// path
																				// insensitive
						}
					} else {
						int currentPathNum = singleMethod.getPathSet().size() + pathSet.size();
						expandAllPaths(n, initPath, pathSet, currentPathNum); // path
																				// sensitive{
					}
				} else {
					addAllPathsWithoutExpand(singleMethod, n, pathSet);// path
																		// insensitive
				}
				int currentPathNum = singleMethod.getPathSet().size() + pathSet.size();
				if (currentPathNum > MyConfig.getInstance().getMaxPathNumber()) {
					break;
				}
			}
			singleMethod.getPathSet().addAll(pathSet);

			if (singleMethod.getPathSet().size() > MyConfig.getInstance().getMaxPathNumber()) {
				break;
			}
		}
		removePathWithSingleRet(singleMethod);
		if (singleMethod.getPathSet().size() > 0 || singleMethod.getSingleObjectSet().size() > 0
				|| singleMethod.getReuseModelSet().size() > 0) {
			currentSummaryMap.put(methodUnderAnalysis.getSignature(), singleMethod);
		}
		// String name =
		// methodUnderAnalysis.getDeclaringClass().getName()+"__"+methodUnderAnalysis.getName();
		// System.out.println( name +" = " +singleMethod.getPathSet().size());
	}

	// new idea
	/**
	 * need Expand paths for inner function invocation
	 * 
	 * @param n
	 * @return
	 */
	private boolean needExpand(UnitNode n) {

		if (MyConfig.getInstance().getMySwithch().isFunctionExpandAllSwitch())
			return true;
		for (String s : objectIdentier) {
			if (n.getUnit().toString().contains(s))
				return true;
			if (n.getMethod().getReturnType().toString().equals(s))
				return true;
		}
		for (UnitNode innerNode : n.getInterFunNode().getNodePathList()) {
			if (appModel.getUnit2ParameterSource().containsKey(innerNode.getUnit())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * addAllPathsWithoutExpand path insensitive
	 * 
	 * @param singleMethod
	 * @param n
	 * @param pathSet
	 */
	private void addAllPathsWithoutExpand(SingleMethodModel singleMethod, UnitNode n, Set<SinglePathModel> pathSet) {
		SingleMethodModel interFunMethod = n.getInterFunNode();
		if (interFunMethod == null)
			return;
		singleMethod.getReuseModelSet().add(interFunMethod);
	}

	/**
	 * add the current node n to each path in pathSet path sensitive
	 * 
	 * @param n
	 * @param initPath
	 * @param pathSet
	 */
	private void forNodeNotFunctionCall(UnitNode n, SinglePathModel initPath, Set<SinglePathModel> pathSet) {
		initPath.getNode2TraceMap().put(initPath.getNode2TraceMap().size(), new ArrayList<String>());
		for (SinglePathModel singlePath : pathSet) {
			singlePath.addNode(n);
		}
	}

	/**
	 * expandAllPaths path sensitive
	 * 
	 * @param n
	 * @param initPath
	 * @param pathSet
	 * @param addedPathSet
	 * @param currentPathNum
	 */
	private void expandAllPaths(UnitNode n, SinglePathModel initPath, Set<SinglePathModel> pathSet, int currentPathNum) {
		Set<SinglePathModel> addedPathSet = new HashSet<SinglePathModel>();
		initPath.getNode2TraceMap().put(initPath.getNode2TraceMap().size(), new ArrayList<String>());
		for (SinglePathModel singlePath : pathSet) {
			singlePath.addNode(n);
			if (n.getInterFunNode() != null) {
				String innerSig = n.getUnit().toString() + n.hashCode();
				if (!singlePath.getMethodTrace().contains(
						n.getInterFunNode().getMethod().getSignature() + " --- " + n.getUnit())) {
					singlePath.getMethodTrace().add(
							n.getInterFunNode().getMethod().getSignature() + " --- " + n.getUnit());
				}
				SingleMethodModel interFunMethod = n.getInterFunNode();
				int id = 0;
				SinglePathModel singlePathUpdated = null;
				for (SinglePathModel innerPath : interFunMethod.getPathSet()) {
					id++;
					if (id == interFunMethod.getPathSet().size())
						singlePathUpdated = singlePath;
					else {
						singlePathUpdated = new SinglePathModel();
						singlePathUpdated.copy(singlePath);
						addedPathSet.add(singlePathUpdated);
					}
					singlePathUpdated.merge(innerPath, innerSig);
				}
			}
			if (currentPathNum + addedPathSet.size() > MyConfig.getInstance().getMaxPathNumber()) {
				pathSet.addAll(addedPathSet);
				return;
			}
		}
		pathSet.addAll(addedPathSet);
	}

	/**
	 * removePathWithSingleRet
	 * 
	 * @param singleMethod
	 */
	private void removePathWithSingleRet(SingleMethodModel singleMethod) {
		Set<SinglePathModel> deleteSet = new HashSet<SinglePathModel>();
		for (SinglePathModel path : singleMethod.getPathSet()) {
			if (path.getNodes().size() == 1) {
				if (path.getNodes().get(0).getUnit() instanceof JIdentityStmt) {
					deleteSet.add(path);
				}
			} else if (path.getNodes() == null || path.getNodes().size() == 0) {
				deleteSet.add(path);
			}
		}
		for (SinglePathModel path : deleteSet) {
			singleMethod.getPathSet().remove(path);
		}
	}

	/**
	 * if exisit in currentSummaryMap, i.e., has been analyzed
	 * 
	 * @param method
	 * @return
	 */
	private boolean hasAnalyzeResutltOfCurrentMehtod(SootMethod method) {
		if (currentSummaryMap.containsKey(method.getSignature()))
			return true;
		return false;
	}

	// subsclass of cg edge
	private SingleMethodModel getSummaryFromStorage(String signature) {
		return currentSummaryMap.get(signature);
	}

}
