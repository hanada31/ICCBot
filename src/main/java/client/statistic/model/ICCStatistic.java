package main.java.client.statistic.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import main.java.client.obj.model.ictg.SingleIntentModel;

public class ICCStatistic {

	private int iCCFlowNum = 0;
	private Map<String, Set<SingleIntentModel>> intentSummaryTypeMap;
	private Map<String, Set<SingleIntentModel>> intentSummaryReceiveTypeMap;
	private Map<String, Set<SingleIntentModel>> intentSummaryUsedTypeMap;
	private Map<String, Set<SingleIntentModel>> intentSummarySetTypeMap;
	private Map<String, Set<SingleIntentModel>> intentSummaryNewTypeMap;
	private Map<String, Set<SingleIntentModel>> intentSummarySendTypeMap;
	private Map<String, Set<String>> destinationMap;
	private Map<Integer, Set<String>> destinationNum2MethodSet;
	private int intraDestinationNum;
	private int interDestinationNum;

	public ICCStatistic() {
		intentSummaryTypeMap = new HashMap<String, Set<SingleIntentModel>>();
		intentSummaryReceiveTypeMap = new HashMap<String, Set<SingleIntentModel>>();
		intentSummarySetTypeMap = new HashMap<String, Set<SingleIntentModel>>();
		intentSummaryUsedTypeMap = new HashMap<String, Set<SingleIntentModel>>();
		intentSummaryNewTypeMap = new HashMap<String, Set<SingleIntentModel>>();
		intentSummarySendTypeMap = new HashMap<String, Set<SingleIntentModel>>();
		destinationMap = new HashMap<String, Set<String>>();
		destinationNum2MethodSet = new HashMap<Integer, Set<String>>();
	}

	public int getICCFlowNum() {
		return iCCFlowNum;
	}

	public void setICCFlowNum(int iCCFlowNum) {
		this.iCCFlowNum = iCCFlowNum;
	}

	public void addICCFlowNum(int iCCFlowNum) {
		this.iCCFlowNum += iCCFlowNum;
	}

	public Map<String, Set<SingleIntentModel>> getIntentSummaryTypeMap() {
		return intentSummaryTypeMap;
	}

	public Map<String, Set<SingleIntentModel>> getIntentSummaryReceiveTypeMap() {
		return intentSummaryReceiveTypeMap;
	}

	public Map<String, Set<SingleIntentModel>> getIntentSummarySetTypeMap() {
		return intentSummarySetTypeMap;
	}

	public Map<String, Set<SingleIntentModel>> getIntentSummaryUsedTypeMap() {
		return intentSummaryUsedTypeMap;
	}

	public Map<String, Set<SingleIntentModel>> getIntentSummaryNewTypeMap() {
		return intentSummaryNewTypeMap;
	}

	public Map<String, Set<SingleIntentModel>> getIntentSummarySendTypeMap() {
		return intentSummarySendTypeMap;
	}

	public Map<String, Set<String>> getDestinationMap() {
		return destinationMap;
	}

	public Map<Integer, Set<String>> getDestinationNum2MethodSet() {
		return destinationNum2MethodSet;
	}

	public int getIntraDestinationNum() {
		return intraDestinationNum;
	}

	public void addIntraDestinationNum() {
		this.intraDestinationNum++;
	}

	public int getInterDestinationNum() {
		return interDestinationNum;
	}

	public void addInterDestinationNum() {
		this.interDestinationNum++;
	}

}
