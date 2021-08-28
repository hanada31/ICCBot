package main.java.client.obj.target.ictg;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.GraphUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.BaseClient;
import main.java.client.cg.CallGraphClient;
import main.java.client.cg.DynamicReceiverCGAnalyzer;
import main.java.client.manifest.ManifestClient;
import main.java.client.obj.ObjectAnalyzer;
import main.java.client.obj.model.atg.ATGModel;
import main.java.client.obj.model.atg.AtgEdge;
import main.java.client.obj.model.atg.AtgNode;
import main.java.client.obj.model.atg.AtgType;
import main.java.client.obj.target.fragment.FragmentClient;
import main.java.client.obj.target.ictg.ICTGClient;
import main.java.client.soot.IROutputClient;
import main.java.client.statistic.model.StatisticResult;
import main.java.client.toolEvaluate.CTGEvaluateClient;

import org.dom4j.DocumentException;

import soot.SootMethod;

/**
 * Analyzer Class
 * 
 * @author yanjw
 * @version 2.0
 */
public class ICTGClient extends BaseClient {

	/**
	 * analyze logic for single app
	 */
	@Override
	protected void clientAnalyze() {
		result = new StatisticResult();
		if (!MyConfig.getInstance().isManifestAnalyzeFinish()) {
			new ManifestClient().start();
			MyConfig.getInstance().setManifestAnalyzeFinish(true);
		}
		if (!MyConfig.getInstance().isCallGraphAnalyzeFinish()) {
			new CallGraphClient().start();
			MyConfig.getInstance().setCallGraphAnalyzeFinish(true);
		}
		if (!MyConfig.getInstance().isStaitiucValueAnalyzeFinish()) {
			if (MyConfig.getInstance().getMySwithch().isStaticFieldSwitch()) {
				StaticValueAnalyzer staticValueAnalyzer = new StaticValueAnalyzer();
				staticValueAnalyzer.analyze();
				MyConfig.getInstance().setStaitiucValueAnalyzeFinish(true);
			}
		}
		if (MyConfig.getInstance().getMySwithch().isDynamicBCSwitch()) {
			DynamicReceiverCGAnalyzer dynamicIntentFilterAnalyzer = new DynamicReceiverCGAnalyzer();
			dynamicIntentFilterAnalyzer.analyze();
		}

		if (MyConfig.getInstance().getMySwithch().isFragmentSwitch()) {
			if (!MyConfig.getInstance().isFragementAnalyzeFinish()) {
				new FragmentClient().start();
				MyConfig.getInstance().setFragementAnalyzeFinish(true);
			}
		}
		for (List<SootMethod> topoQueue : Global.v().getAppModel().getTopoMethodQueueSet()) {
			ObjectAnalyzer analyzer = new ICTGAnalyzer(topoQueue, result);
			analyzer.analyze();
		}
		System.out.println("Successfully analyze with MainClient.");

	}

	@Override
	public void clientOutput() throws IOException, DocumentException {
		outputCTGInfo();
		if (!MyConfig.getInstance().isOracleConstructionClientFinish()) {
			new CTGEvaluateClient().start();
		}
	}

	private void outputCTGInfo() throws IOException, DocumentException {
		String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
				+ File.separator;
		FileUtils.createFolder(summary_app_dir + ConstantUtils.CGFOLDETR);
		FileUtils.createFolder(summary_app_dir + ConstantUtils.ICTGFOLDETR);
		FileUtils.createFolder(summary_app_dir + ConstantUtils.CGFOLDETR);

		ICTGClientOutput outer = new ICTGClientOutput(this.result);

		/** Component **/
		outer.writeComponentModel(summary_app_dir + ConstantUtils.ICTGFOLDETR, ConstantUtils.COMPONENTMODEL);

		/** Method **/
		outer.writeSingleMethodModel(summary_app_dir + ConstantUtils.ICTGFOLDETR, ConstantUtils.SINGLEMETHOD_ENTRY,
				true);
		outer.writeSingleMethodModel(summary_app_dir + ConstantUtils.ICTGFOLDETR, ConstantUtils.SINGLEMETHOD_ALL, false);

		/** Path **/
		outer.writeSinglePathModel(summary_app_dir + ConstantUtils.ICTGFOLDETR, ConstantUtils.SINGLEPATH_ENTRY, true);
		outer.writeSinglePathModel(summary_app_dir + ConstantUtils.ICTGFOLDETR, ConstantUtils.SINGLEPATH_ALL, false);

		/** Intent **/
		outer.writeSingleIntentModel(summary_app_dir + ConstantUtils.ICTGFOLDETR, ConstantUtils.SINGLEOBJECT_ENTRY,
				true);
		outer.writeSingleIntentModel(summary_app_dir + ConstantUtils.ICTGFOLDETR, ConstantUtils.SINGLEOBJECT_ALL, false);

		/** ICTG **/
		// merge frage and component
		ATGModel.mergeNodels2newOne(Global.v().getiCTGModel().getOptModel(), Global.v().getFragmentModel()
				.getAtgModel(), Global.v().getiCTGModel().getOptModel());
		ATGModel ictgMergedModel = Global.v().getiCTGModel().getOptModel();
		// ictgMergedModel
		outer.writeATGModel(summary_app_dir + ConstantUtils.ICTGFOLDETR, ConstantUtils.ICTGMERGE + ".xml",
				ictgMergedModel);
		String dotname = Global.v().getAppModel().getAppName() + "_" + ConstantUtils.ICTGMERGE;
		outer.writeDotFile(summary_app_dir + ConstantUtils.ICTGFOLDETR, dotname, ictgMergedModel, true);
		if (ictgMergedModel.getConnectionSize() < 1800)
			GraphUtils.generateDotFile(summary_app_dir + ConstantUtils.ICTGFOLDETR + dotname, "pdf");

		Global.v().getiCTGModel().setOptModelwithoutFrag(getIctgOptModel());
		ATGModel ictgOptModel = Global.v().getiCTGModel().getOptModelwithoutFrag();
		// ictgOptModel
		outer.writeATGModel(summary_app_dir + ConstantUtils.ICTGFOLDETR, ConstantUtils.ICTGOPT + ".xml", ictgOptModel);
		String txtName = Global.v().getAppModel().getAppName() + "_" + ConstantUtils.ICTGOPT + ".txt";
		outer.writeAtgModeTxtFile(summary_app_dir + ConstantUtils.ICTGFOLDETR, txtName, ictgOptModel, false);
		String dotname2 = Global.v().getAppModel().getAppName() + "_" + ConstantUtils.ICTGOPT;
		outer.writeDotFile(summary_app_dir + ConstantUtils.ICTGFOLDETR, dotname2, ictgOptModel, false);
		if (ictgMergedModel.getConnectionSize() < 1800)
			GraphUtils.generateDotFile(summary_app_dir + ConstantUtils.ICTGFOLDETR + dotname2, "pdf");

		// outer.writeIccLinksConfigFile(summary_app_dir +
		// ConstantUtils.ICTGFOLDETR, ConstantUtils.LINKFILE, ictgOptModel);
	}

	private ATGModel getIctgOptModel() {
		ATGModel ictgOptModel = new ATGModel();
		ATGModel mergedIctgModel = Global.v().getiCTGModel().getOptModel();
		Map<String, Set<String>> desfrag2StratcomMap = new HashMap<String, Set<String>>();
		// System.out.println(PrintUtils.printMap(mergedIctgModel.getAtgEdges()));
		// handle comp2fragment and frag2fragment --> comp2fragment
		for (Entry<String, Set<AtgEdge>> entry : mergedIctgModel.getAtgEdges().entrySet()) {
			for (AtgEdge edge : entry.getValue()) {
				if (edge.getType().equals(AtgType.Act2Frag) || edge.getType().equals(AtgType.NonAct2Frag)) {
					String souComp = edge.getSource().getName();
					String desFrag = edge.getDestnation().getName();
					if (!desfrag2StratcomMap.containsKey(desFrag))
						desfrag2StratcomMap.put(desFrag, new HashSet<String>());
					desfrag2StratcomMap.get(desFrag).add(souComp);
				}
			}
		}
		while (true) { // fix point
			boolean changed = false;
			for (Entry<String, Set<AtgEdge>> entry : mergedIctgModel.getAtgEdges().entrySet()) {
				for (AtgEdge edge : entry.getValue()) {
					if (edge.getType().equals(AtgType.Frag2Frag)) {
						String souFrag = edge.getSource().getName();
						String desFrag = edge.getDestnation().getName();
						if (desfrag2StratcomMap.containsKey(souFrag)) {
							if (!desfrag2StratcomMap.containsKey(desFrag)) {
								desfrag2StratcomMap.put(desFrag, new HashSet<String>());
							}
							for (String com : desfrag2StratcomMap.get(souFrag)) {
								if (!desfrag2StratcomMap.get(desFrag).contains(com)) {
									desfrag2StratcomMap.get(desFrag).add(com);
									changed = true;
								}
							}
						}
					}
				}
			}
			if (changed == false)
				break;
		}

		for (Entry<String, Set<AtgEdge>> entry : mergedIctgModel.getAtgEdges().entrySet()) {
			for (AtgEdge edge : entry.getValue()) {
				// if(edge.getSource().getClassName().contains("ItemlistFragment"))
				// System.out.println(edge);
				switch (edge.getType()) {
				case Act2Act:
					ictgOptModel.addAtgEdges(entry.getKey(), edge);
					break;
				case NonAct2Act:
					ictgOptModel.addAtgEdges(entry.getKey(), edge);
					break;
				case Act2NonAct:
					ictgOptModel.addAtgEdges(entry.getKey(), edge);
					break;
				case NonAct2NonAct:
					ictgOptModel.addAtgEdges(entry.getKey(), edge);
					break;
				case Frag2Act:
					if (desfrag2StratcomMap.containsKey(edge.getSource().getClassName())) {
						for (String startCom : desfrag2StratcomMap.get(edge.getSource().getClassName())) {
							AtgEdge edgeCopy = new AtgEdge(edge);
							edgeCopy.setSource(new AtgNode(startCom));
							ictgOptModel.addAtgEdges(startCom, edgeCopy);
						}
					}
					break;
				case Frag2NonAct:
					if (desfrag2StratcomMap.containsKey(edge.getSource().getClassName())) {
						for (String startCom : desfrag2StratcomMap.get(edge.getSource().getClassName())) {
							AtgEdge edgeCopy = new AtgEdge(edge);
							edgeCopy.setSource(new AtgNode(startCom));
							ictgOptModel.addAtgEdges(startCom, edgeCopy);
						}
					}
					break;
				default:
					break;
				}
			}
		}

		return ictgOptModel;
	}
}