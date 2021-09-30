package main.java.client.obj.model.ictg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import main.java.analyze.model.analyzeModel.ObjectSummaryModel;
import main.java.analyze.model.analyzeModel.PathSummaryModel;
import main.java.analyze.utils.output.PrintUtils;
import main.java.client.obj.model.component.BundleType;
import main.java.client.obj.model.component.ExtraData;
import soot.Unit;

public class IntentSummaryModel extends ObjectSummaryModel {

	// unit
	private List<Unit> receiveFromOutList;
	private List<Unit> sendIntent2ICCList;
	// value
	private List<String> getActionCandidateList;
	private List<String> getCategoryCandidateList;
	private List<String> getDataCandidateList;
	private List<String> getTypeCandidateList;
	private BundleType getExtrasCandidateList;

	private List<String> setActionValueList;
	private List<String> setCategoryValueList;
	private List<String> setDataValueList;
	private List<String> setTypeValueList;
	private BundleType setExtrasValueList;
	private List<String> setDestinationList;
	private List<String> setFlagsList;
	private String targetType;

	public IntentSummaryModel(PathSummaryModel pathSummary) {
		super(pathSummary);
		setReceiveFromOutList(new ArrayList<Unit>());
		setSendIntent2ICCList(new ArrayList<Unit>());

		// intent data get operation
		setGetActionCandidateList(new ArrayList<String>());
		setGetCategoryCandidateList(new ArrayList<String>());
		setGetDataCandidateList(new ArrayList<String>());
		setGetTypeCandidateList(new ArrayList<String>());
		setGetExtrasCandidateList(new BundleType());

		setListActionValueList(new ArrayList<String>());
		setListCategoryValueList(new ArrayList<String>());
		setListDataValueList(new ArrayList<String>());
		setListTypeValueList(new ArrayList<String>());

		setListDestinationList(new ArrayList<String>());
		setListFlagsList(new ArrayList<String>());
		setListExtrasValueList(new BundleType());
	}

	public void copy(IntentSummaryModel temp) {
		super.copy(temp);
		setReceiveFromOutList(temp.getReceiveFromOutList());
		setSendIntent2ICCList(temp.getSendIntent2ICCList());

		// intent data get operation
		setGetActionCandidateList(temp.getGetActionCandidateList());
		setGetCategoryCandidateList(temp.getGetCategoryCandidateList());
		setGetDataCandidateList(temp.getGetDataCandidateList());
		setGetTypeCandidateList(temp.getGetTypeCandidateList());
		setGetExtrasCandidateList(temp.getGetExtrasCandidateList());

		setListActionValueList(temp.getSetActionValueList());
		setListCategoryValueList(temp.getSetCategoryValueList());
		setListDataValueList(temp.getSetDataValueList());
		setListTypeValueList(temp.getSetTypeValueList());
		setListDestinationList(temp.getSetDestinationList());
		setListFlagsList(temp.getSetFlagsList());
		setListExtrasValueList(temp.getSetExtrasValueList());
	}

	@Override
	public void merge(ObjectSummaryModel temp) {
		super.merge(temp);
		IntentSummaryModel temp2 = (IntentSummaryModel) temp;
		getReceiveFromOutList().addAll(temp2.getReceiveFromOutList());
		getSendIntent2ICCList().addAll(temp2.getSendIntent2ICCList());

		// intent data get operation
		getGetActionCandidateList().addAll(temp2.getGetActionCandidateList());
		getGetCategoryCandidateList().addAll(temp2.getGetCategoryCandidateList());
		getGetDataCandidateList().addAll(temp2.getGetDataCandidateList());
		getGetTypeCandidateList().addAll(temp2.getGetTypeCandidateList());
		Map<String, List<ExtraData>> extraMap = temp2.getGetExtrasCandidateList().obtainBundle();
		for (Entry<String, List<ExtraData>> en : extraMap.entrySet()) {
			getGetExtrasCandidateList().obtainBundle().put(en.getKey(), en.getValue());
		}
		for (Entry<String, List<ExtraData>> entry : temp2.getGetExtrasCandidateList().obtainBundle().entrySet())
			getGetExtrasCandidateList().obtainBundle().put(entry.getKey(), entry.getValue());

		getSetActionValueList().addAll(temp2.getSetActionValueList());
		getSetCategoryValueList().addAll(temp2.getSetCategoryValueList());
		getSetDataValueList().addAll(temp2.getSetDataValueList());
		getSetTypeValueList().addAll(temp2.getSetTypeValueList());
		getSetDestinationList().addAll(temp2.getSetDestinationList());
		getSetFlagsList().addAll(temp2.getSetFlagsList());
		for (Entry<String, List<ExtraData>> entry : temp2.getSetExtrasValueList().obtainBundle().entrySet())
			getSetExtrasValueList().obtainBundle().put(entry.getKey(), entry.getValue());
		setTargetType(temp2.getTargetType());

	}

	@Override
	public String toHashString() {
		String res = super.toHashString();

		res += PrintUtils.printList(setActionValueList);
		res += PrintUtils.printList(setCategoryValueList);
		res += PrintUtils.printList(setDataValueList);
		res += PrintUtils.printList(setTypeValueList);
		res += PrintUtils.printList(setFlagsList);
		res += PrintUtils.printList(setFlagsList);
		res += PrintUtils.printList(setDestinationList);
		res += setExtrasValueList.obtainBundle();
		return res;
	}

	@Override
	public String toString() {
		String res = super.toString();
		res += "receiveFromOutList:" + PrintUtils.printList(receiveFromOutList) + "\n";
		res += "sendIntent2ICCList:" + PrintUtils.printList(sendIntent2ICCList) + "\n";

		res += "getActionCandidateList:" + PrintUtils.printList(getActionCandidateList) + "\n";
		res += "getCategoryCandidateList:" + PrintUtils.printList(getCategoryCandidateList) + "\n";
		res += "getDataCandidateList:" + PrintUtils.printList(getDataCandidateList) + "\n";
		res += "getTypeCandidateList:" + PrintUtils.printList(getTypeCandidateList) + "\n";
		res += "getExtrasCandidateList:" + getExtrasCandidateList + "\n";

		res += "ListActionValueList:" + PrintUtils.printList(setActionValueList) + "\n";
		res += "ListCategoryValueList:" + PrintUtils.printList(setCategoryValueList) + "\n";
		res += "ListDataValueList:" + PrintUtils.printList(setDataValueList) + "\n";
		res += "ListTypeValueList:" + PrintUtils.printList(setTypeValueList) + "\n";
		res += "ListExtrasValueList:" + setExtrasValueList.obtainBundle() + "\n";
		res += "ListDestinationList:" + PrintUtils.printList(setDestinationList) + "\n";
		res += "ListFlagsList:" + PrintUtils.printList(setFlagsList) + "\n";
		return res;
	}

	public BundleType getGetExtrasCandidateList() {
		return getExtrasCandidateList;
	}

	public void setGetExtrasCandidateList(BundleType getExtrasCandidateList) {
		this.getExtrasCandidateList = getExtrasCandidateList;
	}

	public List<String> getGetActionCandidateList() {
		return getActionCandidateList;
	}

	public void setGetActionCandidateList(List<String> getActionCandidateList) {
		this.getActionCandidateList = getActionCandidateList;
	}

	public List<String> getGetCategoryCandidateList() {
		return getCategoryCandidateList;
	}

	public void setGetCategoryCandidateList(List<String> getCategoryCandidateList) {
		this.getCategoryCandidateList = getCategoryCandidateList;
	}

	public List<String> getGetDataCandidateList() {
		return getDataCandidateList;
	}

	public void setGetDataCandidateList(List<String> List) {
		this.getDataCandidateList = List;
	}

	public List<String> getGetTypeCandidateList() {
		return getTypeCandidateList;
	}

	public void setGetTypeCandidateList(List<String> getTypeCandidateList) {
		this.getTypeCandidateList = getTypeCandidateList;
	}

	public List<String> getSetActionValueList() {
		return setActionValueList;
	}

	public void setListActionValueList(List<String> setActionValueList) {
		this.setActionValueList = setActionValueList;
	}

	public List<String> getSetCategoryValueList() {
		return setCategoryValueList;
	}

	public void setListCategoryValueList(List<String> setCategoryValueList) {
		this.setCategoryValueList = setCategoryValueList;
	}

	public List<String> getSetDataValueList() {
		return setDataValueList;
	}

	public void setListDataValueList(List<String> setDataValueList) {
		this.setDataValueList = setDataValueList;
	}

	public List<String> getSetTypeValueList() {
		return setTypeValueList;
	}

	public void setListTypeValueList(List<String> setTypeValueList) {
		this.setTypeValueList = setTypeValueList;
	}

	public BundleType getSetExtrasValueList() {
		return setExtrasValueList;
	}

	public void setListExtrasValueList(BundleType setExtrasValueList) {
		this.setExtrasValueList = setExtrasValueList;
	}

	public List<String> getSetDestinationList() {
		return setDestinationList;
	}

	public void setListDestinationList(List<String> setDestinationList) {
		this.setDestinationList = setDestinationList;
	}

	public List<Unit> getSendIntent2ICCList() {
		return sendIntent2ICCList;
	}

	public void setSendIntent2ICCList(List<Unit> sendIntent2ICCList) {
		this.sendIntent2ICCList = sendIntent2ICCList;
	}

	public List<String> getSetFlagsList() {
		return setFlagsList;
	}

	public void setListFlagsList(List<String> setFlagsList) {
		this.setFlagsList = setFlagsList;
	}

	public List<Unit> getReceiveFromOutList() {
		return receiveFromOutList;
	}

	public void setReceiveFromOutList(List<Unit> receiveFromOutList) {
		this.receiveFromOutList = receiveFromOutList;
	}

	/**
	 * @return the targetType
	 */
	public String getTargetType() {
		return targetType;
	}

	/**
	 * @param targetType
	 *            the targetType to List
	 */
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

}
