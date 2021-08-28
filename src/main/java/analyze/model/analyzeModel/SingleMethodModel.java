package main.java.analyze.model.analyzeModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import main.java.analyze.utils.output.PrintUtils;
import soot.SootMethod;

public class SingleMethodModel {
	private SootMethod method;
	private String componentName;
	private Set<SinglePathModel> singlePathSet;
	private Set<SinglePathModel> analyzedSinglePathSet;
	private Set<SingleObjectModel> singleObjectSet;
	private List<UnitNode> nodePathList;
	private int maxMethodTraceDepth;
	private Set<SingleMethodModel> reuseModelSet;

	public SingleMethodModel(String className, SootMethod method) {
		this.maxMethodTraceDepth = 0;
		this.componentName = className;
		this.method = method;
		this.nodePathList = new ArrayList<UnitNode>();
		this.singlePathSet = new HashSet<SinglePathModel>();
		this.analyzedSinglePathSet = new HashSet<SinglePathModel>();
		this.singleObjectSet = new HashSet<SingleObjectModel>();
		this.setReuseModelSet(new HashSet<SingleMethodModel>());
	}

	public SingleMethodModel(SingleMethodModel temp) {
		this.method = temp.method;
		this.componentName = temp.componentName;
		this.maxMethodTraceDepth = temp.maxMethodTraceDepth;
		this.nodePathList = temp.getNodePathList();
		this.singlePathSet = new HashSet<SinglePathModel>(temp.getPathSet());
		this.analyzedSinglePathSet = new HashSet<SinglePathModel>(temp.getAnalyzedSinglePathSet());
		this.singleObjectSet = new HashSet<SingleObjectModel>(temp.getSingleObjectSet());
		this.setReuseModelSet(new HashSet<SingleMethodModel>(temp.getReuseModelSet()));
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

	public Set<SinglePathModel> getPathSet() {
		return singlePathSet;
	}

	public void setSinglePathSet(Set<SinglePathModel> singlePathSet) {
		this.singlePathSet = singlePathSet;
	}

	/**
	 * @return the analyzedSinglePathSet
	 */
	public Set<SinglePathModel> getAnalyzedSinglePathSet() {
		return analyzedSinglePathSet;
	}

	/**
	 * @param analyzedSinglePathSet
	 *            the analyzedSinglePathSet to set
	 */
	public void setAnalyzedSinglePathSet(Set<SinglePathModel> analyzedSinglePathSet) {
		this.analyzedSinglePathSet = analyzedSinglePathSet;
	}

	/**
	 * @return the singleObjectSet
	 */
	public Set<SingleObjectModel> getSingleObjectSet() {
		return singleObjectSet;
	}

	/**
	 * @param singleObjectSet
	 *            the singleObjectSet to set
	 */
	public void setSingleObjectSet(Set<SingleObjectModel> singleObjectSet) {
		this.singleObjectSet = singleObjectSet;
	}

	/**
	 * @return the reuseModelSet
	 */
	public Set<SingleMethodModel> getReuseModelSet() {
		return reuseModelSet;
	}

	/**
	 * @param reuseModelSet
	 *            the reuseModelSet to set
	 */
	public void setReuseModelSet(Set<SingleMethodModel> reuseModelSet) {
		this.reuseModelSet = reuseModelSet;
	}

	/**
	 * get all SingleObjects use time to replace space
	 * 
	 * @return
	 */
	public Set<SingleObjectModel> getSingleObjects() {
		Set<SingleObjectModel> objectSet = new HashSet<SingleObjectModel>();
		Set<SingleMethodModel> history = new HashSet<SingleMethodModel>();
		getSingleObjectsIterative(this, objectSet, history);

		return objectSet;
	}

	private void getSingleObjectsIterative(SingleMethodModel singleMethod, Set<SingleObjectModel> objectSet,
			Set<SingleMethodModel> history) {
		if (history.contains(singleMethod))
			return;
		history.add(singleMethod);
		objectSet.addAll(singleMethod.getSingleObjectSet());
		for (SingleMethodModel model : singleMethod.getReuseModelSet()) {
			getSingleObjectsIterative(model, objectSet, history);
		}
	}
}
