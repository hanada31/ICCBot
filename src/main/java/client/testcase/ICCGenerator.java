package main.java.client.testcase;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.microsoft.z3.Model;

import main.java.Analyzer;
import main.java.Global;
import main.java.analyze.model.analyzeModel.Attribute;
import main.java.analyze.utils.CollectionUtils;
import main.java.analyze.utils.ConstraintSolver;
import main.java.analyze.utils.output.PrintUtils;
import main.java.client.obj.model.component.BundleType;
import main.java.client.obj.model.component.ComponentModel;
import main.java.client.obj.model.component.ExtraData;
import main.java.client.obj.model.ictg.ICCMsg;
import main.java.client.obj.model.ictg.IntentRecieveModel;
import main.java.client.obj.model.ictg.IntentSummaryModel;

public class ICCGenerator extends Analyzer {
	String className;
	Set<ICCMsg> ICCSet;
	
	public ICCGenerator(String className) {
		super();
		this.className = className;
		this.ICCSet = new HashSet<ICCMsg>();
	}

	@Override
	public void analyze() {
		ComponentModel component = appModel.getComponentMap().get(className);
		IntentRecieveModel receiveModel = component.getReceiveModel();
		ICCGenerationFromIntentSummaryModel(receiveModel.getIntentObjsbyICCMsg());
		ICCGenerationFromIntentSummaryModel(receiveModel.getIntentObjsbySpec());
		
	}

	private void ICCGenerationFromIntentSummaryModel(Set<IntentSummaryModel> modelSet) {
		
		for(IntentSummaryModel model: modelSet){
			List<String> actions = model.getGetActionCandidateList();
			List<String> categories = model.getGetCategoryCandidateList();
			List<String> datas = model.getGetDataCandidateList();
			List<String> types = model.getGetTypeCandidateList();
			BundleType extras = model.getGetExtrasCandidateList();
			
			//strategies
			genereateICCRandomely(actions, categories, datas, types, extras);
			//genereateICCXXX
			//genereateICCYYY
		}
	} 

	/**
	 * randomly generate one ICCMsg
	 * @param actions
	 * @param categories
	 * @param datas
	 * @param types
	 * @param extras
	 */
	@SuppressWarnings("unchecked")
	private void genereateICCRandomely(List<String> actions, List<String> categories, List<String> datas,
		List<String> types, BundleType extras) {
		ICCMsg msg = new ICCMsg();
		msg.setAction( getRandomElementFromSet(actions));
		msg.setCategory(getRandomElementSetFromSet(categories));
		msg.setData( getRandomElementFromSet(datas));
		msg.setType( getRandomElementFromSet(types));
		Set<String> extraSet = new HashSet<String>();
		for(List<ExtraData> eds: extras.getBundle().values())
			for(ExtraData ed: eds)
				extraSet.add(ed.toString());
		msg.setExtra(extraSet);
		System.out.println(className +"\t"+msg.toString());
		ICCSet.add(msg);	
	}
	
	/**
	 * randomly generate one attribute for ICCMsg
	 * @param s
	 * @return  
	 */
	private String getRandomElementFromSet(List<String> actions){
		actions.add("");
		Object[] obj =actions.toArray();
		String res =  (String) obj[(int)(Math.random()*obj.length)];
		return res;
	}
	/**
	 * randomly generate a set of attribute for ICCMsg
	 * @param s
	 * @return 
	 * @return 
	 */
	private Set<String> getRandomElementSetFromSet(Collection<String> categories){
		categories.add(null);
		HashSet<String> newSet = new HashSet<String>();
		Object[] obj =categories.toArray();
		newSet.add((String) obj[(int)(Math.random()*obj.length)]);
		return newSet;
	}
	
	/**
	 * generate ICC set to test case generator
	 * @return
	 */
	public Set<ICCMsg> getICCSet(){
		return this.ICCSet;
	}
	

//	/**
//	 * add Single Null to all used paths from appModel.pathMap_AU to
//	 * appModel.pathMap_SNV
//	 * 
//	 * @throws CloneNotSupportedException
//	 */
//	private void generateSingleNullAUPs2Map() throws CloneNotSupportedException {
//		for (ComponentModel component : appModel.getComponentMap().values()) {
//			addNullAUPs(component.getReceiveModel().getPathMap_AU(), component.getReceiveModel().getPathMap_SNV());
//			addActionAUPs(component.getReceiveModel().getPathMap_AU(), component.getReceiveModel().getPathMap_SNV());
//			addCategoryAUPs(component.getReceiveModel().getPathMap_AU(), component.getReceiveModel().getPathMap_SNV());
//			addDataAUPs(component.getReceiveModel().getPathMap_AU(), component.getReceiveModel().getPathMap_SNV());
//			addTypeAUPs(component.getReceiveModel().getPathMap_AU(), component.getReceiveModel().getPathMap_SNV());
//			addExtrasAUPs(component, component.getReceiveModel().getPathMap_AU(), component.getReceiveModel()
//					.getPathMap_SNV());
//		}
//	}
//
//	/**
//	 * add null ICC to mapTo
//	 * 
//	 * @param pathMap_SNV
//	 * @param mapFrom
//	 * @param mapTo
//	 * @throws CloneNotSupportedException
//	 */
//	private void addNullAUPs(Map<String, Set<ICCMsg>> mapFrom, Map<String, Set<ICCMsg>> mapTo)
//			throws CloneNotSupportedException {
//		for (Entry<String, Set<ICCMsg>> en : mapFrom.entrySet()) {
//			ICCMsg newMsg = new ICCMsg("");
//			CollectionUtils.addNewMsg2Map(mapTo, newMsg, en.getKey());
//		}
//	}
//
//	/**
//	 * add null Action to mapTo
//	 * 
//	 * @param mapFrom
//	 * @param mapTo
//	 * @throws CloneNotSupportedException
//	 */
//	private void addActionAUPs(Map<String, Set<ICCMsg>> map, Map<String, Set<ICCMsg>> mapTo)
//			throws CloneNotSupportedException {
//		for (Entry<String, Set<ICCMsg>> en : map.entrySet()) {
//			Set<ICCMsg> iccList = en.getValue();
//			if (iccList == null)
//				continue;
//			Set<ICCMsg> iccListCopy = new HashSet<ICCMsg>(iccList);
//			for (ICCMsg icc : iccListCopy) {
//				ICCMsg newMsg;
//				newMsg = icc.clone();
//				if (newMsg.getAction() != null) {
//					newMsg.setAction(null);
//					CollectionUtils.addNewMsg2Map(mapTo, newMsg, en.getKey());
//				}
//			}
//		}
//	}
//
//	/**
//	 * add null Category to mapTo
//	 * 
//	 * @param mapFrom
//	 * @param mapTo
//	 * @throws CloneNotSupportedException
//	 */
//	private void addCategoryAUPs(Map<String, Set<ICCMsg>> map, Map<String, Set<ICCMsg>> mapTo)
//			throws CloneNotSupportedException {
//		for (Entry<String, Set<ICCMsg>> en : map.entrySet()) {
//			Set<ICCMsg> iccList = en.getValue();
//			if (iccList == null)
//				continue;
//			Set<ICCMsg> iccListCopy = new HashSet<ICCMsg>(iccList);
//			for (ICCMsg icc : iccListCopy) {
//				ICCMsg newMsg;
//				newMsg = icc.clone();
//				if (newMsg.getCategory() != null && newMsg.getCategory().size() != 0) {
//					newMsg.setCategory(null);
//					CollectionUtils.addNewMsg2Map(mapTo, newMsg, en.getKey());
//				}
//			}
//		}
//	}
//
//	/**
//	 * add null Data to mapTo
//	 * 
//	 * @param mapFrom
//	 * @param mapTo
//	 * @throws CloneNotSupportedException
//	 */
//	private void addDataAUPs(Map<String, Set<ICCMsg>> map, Map<String, Set<ICCMsg>> mapTo)
//			throws CloneNotSupportedException {
//		for (Entry<String, Set<ICCMsg>> en : map.entrySet()) {
//			Set<ICCMsg> iccList = en.getValue();
//			if (iccList == null)
//				continue;
//			Set<ICCMsg> iccListCopy = new HashSet<ICCMsg>(iccList);
//			for (ICCMsg icc : iccListCopy) {
//				ICCMsg newMsg = icc.clone();
//				if (newMsg.getData() != null) {
//					newMsg.setData(null);
//					CollectionUtils.addNewMsg2Map(mapTo, newMsg, en.getKey());
//				}
//			}
//		}
//	}
//
//	/**
//	 * add null Type to mapTo
//	 * 
//	 * @param mapFrom
//	 * @param mapTo
//	 * @throws CloneNotSupportedException
//	 */
//	private void addTypeAUPs(Map<String, Set<ICCMsg>> map, Map<String, Set<ICCMsg>> mapTo)
//			throws CloneNotSupportedException {
//		for (Entry<String, Set<ICCMsg>> en : map.entrySet()) {
//			Set<ICCMsg> iccList = en.getValue();
//			if (iccList == null)
//				continue;
//			Set<ICCMsg> iccListCopy = new HashSet<ICCMsg>(iccList);
//			for (ICCMsg icc : iccListCopy) {
//				ICCMsg newMsg = icc.clone();
//				if (newMsg.getType() != null) {
//					newMsg.setType(null);
//					CollectionUtils.addNewMsg2Map(mapTo, newMsg, en.getKey());
//				}
//			}
//		}
//	}
//
//	/**
//	 * add Extra AUPs
//	 * 
//	 * @param component
//	 * 
//	 * @param mapFrom
//	 * @param mapTo
//	 * @throws CloneNotSupportedException
//	 */
//	private void addExtrasAUPs(ComponentModel component, Map<String, Set<ICCMsg>> map, Map<String, Set<ICCMsg>> mapTo)
//			throws CloneNotSupportedException {
//		for (Entry<String, Set<ICCMsg>> en : map.entrySet()) {
//			Set<ICCMsg> iccList = en.getValue();
//			if (iccList == null)
//				continue;
//			Set<ICCMsg> iccListCopy = new HashSet<ICCMsg>(iccList);
//			for (ICCMsg icc : iccListCopy) {
//				if (icc.getExtra() == null)
//					continue;
//				ICCMsg nullExtraMsg = icc.clone();
//				nullExtraMsg.setExtra(null);
//				CollectionUtils.addNewMsg2Map(component.getReceiveModel().getPathMap_SNV(), nullExtraMsg, en.getKey());
//
//				HashSet<String> extraCopy = new HashSet<String>(icc.getExtra());
//				for (String extraPair : extraCopy) {
//					String ss[] = extraPair.split(","); // extra:Bundle-"b1",(,String-"s2",)
//					for (int i = 0; i < ss.length; i++) {
//						if (ss[i].equals("(") || ss[i].equals(")"))
//							continue;
//						String str = ss[i];
//						String rmStr = str;
//						if (i < ss.length - 1 && ss[i + 1].equals("(")) {
//							rmStr += "," + ss[i + 1];
//							int num = 1, j = i + 2;
//							while (num > 0 && j < ss.length) {
//								if (ss[j].equals("("))
//									num++;
//								if (ss[j].equals(")"))
//									num--;
//								rmStr += "," + ss[j];
//								j++;
//							}
//						}
//						String addExtra = getAddExtra(extraPair, rmStr);
//						ICCMsg newMsg = icc.clone();
//						newMsg.getExtra().remove(extraPair);
//						newMsg.getExtra().add(addExtra);
//						CollectionUtils.addNewMsg2Map(mapTo, newMsg, en.getKey());
//					}
//				}
//			}
//		}
//	}
//
//	/**
//	 * get Add Extra
//	 * 
//	 * @param extraPair
//	 * @param rmStr
//	 * @return
//	 */
//	private String getAddExtra(String extraPair, String rmStr) {
//		String addExtra = extraPair.replace(rmStr, "");
//		while (addExtra.contains(",,") || addExtra.contains("()")) {
//			addExtra = addExtra.replace(",,", ",");
//			addExtra = addExtra.replace("()", "");
//		}
//		if (addExtra.endsWith(","))
//			addExtra = addExtra.substring(0, addExtra.length() - 1);
//		return addExtra;
//	}

 }
