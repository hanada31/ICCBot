package main.java.client.toolEvaluate;

import java.io.File;
import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.model.labeledOracleModel.LabeledOracleReader;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.GraphUtils;
import main.java.analyze.utils.OracleUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.BaseClient;
import main.java.client.obj.model.atg.ATGModel;
import main.java.client.obj.target.ctg.CTGReaderClient;
import main.java.client.related.a3e.A3EResultEvaluateClient;
import main.java.client.related.gator.GatorATGResultEvaluateClient;
import main.java.client.related.ic3.IC3ResultEvaluateClient;
import main.java.client.related.ic3dial.IC3DIALDroidResultEvaluateClient;
import main.java.client.related.story.StoryResultEvaluateClient;

/**
 * Analyzer Class
 * 
 * @author hanada
 * @version 2.0
 */
public class ToolEvaluateClient extends BaseClient {

	@Override
	protected void clientAnalyze() {
		new CTGReaderClient().start();
		
		new IC3ResultEvaluateClient().start();

		new IC3DIALDroidResultEvaluateClient().start();

		new GatorATGResultEvaluateClient().start();

		new A3EResultEvaluateClient().start();
		
		new StoryResultEvaluateClient().start();
		

//		dynamicResultAnalyzer analyzer = new dynamicResultAnalyzer();
//		analyzer.analyze();
//
//		manualResultAnalyzer analyzer2 = new manualResultAnalyzer();
//		analyzer2.analyze();

		LabeledOracleReader reader = new LabeledOracleReader();
		reader.analyze();

		System.out.println("Successfully analyze with ToolEvaluateClient.");

	}

	@Override
	public void clientOutput() {
		String appName = Global.v().getAppModel().getAppName();
		String summary_app_dir = MyConfig.getInstance().getResultFolder() + appName + File.separator;
		FileUtils.createFolder(summary_app_dir + ConstantUtils.ORACLEFOLDETR);
		StringBuilder sb = new StringBuilder(MyConfig.getInstance().getMySwithch().toString());

//		dynamicModelContruction(sb, appName, summary_app_dir);
//		manualModelContruction(sb, appName, summary_app_dir);
//		oracleModelContruction(sb, appName, summary_app_dir);
		
		oracleEvaluate(sb);
		ICCBotEvaluate(sb);
		IC3Evaluate(sb);
		IC3DialEvaluate(sb);
		GatorEvaluate(sb);
		A3EEvaluate(sb); 
		StoryEvaluate(sb);

//		FilterAndEnhanceEvaluate(sb);

		outputNumberCount();
		outputGraphCount();
		outputOracleEvaluate();

		String content = sb.toString();
		FileUtils.writeText2File(summary_app_dir + ConstantUtils.ORACLEFOLDETR + appName + ConstantUtils.SCORERECORD,
				content, true);
	}


	private void FilterAndEnhanceEvaluate(StringBuilder sb) {
		ATGModel manualModel = Global.v().getiCTGModel().getMannualModel();
		String content = Global.v().getAppModel().getAppName() + "\t";
		content += "filter\t" + manualModel.getFilteredNum() + "\t";
		content += "add\t" + manualModel.getEnhancedNum() + "\t";
		content += "filteredService\t" + manualModel.getFilteredServiceNum() + "\t";
		FileUtils.writeText2File(MyConfig.getInstance().getResultWarpperFolder() + File.separator + "edgeResult.txt", content, true);
		System.out.print(manualModel.getFilteredNum() + "\t");
		System.out.print(manualModel.getEnhancedNum() + "\t");

	}

	private void outputNumberCount() {
		ATGModel iccBotModelnoFrag = Global.v().getiCTGModel().getOptModelwithoutFrag();
		ATGModel iccBotModel = Global.v().getiCTGModel().getOptModel();
		ATGModel ic3Model = Global.v().getiC3Model().getIC3AtgModel();
		ATGModel IC3DialModel = Global.v().getiC3DialDroidModel().getIC3AtgModel();
		ATGModel gatorModel = Global.v().getGatorModel().getGatorAtgModel();
		ATGModel a3eModel = Global.v().getA3eModel().geta3eAtgModel();
		ATGModel storyModelnoFrag = Global.v().getStoryModel().getStoryAtgModelWithoutFrag();
		ATGModel storyModel = Global.v().getStoryModel().getStoryAtgModel();

		StringBuilder sb = new StringBuilder();
		
		sb.append(Global.v().getAppModel().getComponentMap().size() + "\t");
		
		sb.append(gatorModel.getComp2CompSize() + "\t");
		sb.append(ic3Model.getComp2CompSize() + "\t");
		sb.append(IC3DialModel.getComp2CompSize() + "\t");
		sb.append(a3eModel.getComp2CompSize() + "\t");
		sb.append(storyModelnoFrag.getComp2CompSize() + "\t");
		sb.append(iccBotModelnoFrag.getComp2CompSize() + "\t");
		
		sb.append(gatorModel.getAct2ActSize() + "\t");
		sb.append(ic3Model.getAct2ActSize() + "\t");
		sb.append(IC3DialModel.getAct2ActSize() + "\t");
		sb.append(a3eModel.getAct2ActSize() + "\t");
		sb.append(storyModelnoFrag.getAct2ActSize() + "\t");
		sb.append(iccBotModelnoFrag.getAct2ActSize() + "\t");
		
		sb.append(gatorModel.getComp2CompSize() + "\t");
		sb.append(ic3Model.getComp2CompSize() + "\t");
		sb.append(IC3DialModel.getComp2CompSize() + "\t");
		sb.append(a3eModel.getComp2CompSize() + "\t");
		sb.append(storyModel.getComp2CompSize() + "\t");
		sb.append(iccBotModel.getComp2CompSize() + "\t");
		
		System.out.println(sb.toString());
		
		String filename = MyConfig.getInstance().getResultWarpperFolder() + File.separator + "numberCount.txt";
		File f = new File(filename);
		if(!f.exists()){
			FileUtils.writeText2File(filename,  "\t\t"  +  "C2C\t\t\t\t\t\t" + "A2A\t\t\t\t\t\t" + "AF2AF\t\t\t\t\t\t"+ "\n", true);
			FileUtils.writeText2File(filename, "\tComponent\t"  
					+  "Gator\tIC3\tIC3-Dial\tA3E\tStory\tICCBot\t" + "Gator\tIC3\tIC3-Dial\tA3E\tStory\tICCBot\t" 
					+"Gator\tIC3\tIC3-Dial\tA3E\tStory\tICCBot\t"+ "\n", true);
		}
		FileUtils.writeText2File(filename, Global.v().getAppModel().getAppName()
				+ "\t" + sb.toString() + "\n", true);
	}

	private void outputGraphCount() {
		ATGModel dynamicModel = Global.v().getiCTGModel().getDynamicModel();
		ATGModel manualModel = Global.v().getiCTGModel().getMannualModel();
		ATGModel oracleModel = Global.v().getiCTGModel().getOracleModel();

		ATGModel iccBotModelnoFrag = Global.v().getiCTGModel().getOptModelwithoutFrag();
		ATGModel ic3Model = Global.v().getiC3Model().getIC3AtgModel();
		ATGModel IC3DialModel = Global.v().getiC3DialDroidModel().getIC3AtgModel();
		ATGModel gatorModel = Global.v().getGatorModel().getGatorAtgModel();
		ATGModel a3eModel = Global.v().getA3eModel().geta3eAtgModel();
		ATGModel storyModelnoFrag = Global.v().getStoryModel().getStoryAtgModelWithoutFrag();

		StringBuilder sb = new StringBuilder();

//		sb.append(String.format("%.2f", dynamicModel.getCompletenessScore()) + "\t");
//		sb.append(String.format("%.2f", manualModel.getCompletenessScore()) + "\t");
//		sb.append(String.format("%.2f", oracleModel.getCompletenessScore()) + "\t");
//
//		sb.append(String.format("%.2f", dynamicModel.getConnectionScore()) + "\t");
//		sb.append(String.format("%.2f", manualModel.getConnectionScore()) + "\t");
//		sb.append(String.format("%.2f", oracleModel.getConnectionScore()) + "\t");

		
		sb.append(String.format("%.2f", gatorModel.getCompletenessScore()) + "\t");
		sb.append(String.format("%.2f", ic3Model.getCompletenessScore()) + "\t");
		sb.append(String.format("%.2f", IC3DialModel.getCompletenessScore()) + "\t");
		sb.append(String.format("%.2f", a3eModel.getCompletenessScore()) + "\t");
		sb.append(String.format("%.2f", storyModelnoFrag.getCompletenessScore()) + "\t");
		sb.append(String.format("%.2f", iccBotModelnoFrag.getCompletenessScore()) + "\t");
		
		
		sb.append(String.format("%.2f", gatorModel.getConnectionScore()) + "\t");
		sb.append(String.format("%.2f", ic3Model.getConnectionScore()) + "\t");
		sb.append(String.format("%.2f", IC3DialModel.getConnectionScore()) + "\t");
		sb.append(String.format("%.2f", a3eModel.getConnectionScore()) + "\t");
		sb.append(String.format("%.2f", storyModelnoFrag.getConnectionScore()) + "\t");
		sb.append(String.format("%.2f", iccBotModelnoFrag.getConnectionScore()) + "\t");
		
		System.out.println(sb.toString());
		String filename = MyConfig.getInstance().getResultWarpperFolder() + File.separator + "graphCount.txt";
		File f = new File(filename);
		if(!f.exists()){
			FileUtils.writeText2File(filename,  "\t" +  "Completeness\t\t\t\t\t\t" + "Connection\t\t\t\t\t\t" + "\n", true);
			FileUtils.writeText2File(filename,  "\t" +  "Gator\tIC3\tIC3-Dial\tA3E\tStory\tICCBot\t" + "Gator\tIC3\tIC3-Dial\tA3E\tStory\tICCBot\t" + "\n", true);
		}
		
		FileUtils.writeText2File(filename, Global.v().getAppModel().getAppName()
				+ "\t" + sb.toString() + "\n", true);
	}

	private void outputOracleEvaluate() {
		ATGModel iccBotModelnoFrag = Global.v().getiCTGModel().getOptModelwithoutFrag();
		ATGModel ic3Model = Global.v().getiC3Model().getIC3AtgModel();
		ATGModel IC3DialModel = Global.v().getiC3DialDroidModel().getIC3AtgModel();
		ATGModel gatorModel = Global.v().getGatorModel().getGatorAtgModel();
		ATGModel a3eModel = Global.v().getA3eModel().geta3eAtgModel();
		ATGModel storyModelnoFrag = Global.v().getStoryModel().getStoryAtgModelWithoutFrag();

		StringBuilder sb = new StringBuilder();

		//oracle
		sb.append(iccBotModelnoFrag.getOracleEdgeSize() + "\t");

		sb.append(gatorModel.getFnEdgeSize() + "\t");
		sb.append(ic3Model.getFnEdgeSize() + "\t");
		sb.append(IC3DialModel.getFnEdgeSize() + "\t");
		sb.append(a3eModel.getFnEdgeSize() + "\t");
		sb.append(storyModelnoFrag.getFnEdgeSize() + "\t");
		sb.append(iccBotModelnoFrag.getFnEdgeSize() + "\t");
		
		
		sb.append(String.format("%.2f", gatorModel.getFalsenegativeScore())+ "\t");
		sb.append(String.format("%.2f", ic3Model.getFalsenegativeScore()) + "\t");
		sb.append(String.format("%.2f", IC3DialModel.getFalsenegativeScore()) + "\t");
		sb.append(String.format("%.2f", a3eModel.getFalsenegativeScore())+ "\t");
		sb.append(String.format("%.2f", storyModelnoFrag.getFalsenegativeScore()));
		sb.append(String.format("%.2f", iccBotModelnoFrag.getFalsenegativeScore()) + "\t");
		
		System.out.println(sb.toString());
		
		String filename = MyConfig.getInstance().getResultWarpperFolder() + File.separator + "oracleCount.txt";
		File f = new File(filename);
		if(!f.exists()){
			FileUtils.writeText2File(filename,  "\t"  + "Oracle\t" + "FN\t\t\t\t\t\t" + "FNRate\t\t\t\t\t\t" + "\n", true);
			FileUtils.writeText2File(filename,  "\t"  + "Oracle\t" + "Gator\tIC3\tIC3-Dial\tA3E\tStory\tICCBot\t" + "Gator\tIC3\tIC3-Dial\tA3E\tStory\tICCBot\t" + "\n", true);
		}
		FileUtils.writeText2File(filename, Global.v().getAppModel().getAppName()
				+ "\t" + sb.toString() + "\n", true);
	}
	/**
	 * construct model use dynamic log information
	 * 
	 * @param sb
	 * @param appName
	 * @param summary_app_dir
	 */
	private void dynamicModelContruction(StringBuilder sb, String appName, String summary_app_dir) {
		System.out.println();
		ATGModel dynamicModel = Global.v().getiCTGModel().getDynamicModel();

		ToolEvaluateClientOutput outer = new ToolEvaluateClientOutput();
		String dotname = appName + "_" + ConstantUtils.ICTGDYNAMIC;
		outer.writeDotFile(summary_app_dir + ConstantUtils.ORACLEFOLDETR, dotname, dynamicModel, false);
		GraphUtils.generateDotFile(summary_app_dir + ConstantUtils.ORACLEFOLDETR + dotname, "pdf");
		initStringBuilderComplete("dynamic", sb);
		dynamicModel.evaluateCompleteness("dynamic oracle", sb);

	}

	/**
	 * construct model use manual information
	 * 
	 * @param sb
	 * @param appName
	 * @param summary_app_dir
	 */
	private void manualModelContruction(StringBuilder sb, String appName, String summary_app_dir) {
		System.out.println();
		ATGModel manualModel = Global.v().getiCTGModel().getMannualModel();

		ToolEvaluateClientOutput outer = new ToolEvaluateClientOutput();
		String dotname2 = appName + "_" + ConstantUtils.ICTGDMANUAL;
		outer.writeDotFile(summary_app_dir + ConstantUtils.ORACLEFOLDETR, dotname2, manualModel, false);
		GraphUtils.generateDotFile(summary_app_dir + ConstantUtils.ORACLEFOLDETR + dotname2, "pdf");
		initStringBuilderComplete("manual", sb);
		manualModel.evaluateCompleteness("manual oracle", sb);
	}

	/**
	 * merge dynamic and manual oracle to oracleModel
	 * 
	 * @param sb
	 * @param appName
	 * @param summary_app_dir
	 */
	private void oracleModelContruction(StringBuilder sb, String appName, String summary_app_dir) {
		System.out.println();
		ATGModel dynamicModel = Global.v().getiCTGModel().getDynamicModel();
		ATGModel manualModel = Global.v().getiCTGModel().getMannualModel();
		ATGModel oracleModel = Global.v().getiCTGModel().getOracleModel();

		ToolEvaluateClientOutput outer = new ToolEvaluateClientOutput();
		ATGModel.mergeNodels2newOne(dynamicModel, manualModel, oracleModel);
		String dotname3 = appName + "_" + ConstantUtils.ICTGORACLE;
		outer.writeDotFile(summary_app_dir + ConstantUtils.ORACLEFOLDETR, dotname3, oracleModel, false);
		GraphUtils.generateDotFile(summary_app_dir + ConstantUtils.ORACLEFOLDETR + dotname3, "pdf");
		initStringBuilderComplete("oracle", sb);
		OracleUtils.writeTagedOracleFile(summary_app_dir, appName);
		oracleModel.evaluateCompleteness("whole oracle", sb);
	}

	private void oracleEvaluate(StringBuilder sb) {
		System.out.println();
//		ATGModel dynamicModel = Global.v().getiCTGModel().getDynamicModel();
//		ATGModel manualModel = Global.v().getiCTGModel().getMannualModel();
		ATGModel oracleModel = Global.v().getiCTGModel().getOracleModel();

//		initStringBuilderConnection("oracleModel", sb);
//		dynamicModel.evaluateConnectivity("dynamicModel", sb);
//		manualModel.evaluateConnectivity("manualModel", sb);
//		oracleModel.evaluateConnectivity("oracleModel", sb);
		oracleModel.countTagForOracle();
		System.out.println();

	}

	/**
	 * Evaluate the completeness/connection rate/false nagetive of ICCBot
	 * 
	 * @param sb
	 */
	private void ICCBotEvaluate(StringBuilder sb) {
		System.out.println();
		ATGModel oracleModel = Global.v().getiCTGModel().getOracleModel();
		ATGModel iccBotModelnoFrag = Global.v().getiCTGModel().getOptModelwithoutFrag();
		if(iccBotModelnoFrag.isExist()){
			initStringBuilderComplete("ICCBot      ", sb);
			iccBotModelnoFrag.evaluateCompleteness("ICCBot      ", sb);
	
			initStringBuilderConnection("ICCBot      ", sb);
			iccBotModelnoFrag.evaluateConnectivity("ICCBot      ", sb);
	
			if (oracleModel.getAtgEdges().size() > 0) {
				initStringBuilderFN("ICCBot      ", sb);
				iccBotModelnoFrag.evaluateFalseNegative("ICCBot      ", oracleModel, sb);
			}else{
				String hint = "The results of labeled oracle doesn't exist, for false negative evaluation, please add files into folder /labeledOracle\n";
				sb.append(hint);
				System.out.println(hint);
			}
			System.out.println();
		}else{
			String hint = "The results for tool IC3 doesn't exist, for false negative evaluation, please run the MainClient first.\n";
			sb.append(hint);
			System.out.println(hint);
		}
	}

	/**
	 * Evaluate the completeness/connection rate/false nagetive of IC3
	 * 
	 * @param sb
	 */
	private void IC3Evaluate(StringBuilder sb) {
		ATGModel oracleModel = Global.v().getiCTGModel().getOracleModel();
		ATGModel ic3Model = Global.v().getiC3Model().getIC3AtgModel();
		if(ic3Model.isExist()){
			initStringBuilderComplete("IC3         ", sb);
			ic3Model.evaluateCompleteness("IC3         ", sb);
	
			initStringBuilderConnection("IC3         ", sb);
			ic3Model.evaluateConnectivity("IC3         ", sb);
	
			if (oracleModel.getAtgEdges().size() > 0) {
				initStringBuilderFN("IC3         ", sb);
				ic3Model.evaluateFalseNegative("IC3         ", oracleModel, sb);
			}else{
				String hint = "The results of labeled oracle doesn't exist, for false negative evaluation, please add files into folder /labeledOracle\n";
				sb.append(hint);
				System.out.println(hint);
			}
			System.out.println();
		}else{
			String hint = "The results for tool IC3 doesn't exist, please add files into folder /relatedTools/IC3\n";
			sb.append(hint);
			System.out.println(hint);
		}
	}

	/**
	 * Evaluate the completeness/connection rate/false nagetive of IC3-dialdroid
	 * 
	 * @param sb
	 */
	private void IC3DialEvaluate(StringBuilder sb) {
		ATGModel oracleModel = Global.v().getiCTGModel().getOracleModel();
		ATGModel ic3Model = Global.v().getiC3DialDroidModel().getIC3AtgModel();
		if(ic3Model.isExist()){
			initStringBuilderComplete("IC3Dial     ", sb);
			ic3Model.evaluateCompleteness("IC3Dial     ", sb);
	
			initStringBuilderConnection("IC3Dial     ", sb);
			ic3Model.evaluateConnectivity("IC3Dial     ", sb);
	
			if (oracleModel.getAtgEdges().size() > 0) {
				initStringBuilderFN("IC3Dial     ", sb);
				ic3Model.evaluateFalseNegative("IC3Dial     ", oracleModel, sb);
			}else{
				String hint = "The results of labeled oracle doesn't exist, for false negative evaluation, please add files into folder /labeledOracle\n";
				sb.append(hint);
				System.out.println(hint);
			}
			System.out.println();
		}else{
			String hint = "The results for tool IC3-DIALDroid doesn't exist, please add files into folder /relatedTools/IC3-Dial\n";
			sb.append(hint);
			System.out.println(hint);
		}
	}

	/**
	 * Evaluate the completeness/connection rate/false nagetive of Gator
	 * 
	 * @param sb
	 */
	private void GatorEvaluate(StringBuilder sb) {
		ATGModel oracleModel = Global.v().getiCTGModel().getOracleModel();
		ATGModel GatorModel = Global.v().getGatorModel().getGatorAtgModel();
		if(GatorModel.isExist()){
			initStringBuilderComplete("Gator         ", sb);
			GatorModel.evaluateCompleteness("Gator         ", sb);
	
			initStringBuilderConnection("Gator         ", sb);
			GatorModel.evaluateConnectivity("Gator         ", sb);
	
			if (oracleModel.getAtgEdges().size() > 0) {
				initStringBuilderFN("Gator         ", sb);
				GatorModel.evaluateFalseNegative("Gator         ", oracleModel, sb);
			}else{
				String hint = "The results of labeled oracle doesn't exist, please add files into folder /labeledOracle\n";
				sb.append(hint);
				System.out.println(hint);
			}
			System.out.println();
		}else{
			String hint = "The results for tool Gator doesn't exist, please add files into folder /relatedTools/Gator\n";
			sb.append(hint);
			System.out.println(hint);
		}
	}
	

	private void A3EEvaluate(StringBuilder sb) {
		ATGModel oracleModel = Global.v().getiCTGModel().getOracleModel();
		ATGModel a3eModel = Global.v().getA3eModel().geta3eAtgModel();
		if(a3eModel.isExist()){
			initStringBuilderComplete("A3E         ", sb);
			a3eModel.evaluateCompleteness("A3E         ", sb);
	
			initStringBuilderConnection("A3E         ", sb);
			a3eModel.evaluateConnectivity("A3E         ", sb);
	
			if (oracleModel.getAtgEdges().size() > 0) {
				initStringBuilderFN("A3E         ", sb);
				a3eModel.evaluateFalseNegative("A3E         ", oracleModel, sb);
			}else{
				String hint = "The results of labeled oracle doesn't exist, please add files into folder /labeledOracle\n";
				sb.append(hint);
				System.out.println(hint);
			}
			System.out.println();
		}else{
			String hint = "The results for tool A3E doesn't exist, please add files into folder /relatedTools/A3E\n";
			sb.append(hint);
			System.out.println(hint);
		}
		
	}
	
	private void StoryEvaluate(StringBuilder sb) {
		ATGModel oracleModel = Global.v().getiCTGModel().getOracleModel();
		ATGModel storyModelnoFrag = Global.v().getStoryModel().getStoryAtgModelWithoutFrag();
		if(storyModelnoFrag.isExist()){
			initStringBuilderComplete("Story         ", sb);
			storyModelnoFrag.evaluateCompleteness("Story         ", sb);
	
			initStringBuilderConnection("Story         ", sb);
			storyModelnoFrag.evaluateConnectivity("Story         ", sb);
	
			if (oracleModel.getAtgEdges().size() > 0) {
				initStringBuilderFN("Story         ", sb);
				storyModelnoFrag.evaluateFalseNegative("Story         ", oracleModel, sb);
			}else{
				String hint = "The results of labeled oracle doesn't exist, please add files into folder /labeledOracle\n";
				sb.append(hint);
				System.out.println(hint);
			}
			System.out.println();
		}else{
			String hint = "The results for tool Story doesn't exist, please add files into folder /relatedTools/StoryDistiller\n";
			sb.append(hint);
			System.out.println(hint);
		}
		
	}
	

	/**
	 * inital sb for output
	 * 
	 * @param start
	 * @param sb
	 */
	private void initStringBuilderComplete(String start, StringBuilder sb) {
		sb.append("\n" + start + " completeness: \n");
		sb.append("allNode\t");
		sb.append("mainReachable\t");
		sb.append("mainScore\t");
		sb.append("exportReachable\t");
		sb.append("expoortScore\t");
		sb.append("nonSeperateNode\t");
		sb.append("nonSeperateNodeScore\t");
		sb.append("completenessScore\n");
	}

	/**
	 * inital sb for output
	 * 
	 * @param start
	 * @param sb
	 */
	private void initStringBuilderConnection(String start, StringBuilder sb) {
		sb.append("\n" + start + " connectivity: \n");
		sb.append("actualEdges\t");
		sb.append("maxEdges\t");
		sb.append("connectivityScore\n");
	}

	/**
	 * inital sb for output
	 * 
	 * @param start
	 * @param sb
	 */
	private void initStringBuilderFN(String start, StringBuilder sb) {
		sb.append("\n" + start + " false negative: \n");
		sb.append("falseNegativeNum\t");
		sb.append("oracleEdgeNum\t");
		sb.append("FalseNegativeScore\n");
	}

	

}