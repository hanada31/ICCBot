package main.java.analyze.model.analyzeModel;

import java.util.ArrayList;
import java.util.List;

import main.java.analyze.model.analyzeModel.SinglePathModel;
import main.java.analyze.model.analyzeModel.UnitNode;
import main.java.analyze.utils.output.PrintUtils;
import soot.SootMethod;
import soot.Unit;

public class SingleObjectModel {
	protected List<UnitNode> nodes;
	protected SinglePathModel singlePath;
	protected SootMethod method;
	// protected List<String> methodTraceUnrepeated;

	protected List<Unit> createList;
	protected List<Unit> receiveFromParaList;
	protected List<Unit> receiveFromFromRetValueList;

	protected List<Unit> sendIntent2FunList;
	protected List<Unit> dataHandleList;

	protected boolean finishFlag;

	public SingleObjectModel(SinglePathModel singlePath) {
		setNodes(new ArrayList<UnitNode>());
		setSinglePath(singlePath);
		if (singlePath != null && singlePath.getSingleMethod() != null)
			setMethod(singlePath.getSingleMethod().getMethod());
		// setMethodTrace(new ArrayList<String>());

		setFinishFlag(false);
		setCreateList(new ArrayList<Unit>());
		setReceiveFromParaList(new ArrayList<Unit>());

		setReceiveFromFromRetValueList(new ArrayList<Unit>());

		setSendIntent2FunList(new ArrayList<Unit>());
		setDataHandleList(new ArrayList<Unit>());
	}

	public void copy(SingleObjectModel temp) {
		setNodes(temp.nodes);
		setSinglePath(temp.singlePath);
		// setMethodTrace(new ArrayList<>(temp.getMethodTrace()));
		setCreateList(temp.getCreateList());
		setReceiveFromParaList(temp.getReceiveFromParaList());

		setReceiveFromFromRetValueList(temp.getReceiveFromFromRetValueList());
		setSendIntent2FunList(temp.getSendIntent2FunList());
		setDataHandleList(temp.getDataHandleList());

	}

	public void merge(SingleObjectModel temp) {
		getNodes().addAll(temp.nodes);
		// for (String me : temp.getMethodTrace()) {
		// if (!getMethodTrace().contains(me))
		// getMethodTrace().add(me);
		// }
		getCreateList().addAll(temp.getCreateList());
		getReceiveFromParaList().addAll(temp.getReceiveFromParaList());
		getReceiveFromFromRetValueList().addAll(temp.getReceiveFromFromRetValueList());

		getSendIntent2FunList().addAll(temp.getSendIntent2FunList());
		getDataHandleList().addAll(temp.getDataHandleList());

		this.finishFlag = this.finishFlag | temp.finishFlag;
	}

	@Override
	public int hashCode() {
		return this.toString().length();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SingleObjectModel) {
			SingleObjectModel model = (SingleObjectModel) obj;
			return model.toHashString().equals(this.toHashString());
		} else {
			return false;
		}
	}

	public StringBuilder appendList2SB(StringBuilder sb, List<?> list, String tag) {
		if (list.size() > 0)
			sb.append(tag + ":\n" + PrintUtils.printList(list, "\n") + "\n");
		return sb;
	}

	public String toHashString() {
		String res = "";
		res += createList.size();
		res += receiveFromParaList.size();
		res += receiveFromFromRetValueList.size();
		res += sendIntent2FunList.size();
		res += receiveFromParaList.size();
		for (UnitNode n : getNodes()) {
			res += n.getUnit().toString();
		}
		return res;
	}

	@Override
	public String toString() {
		String res = "";
		StringBuilder sb = new StringBuilder();
		appendList2SB(sb, getNodes(), "nodes");
		res += sb.toString();

		res += "finishFlag:" + finishFlag + "\n";

		res += "createList:" + PrintUtils.printList(createList) + "\n";
		res += "receiveFromParaList:" + PrintUtils.printList(receiveFromParaList) + "\n";
		res += "receiveFromFromRetValueList:" + PrintUtils.printList(receiveFromFromRetValueList) + "\n";
		res += "sendIntent2FunList:" + PrintUtils.printList(sendIntent2FunList) + "\n";
		res += "dataHandleList:" + PrintUtils.printList(dataHandleList, "\n") + "\n";
		return res;
	}

	public List<Unit> getDataHandleList() {
		return dataHandleList;
	}

	public void setDataHandleList(List<Unit> dataHandleList) {
		this.dataHandleList = dataHandleList;
	}

	//
	// public List<String> getMethodTrace() {
	// return methodTraceUnrepeated;
	// }
	//
	// public void setMethodTrace(List<String> methodTrace) {
	// this.methodTraceUnrepeated = methodTrace;
	// }

	public SinglePathModel getSinglePath() {
		return singlePath;
	}

	public void setSinglePath(SinglePathModel singlePath) {
		this.singlePath = singlePath;
	}

	public List<UnitNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<UnitNode> nodes) {
		this.nodes = nodes;
	}

	/**
	 * @return the method
	 */
	public SootMethod getMethod() {
		return method;
	}

	/**
	 * @param method
	 *            the method to set
	 */
	public void setMethod(SootMethod method) {
		this.method = method;
	}

	public List<Unit> getCreateList() {
		return createList;
	}

	public void setCreateList(List<Unit> createList) {
		this.createList = createList;
	}

	public List<Unit> getSendIntent2FunList() {
		return sendIntent2FunList;
	}

	public void setSendIntent2FunList(List<Unit> sendIntent2FunList) {
		this.sendIntent2FunList = sendIntent2FunList;
	}

	public List<Unit> getReceiveFromParaList() {
		return receiveFromParaList;
	}

	public void setReceiveFromParaList(List<Unit> receiveFromParaList) {
		this.receiveFromParaList = receiveFromParaList;
	}

	public List<Unit> getReceiveFromFromRetValueList() {
		return receiveFromFromRetValueList;
	}

	public void setReceiveFromFromRetValueList(List<Unit> receiveFromFromRetValueList) {
		this.receiveFromFromRetValueList = receiveFromFromRetValueList;
	}

	public boolean isFinishFlag() {
		return finishFlag;
	}

	public void setFinishFlag(boolean finishFlag) {
		this.finishFlag = finishFlag;
	}

}
