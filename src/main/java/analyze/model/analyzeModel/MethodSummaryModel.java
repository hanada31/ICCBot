package main.java.analyze.model.analyzeModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import main.java.analyze.utils.output.PrintUtils;
import soot.SootMethod;

public class MethodSummaryModel {
	private SootMethod method;
	private String componentName;
	private Set<PathSummaryModel> singlePathSet;
	private Set<PathSummaryModel> analyzedSinglePathSet;
	private Set<ObjectSummaryModel> singleObjectSet;
	private List<UnitNode> nodePathList;
	private int maxMethodTraceDepth;
	private Set<MethodSummaryModel> reuseModelSet;

	public MethodSummaryModel(String className, SootMethod method) {
		this.maxMethodTraceDepth = 0;
		this.componentName = className;
		this.method = method;
		this.nodePathList = new ArrayList<UnitNode>();
		this.singlePathSet = new HashSet<PathSummaryModel>();
		this.analyzedSinglePathSet = new HashSet<PathSummaryModel>();
		this.singleObjectSet = new HashSet<ObjectSummaryModel>();
		this.setReuseModelSet(new HashSet<MethodSummaryModel>());
	}

	public MethodSummaryModel(MethodSummaryModel temp) {
		this.method = temp.method;
		this.componentName = temp.componentName;
		this.maxMethodTraceDepth = temp.maxMethodTraceDepth;
		this.nodePathList = temp.getNodePathList();
		this.singlePathSet = new HashSet<PathSummaryModel>(temp.getPathSet());
		this.analyzedSinglePathSet = new HashSet<PathSummaryModel>(temp.getAnalyzedSinglePathSet());
		this.singleObjectSet = new HashSet<ObjectSummaryModel>(temp.getSingleObjectSet());
		this.setReuseModelSet(new HashSet<MethodSummaryModel>(temp.getReuseModelSet()));
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public StringBuilder appendSet2SB(StringBuilder sb, Set<?> set, String tag) {
		if (set.size() > 0)
			sb.append(tag + ":\n" + PrintUtils.printSet(set) + "\n");
		return sb;
	}

	public StringBuilder appendList2SB(StringBuilder sb, List<?> list, String tag) {
		if (list.size() > 0)
			sb.append(tag + ":\n" + PrintUtils.printList(list, "\n") + "\n");
		return sb;
	}

	public StringBuilder appendSet2SB(StringBuilder sb, Set<?> set, String tag, String split) {
		if (set.size() > 0)
			sb.append(tag + ":\n" + PrintUtils.printSet(set, split) + "\n");
		return sb;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		appendList2SB(sb, getNodePathList(), "pathList");
		return sb.toString();
	}

	public List<UnitNode> getNodePathList() {
		return nodePathList;
	}

	public SootMethod getMethod() {
		return method;
	}

	public int getMaxMethodTraceDepth() {
		return maxMethodTraceDepth;
	}

	public void setMaxMethodTraceDepth(int maxMethodTraceDepth) {
		this.maxMethodTraceDepth = maxMethodTraceDepth;
	}

	public Set<PathSummaryModel> getPathSet() {
		return singlePathSet;
	}

	public void setSinglePathSet(Set<PathSummaryModel> singlePathSet) {
		this.singlePathSet = singlePathSet;
	}

	/**
	 * @return the analyzedSinglePathSet
	 */
	public Set<PathSummaryModel> getAnalyzedSinglePathSet() {
		return analyzedSinglePathSet;
	}

	/**
	 * @param analyzedSinglePathSet
	 *            the analyzedSinglePathSet to set
	 */
	public void setAnalyzedSinglePathSet(Set<PathSummaryModel> analyzedSinglePathSet) {
		this.analyzedSinglePathSet = analyzedSinglePathSet;
	}

	/**
	 * @return the singleObjectSet
	 */
	public Set<ObjectSummaryModel> getSingleObjectSet() {
		return singleObjectSet;
	}

	/**
	 * @param singleObjectSet
	 *            the singleObjectSet to set
	 */
	public void setSingleObjectSet(Set<ObjectSummaryModel> singleObjectSet) {
		this.singleObjectSet = singleObjectSet;
	}

	/**
	 * @return the reuseModelSet
	 */
	public Set<MethodSummaryModel> getReuseModelSet() {
		return reuseModelSet;
	}

	/**
	 * @param reuseModelSet
	 *            the reuseModelSet to set
	 */
	public void setReuseModelSet(Set<MethodSummaryModel> reuseModelSet) {
		this.reuseModelSet = reuseModelSet;
	}

	/**
	 * get all SingleObjects use time to replace space
	 * 
	 * @return
	 */
	public Set<ObjectSummaryModel> getSingleObjects() {
		Set<ObjectSummaryModel> objectSet = new HashSet<ObjectSummaryModel>();
		Set<MethodSummaryModel> history = new HashSet<MethodSummaryModel>();
		getSingleObjectsIterative(this, objectSet, history);

		return objectSet;
	}

	private void getSingleObjectsIterative(MethodSummaryModel singleMethod, Set<ObjectSummaryModel> objectSet,
			Set<MethodSummaryModel> history) {
		if (history.contains(singleMethod))
			return;
		history.add(singleMethod);
		objectSet.addAll(singleMethod.getSingleObjectSet());
		for (MethodSummaryModel model : singleMethod.getReuseModelSet()) {
			getSingleObjectsIterative(model, objectSet, history);
		}
	}
}
