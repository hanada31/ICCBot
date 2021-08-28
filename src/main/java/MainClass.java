package main.java;

import java.io.File;

import main.java.analyze.utils.TimeUtilsofProject;
import main.java.client.BaseClient;
import main.java.client.cg.CallGraphClient;
import main.java.client.instrument.InstrumentClient;
import main.java.client.manifest.ManifestClient;
import main.java.client.obj.target.fragment.FragmentClient;
import main.java.client.obj.target.ictg.ICTGClient;
import main.java.client.related.ic3.IC3ReaderClient;
import main.java.client.related.wtg.WTGReadderClient;
import main.java.client.soot.IROutputClient;
import main.java.client.statistic.StatisticClient;
import main.java.client.toolEvaluate.CTGEvaluateClient;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
import polyglot.main.Main;

 * Main Class of ICC Extractor
 * 
 * @author yanjw
 * @version 2.0 -version 28 -path ..\apk\ -name IntentFlowBench.apk -outputDir
 *          Result_testGen -client xxxClient
 */

public class MainClass {
	private static CommandLine mCmd = null;

	public static void main(String[] args) {
		mCmd = getCmd(args);
		analyzeArgs();
		if (mCmd.hasOption("debug")) {
			testConfig();
			setMySwitch();
		}
		startAnalyze();
		System.out.println("ICC Extractor Finish...\n");
		System.exit(0);
	}

	private static CommandLine getCmd(String[] mCmdArgs) {
		CommandLineParser parser = new DefaultParser();
		try {
			return parser.parse(getOptions(), mCmdArgs);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * construct the structure of options
	 * 
	 * @return
	 */
	private static Options getOptions() {
		Options options = new Options();

		options.addOption("h", false, "-h: Show the help information.");

		/** input **/
		options.addOption("name", true,
				"-name: Set the name of the apk under analysis.");
		options.addOption("path", true,
				"-path: Set the path to the apk under analysis.");
		options.addOption("androidJar", true,
				"-androidJar: Set the path of android.jar.");
		options.addOption("version", true,
				"-version [default:23]: Version of Android SDK.");

		/** analysis config **/
		options.addOption("client", true,
				"-client [default:ICTGClient]: Set the analyze client.\n"
						+ "IROutputClient: Output soot IR files.\n"
						+ "ManifestClient: Output manifest.xml file.\n"
						+ "CallGraphClient: Output call graph files.\n"
						+ "ICTGClient: Analyze Intents.\n");

		options.addOption("time", true,
				"-time [default:90]: Set the max running time (min).");
		options.addOption("maxPathNumber", true,
				"-maxPathNumber [default:10000]: Set the max number of paths.");

		/** output **/
		options.addOption("outputDir", true,
				"-outputDir: Set the output folder of the apk.");

		/** debug **/
		options.addOption("debug", false, "-debug: use debug mode.");

		/** Switch **/

		options.addOption("onlyDummyMain", false,
				"-onlyDummyMain: limit the entry scope");
		options.addOption("noCallBackEntry", false,
				"-noCallBackEntry: exclude the call back methods");
		options.addOption("noFunctionExpand", false,
				"-noFunctionExpand: do not inline function with useful contexts");
		options.addOption("noAsyncMethod", false,
				"-noAsyncMethod: exclude async method call edge");
		options.addOption("noPolym", false,
				"-noPolym: exclude polymorphism methods");
		options.addOption("noAdapter", false,
				"-noAdapter: exclude super simple adapter model");
		options.addOption("noStringOp", false,
				"-noStringOp: exclude string operation model");
		options.addOption("noStaticField", false,
				"-noStaticField: exclude string operation model");
		options.addOption("noFragment", false,
				"-noFragment: exclude fragment operation model");
		options.addOption("noLibCode", false,
				"-noLibCode: exclude the activities not declared in app's package");
		options.addOption("noWrapperAPI", false,
				"-noWrapperAPI: exclude RAICC model");
		options.addOption("noImplicit", false,
				"-noImplicit: exclude implict matching");
		options.addOption("noDynamicBC", false,
				"-noDynamicBC: exclude dynamic broadcast receiver matching");
//
//		/** Strategy **/
//		options.addOption("summaryStrategy", true,
//				"-summaryStrategy: choose the type of summary model from object/path/none");
//		options.addOption("noVfgStrategy", false,
//				"-vfgStrategy: do not use vfg model");
//		options.addOption("cgAnalyzeGroup", false,
//				"-cgAnalyzeGroup: group cg edges into several groups");
//		options.addOption("getAttributeStrategy", false,
//				"-getAttributeStrategy: include the analyze of intent data receiveing.");
//
//		options.addOption("scenarioStack", false,
//				"-scenarioStack: for stack related bug analysis.");

		return options;
	}

	/**
	 * analyze args and store information to MyConfig
	 * 
	 */
	private static void analyzeArgs() {
		if (null == mCmd)
			System.exit(-1);

		if (mCmd.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar ICCBot.jar [options] [-path] [-name] [-outputDir] [-client]",
					getOptions());
			System.out
					.println("E.g., -path apk\\ -name test.apk -outputDir result -client MainClient");
			System.exit(0);
		}

		/** run config **/
		MyConfig.getInstance().setJimple(true);
		MyConfig.getInstance().setAppName(mCmd.getOptionValue("name", ""));
		MyConfig.getInstance().setAppPath(mCmd.getOptionValue("path", System.getProperty("user.dir")) + File.separator);
		MyConfig.getInstance().setAndroidJar(mCmd.getOptionValue("androidJar", "lib") + File.separator);
		MyConfig.getInstance().setAndroidVersion("android-" + mCmd.getOptionValue("version", "23"));

		int timeLimit = Integer.valueOf(mCmd.getOptionValue("time", "90"));
		MyConfig.getInstance().setTimeLimit(timeLimit);
		MyConfig.getInstance().setMaxPathNumber(Integer.valueOf(mCmd.getOptionValue("maxPathNumber", "1000")));

		String client = mCmd.getOptionValue("client", "MainClient");
		MyConfig.getInstance().setClient(mCmd.getOptionValue("client", client));

		MyConfig.getInstance().setResultFolder(mCmd.getOptionValue("outputDir", "outputDir") + File.separator);

		if (!mCmd.hasOption("debug") && !mCmd.hasOption("name")) {
			printHelp("Please input the apk name use -name.");
		}
		if (!mCmd.hasOption("debug") && !mCmd.hasOption("androidJar")) {
			printHelp("Please input the path of android.jar use -androidJar.");
		}

		/** analysis config **/
		if (mCmd.hasOption("onlyDummyMain"))
			MyConfig.getInstance().getMySwithch().setDummyMainSwitch(true);
		if (mCmd.hasOption("noCallBackEntry"))
			MyConfig.getInstance().getMySwithch().setCallBackSwitch(false);
		if (mCmd.hasOption("noFunctionExpand"))
			MyConfig.getInstance().getMySwithch().setFunctionExpandSwitch(false);
		if (mCmd.hasOption("noAsyncMethod"))
			MyConfig.getInstance().getMySwithch().setAsyncMethodSwitch(false);
		if (mCmd.hasOption("noPolym"))
			MyConfig.getInstance().getMySwithch().setPolymSwitch(false);

		if (mCmd.hasOption("noAdapter"))
			MyConfig.getInstance().getMySwithch().setAdapterSwitch(false);
		if (mCmd.hasOption("noStringOp"))
			MyConfig.getInstance().getMySwithch().setStringOpSwitch(false);
		if (mCmd.hasOption("noStaticField"))
			MyConfig.getInstance().getMySwithch().setStaticFieldSwitch(false);

		if (mCmd.hasOption("noFragment"))
			MyConfig.getInstance().getMySwithch().setFragmentSwitch(false);
		if (mCmd.hasOption("noLibCode"))
			MyConfig.getInstance().getMySwithch().setLibCodeSwitch(false);
		if (mCmd.hasOption("noWrapperAPI"))
			MyConfig.getInstance().getMySwithch().setWrapperAPISwitch(false);

		if (mCmd.hasOption("noImplicit"))
			MyConfig.getInstance().getMySwithch().setImplicitLaunchSwitch(false);
		if (mCmd.hasOption("noDynamicBC"))
			MyConfig.getInstance().getMySwithch().setDynamicBCSwitch(false);
		if (mCmd.hasOption("summaryStrategy") && mCmd.getOptionValue("summaryStrategy").equals("none"))
			MyConfig.getInstance().getMySwithch().setSummaryStrategy(SummaryLevel.none);
		else if (mCmd.hasOption("summaryStrategy") && mCmd.getOptionValue("summaryStrategy").equals("path"))
			MyConfig.getInstance().getMySwithch().setSummaryStrategy(SummaryLevel.path);
		else if (mCmd.hasOption("summaryStrategy"))
			MyConfig.getInstance().getMySwithch().setSummaryStrategy(SummaryLevel.object);

		if (mCmd.hasOption("noVfgStrategy"))
			MyConfig.getInstance().getMySwithch().setVfgStrategy(false);
		if (mCmd.hasOption("cgAnalyzeGroup"))
			MyConfig.getInstance().getMySwithch().setCgAnalyzeGroupedStrategy(true);
		if (mCmd.hasOption("getAttributeStrategy"))
			MyConfig.getInstance().getMySwithch().setGetAttributeStrategy(true);
		if (mCmd.hasOption("scenarioStack")) {
			MyConfig.getInstance().getMySwithch().setScenario_stack(true);
			if (MyConfig.getInstance().getMySwithch().isScenario_stack()) {
				MyConfig.getInstance().getMySwithch().setFunctionExpandAllSwitch(true);
			}
		}
	}

	private static void printHelp(String string) {
		System.out.println(string);
		HelpFormatter formatter = new HelpFormatter();
		System.out.println("Please check the help inforamtion");
		formatter.printHelp("java -jar ICCExtractor.jar [options]", getOptions());
		System.exit(0);
	}

	/**
	 * analyze parameters for evaluation
	 */
	private static void setMySwitch() {
		MyConfig.getInstance().getMySwithch().setDummyMainSwitch(false);
		MyConfig.getInstance().getMySwithch().setCallBackSwitch(true);
		MyConfig.getInstance().getMySwithch().setFunctionExpandSwitch(true);

		MyConfig.getInstance().getMySwithch().setAsyncMethodSwitch(true);
		MyConfig.getInstance().getMySwithch().setPolymSwitch(true);

		MyConfig.getInstance().getMySwithch().setAdapterSwitch(true);
		MyConfig.getInstance().getMySwithch().setStringOpSwitch(true);
		MyConfig.getInstance().getMySwithch().setStaticFieldSwitch(true);

		MyConfig.getInstance().getMySwithch().setFragmentSwitch(true);
		MyConfig.getInstance().getMySwithch().setLibCodeSwitch(true);
		MyConfig.getInstance().getMySwithch().setWrapperAPISwitch(true);

		MyConfig.getInstance().getMySwithch().setImplicitLaunchSwitch(true);
		MyConfig.getInstance().getMySwithch().setDynamicBCSwitch(true);

		MyConfig.getInstance().getMySwithch().setSummaryStrategy(SummaryLevel.object);
		MyConfig.getInstance().getMySwithch().setVfgStrategy(true);
		MyConfig.getInstance().getMySwithch().setCgAnalyzeGroupedStrategy(false);
		MyConfig.getInstance().getMySwithch().setGetAttributeStrategy(false);
	}

	/** for self testing **/
	private static void testConfig() {
		String path;
		path = "apk/";
		String name;
		name = "IntentFlowBench";
		String client = "MainClient";

		MyConfig.getInstance().setAppName(name + ".apk");
		MyConfig.getInstance().setAppPath(path + File.separator);
		MyConfig.getInstance().setClient(client);
		MyConfig.getInstance().setMaxPathNumber(100);
		MyConfig.getInstance().setResultFolder("results" + File.separator + "output-test" + File.separator);
		MyConfig.getInstance().setTimeLimit(50);
	}

	/**
	 * startAnalyze
	 */
	private static void startAnalyze() {
		System.out.println("Analyzing " + MyConfig.getInstance().getAppName());
		BaseClient client = getClient();

		TimeUtilsofProject.setTotalTimer(client);
		long startTime = System.currentTimeMillis();

		client.start();

		long endTime = System.currentTimeMillis();
		System.out.println("---------------------------------------");
		System.out.println("Analyzing " + MyConfig.getInstance().getAppName() + " Finish...\n");
		System.out.println(MyConfig.getInstance().getClient() + " time = " + (endTime - startTime) / 1000 + " seconds");
	}

	private static BaseClient getClient() {
		System.out.println("using client " + MyConfig.getInstance().getClient());
		BaseClient client;
		switch (MyConfig.getInstance().getClient()) {
		case "IROutputClient":
			client = new IROutputClient();
			break;
		case "InstrumentClient":
			client = new InstrumentClient();
			break;
		case "ManifestClient":
			client = new ManifestClient();
			break;
		case "CallGraphClient":
			client = new CallGraphClient();
			break;
		case "MainClient":
			client = new ICTGClient();
			break;
		case "CTGEvaluateClient":
			client = new CTGEvaluateClient();
			break;
		default:
			client = new ICTGClient();
			break;
		}
		return client;
	}

}