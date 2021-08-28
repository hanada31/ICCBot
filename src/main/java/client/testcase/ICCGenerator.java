//package main.java.client.testcase;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.Map.Entry;
//
//import main.java.Analyzer;
//import main.java.analyze.model.analyzeModel.Attribute;
//import main.java.analyze.utils.CollectionUtils;
//import main.java.analyze.utils.ConstraintSolver;
//import main.java.analyze.utils.output.PrintUtils;
//import main.java.client.obj.model.component.ComponentModel;
//import main.java.client.obj.model.ictg.ICCMsg;
//
//public class ICCGenerator extends Analyzer {
//
//	public ICCGenerator() {
//		super();
//	}
//
//	@Override
//	public void analyze() {
//		System.out.println("Start ICC Generator...\n");
//
//		try {
//			generateAUPs2Map();
//			generateSingleNullAUPs2Map();
//
//		} catch (CloneNotSupportedException e) {
//			e.printStackTrace();
//		}
//
//		// printPathMapAU();
//		// printPathMapSNV();
//
//		System.out.println("End ICC Generator...\n");
//	}
//
//	/**
//	 * generate ICCs with all used ICC attributes from globalPathMap to
//	 * appModel.pathMap_AU
//	 * 
//	 * @throws CloneNotSupportedException
//	 */
//	private void generateAUPs2Map() throws CloneNotSupportedException {
//		for (ComponentModel component : appModel.getComponentMap().values()) {
//			for (Entry<String, Set<List<Attribute>>> entry : component.getReceiveModel().getGlobalPathMap().entrySet()) {
//				String clsName = entry.getKey();
//				System.out.println(clsName + " " + entry.getValue().size());
//				for (List<Attribute> globalPath : entry.getValue()) {
//					Set<String> modelStrs = ConstraintSolver.eliminateConfictedPaths(globalPath);
//					for (String modelStr : modelStrs) {
//						if (!modelStr.equals("unsat!")) {
//							ICCMsg icc = ICCMsg.getIccFromModelStr(modelStr, globalPath);
//							if (icc.toString().equals(""))
//								continue;
//							icc.setSource("unknown");
//							icc.setDestination(clsName);
//							component.getReceiveModel().getPathMap().get(clsName).add(icc);
//						}
//					}
//				}
//			}
//			Set<ICCMsg> iccs = new HashSet<>(component.getReceiveModel().getPathMap().get(component));
//			component.getReceiveModel().getPathMap_AU().put(component.getComponetName(), iccs);
//		}
//	}
//
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
//
//	/**
//	 * print PathMap all used
//	 */
//	public void printPathMapAU() {
//		for (ComponentModel component : appModel.getComponentMap().values()) {
//			PrintUtils.printInfo("appModel.pathMap_AU");
//			PrintUtils.printInfo(PrintUtils.printMap(component.getReceiveModel().getPathMap_AU()) + "\n");
//		}
//	}
//
//	/**
//	 * print PathMap single null value
//	 */
//	public void printPathMapSNV() {
//		for (ComponentModel component : appModel.getComponentMap().values()) {
//			PrintUtils.printInfo("appModel.pathMap_SNV");
//			PrintUtils.printInfo(PrintUtils.printMap(component.getReceiveModel().getPathMap_SNV()) + "\n");
//		}
//	}
//}
