package com.iscas.iccbot;

import com.iscas.iccbot.analyze.utils.TimeUtilsofProject;
import com.iscas.iccbot.client.BaseClient;
import com.iscas.iccbot.client.cg.CallGraphClient;
import com.iscas.iccbot.client.manifest.ManifestClient;
import com.iscas.iccbot.client.obj.target.ctg.CTGClient;
import com.iscas.iccbot.client.obj.target.ctg.ICCSpecClient;
import com.iscas.iccbot.client.obj.target.fragment.FragmentClient;
import com.iscas.iccbot.client.soot.IROutputClient;
import org.apache.commons.cli.*;

import java.io.File;

/**
 * Main Class of Android ICC Resolution Tool ICCBot
 *
 * @author hanada
 * @version 2.0
 */
public class Main {

    /**
     * get commands from args
     *
     * @param args
     */
    public static void main(String[] args) {
        /** analyze args**/
        CommandLine mCmd = getCommandLine(args);
        analyzeArgs(mCmd);
        /** debug mode in IDE **/
        if (mCmd.hasOption("debug")) {
            testConfig();
            setMySwitch();
        }

        /** start ICCBot**/
        startAnalyze();

        System.out.println("ICC Resolution Finish...\n");
        System.exit(0);
    }

    /**
     * @param mCmdArgs
     * @return
     */
    private static CommandLine getCommandLine(String[] mCmdArgs) {
        CommandLineParser parser = new DefaultParser();
        try {
            return parser.parse(getOptions(), mCmdArgs, false);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * start the analyze of app with a given client
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


    /**
     * get the client to be analyzed
     * the default client is used for ICC resolution
     *
     * @return
     */
    private static BaseClient getClient() {
        System.out.println("using client " + MyConfig.getInstance().getClient());
        BaseClient client;
        switch (MyConfig.getInstance().getClient()) {
            case "CTGClient":
                client = new CTGClient();
                break;
            case "ICCSpecClient":
                client = new ICCSpecClient();
                break;
            case "MainClient":
                client = new CTGClient();
                break;
            case "IROutputClient":
                client = new IROutputClient();
                break;
            case "ManifestClient":
                client = new ManifestClient();
                break;
            case "CallGraphClient":
                client = new CallGraphClient();
                break;
            case "FragmentClient":
                client = new FragmentClient();
                break;
//			case "StoryResultEvaluateClient":
//				client = new IC3ResultEvaluateClient();
//				break;
//			case "IC3DIALDroidResultEvaluateClient":
//				client = new IC3DIALDroidResultEvaluateClient();
//				break;
//			case "GatorATGResultEvaluateClient":
//				client = new GatorATGResultEvaluateClient();
//				break;
//			case "ToolEvaluateClient":
//				client = new ToolEvaluateClient();
//				break;
//			case "TestGenerationClient":
//				client = new TestGenerationClient();
//				break;
//			case "MisEACheckerClient":
//				client = new MisEACheckerClient();
//				break;
//			case "GatorClient":
//				client = new GatorClient();
//				break;
//			case "FragmentRemoveClient":
//				client = new FragmentRemoveClient();
//				break;
            default:
                client = new CTGClient();
                break;
        }
        return client;
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
        options.addOption("name", true, "-name: Set the name of the apk under analysis.");
        options.addOption("path", true, "-path: Set the path to the apk under analysis.");
        options.addOption("androidJar", true, "-androidJar: Set the path of android.jar.");
//		options.addOption("version", true, "-version [default:23]: Version of Android SDK.");

        /** analysis config **/
        options.addOption("client", true, "-client "

                        + "CallGraphClient: Output call graph files.\n"
                        + "ManifestClient: Output manifest.xml file.\n"
                        + "IROutputClient: Output soot IR files.\n"
                        + "FragmentClient: Output the fragment loading results.\n"
                        + "CTGClient/MainClient: Resolve ICC and generate CTG.\n"
                        + "ICCSpecClient: Report ICC specification for each component.\n"

//				+ "ICCBotResultEvaluateClient: Evaluate the results generated by ICCBot.\n"
//				+ "StoryResultEvaluateClient: Evaluate the results generated by IC3.\n"
//				+ "IC3DIALDroidResultEvaluateClient: Evaluate the results generated by IC3DIALDroid.\n"
//				+ "GatorATGResultEvaluateClient: Evaluate the results generated by Gator.\n"
//				+ "ToolEvaluateClient: Evaluate the results of four tools.\n"
//				+ "TestGenerationClient: Generate test cases for components based on the static results.\n"
//				+ "MisEACheckerClient: Report the mis-exported activities.\n"
//				+ "GatorClient: Invoke the client in Gator tool.\n"
        );
        /** analysis config **/
        options.addOption("time", true, "-time [default:90]: Set the max running time (min).");
        options.addOption("maxPathNumber", true, "-maxPathNumber [default:100]: Set the max number of paths.");
        options.addOption("maxFunctionExpandNumber", true, "-maxFunctionExpandNumber [default:10]: Set the max number of expanded functions when perform inter-precedural analysis.");
        options.addOption("maxObjectSummarySize", true, "-maxObjectSummarySize [default:1000]: Set the max number of units in an object summary.");
        options.addOption("callgraphAlgorithm", true, "-callgraphAlgorithm [default:SPARK]: Set algorithm for CG, CHA or SPARK.");

        /** output **/
        options.addOption("outputDir", true, "-outputDir: Set the output folder of the apk.");

        /** debug **/
        options.addOption("debug", false, "-debug: use debug mode.");

        /** Switch **/

        options.addOption("onlyDummyMain", false, "-onlyDummyMain: limit the entry scope");
        options.addOption("noCallBackEntry", false, "-noCallBackEntry: exclude the call back methods");
        options.addOption("noFunctionExpand", false, "-noFunctionExpand: do not inline function with useful contexts");
        options.addOption("noAsyncMethod", false, "-noAsyncMethod: exclude async method call edge");
        options.addOption("noPolym", false, "-noPolym: exclude polymorphism methods");
        options.addOption("noAdapter", false, "-noAdapter: exclude super simple adapter model");
        options.addOption("noStringOp", false, "-noStringOp: exclude string operation model");
        options.addOption("noStaticField", false, "-noStaticField: exclude static field analysis");
        options.addOption("noFragment", false, "-noFragment: exclude fragment operation model");
        options.addOption("noLibCode", false, "-noLibCode: exclude the activities not declared in app's package");
        options.addOption("noWrapperAPI", false, "-noWrapperAPI: exclude RAICC model");
        options.addOption("noImplicit", false, "-noImplicit: exclude implict matching");
        options.addOption("noDynamicBC", false, "-noDynamicBC: exclude dynamic broadcast receiver matching");
        options.addOption("sootOutput", false, "-sootOutput: Output the sootOutput");

//		 /** Strategy **/
//		 options.addOption("summaryStrategy", true,
//		 "-summaryStrategy: choose the type of summary model from object/path/none");
//		 options.addOption("noVfgStrategy", false,
//		 "-vfgStrategy: do not use vfg model");
//		 options.addOption("cgAnalyzeGroup", false,
//		 "-cgAnalyzeGroup: group cg edges into several groups");
//		 options.addOption("getAttributeStrategy", false,
//		 "-getAttributeStrategy: include the analyze of intent data receiveing.");
//		 options.addOption("setAttributeStrategy", false,
//				 "-setAttributeStrategy: include the analyze of intent data sending.");
//				
//		 options.addOption("scenarioStack", false,
//		 "-scenarioStack: for stack related bug analysis.");

        return options;
    }

    /**
     * analyze args and store information to MyConfig
     *
     * @param mCmd
     */
    private static void analyzeArgs(CommandLine mCmd) {
        if (null == mCmd)
            System.exit(-1);

        if (mCmd.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.setWidth(120);

            formatter.printHelp("java -jar [jarFile] [options] [-path] [-name] [-outputDir] [-client]", getOptions());
            System.out.println("E.g., -path apk\\ -name test.apk -outputDir result -client MainClient");
            System.exit(0);
        }

        /** run config **/
        MyConfig.getInstance().setJimple(true);
        MyConfig.getInstance().setAppName(mCmd.getOptionValue("name", ""));
        MyConfig.getInstance().setAppPath(mCmd.getOptionValue("path", System.getProperty("user.dir")) + File.separator);
        MyConfig.getInstance().setAndroidJar(mCmd.getOptionValue("androidJar", "lib/platforms") + File.separator);
//		MyConfig.getInstance().setAndroidVersion("android-" + mCmd.getOptionValue("version", "23"));
        if (mCmd.hasOption("sootOutput"))
            MyConfig.getInstance().setWriteSootOutput(true);

        int timeLimit = Integer.valueOf(mCmd.getOptionValue("time", "90"));
        MyConfig.getInstance().setTimeLimit(timeLimit);
        MyConfig.getInstance().setMaxPathNumber(Integer.valueOf(mCmd.getOptionValue("maxPathNumber", "100")));
        MyConfig.getInstance().setMaxFunctionExpandNumber(Integer.valueOf(mCmd.getOptionValue("maxFunctionExpandNumber", "10")));
        MyConfig.getInstance().setMaxObjectSummarySize(Integer.valueOf(mCmd.getOptionValue("maxObjectSummarySize", "1000")));
        MyConfig.getInstance().setCallGraphAlgorithm(mCmd.getOptionValue("callgraphAlgorithm", "SPARK"));

        String client = mCmd.getOptionValue("client", "MainClient");
        MyConfig.getInstance().setClient(mCmd.getOptionValue("client", client));

        String gatorClient = mCmd.getOptionValue("gatorClient", "GUIHierarchyPrinterClient");
        MyConfig.getInstance().setGatorClient(mCmd.getOptionValue("gatorClient", gatorClient));

        MyConfig.getInstance().setResultFolder(mCmd.getOptionValue("outputDir", "outputDir") + File.separator);
        String resFolder = mCmd.getOptionValue("outputDir", "results/outputDir");
        if (resFolder.contains("/")) {
            resFolder = resFolder.substring(0, resFolder.lastIndexOf("/"));
            MyConfig.getInstance().setResultWrapperFolder(resFolder + File.separator);
        } else if (resFolder.contains("\\")) {
            resFolder = resFolder.substring(0, resFolder.lastIndexOf("\\"));
            MyConfig.getInstance().setResultWrapperFolder(resFolder + File.separator);
        }

        if (!mCmd.hasOption("debug") && !mCmd.hasOption("name")) {
            printHelp("Please input the apk name use -name.");
        }
//		if (!mCmd.hasOption("debug") && !mCmd.hasOption("androidJar")) {
//			printHelp("Please input the path of android.jar use -androidJar.");
//		}

        /** analysis config **/
        if (mCmd.hasOption("onlyDummyMain"))
            MyConfig.getInstance().getMySwitch().setDummyMainSwitch(true);
        if (mCmd.hasOption("noCallBackEntry"))
            MyConfig.getInstance().getMySwitch().setCallBackSwitch(false);
        if (mCmd.hasOption("noFunctionExpand"))
            MyConfig.getInstance().getMySwitch().setFunctionExpandSwitch(false);
        if (mCmd.hasOption("noAsyncMethod"))
            MyConfig.getInstance().getMySwitch().setAsyncMethodSwitch(false);
        if (mCmd.hasOption("noPolym"))
            MyConfig.getInstance().getMySwitch().setPolymSwitch(false);

        if (mCmd.hasOption("noAdapter"))
            MyConfig.getInstance().getMySwitch().setAdapterSwitch(false);
        if (mCmd.hasOption("noStringOp"))
            MyConfig.getInstance().getMySwitch().setStringOpSwitch(false);
        if (mCmd.hasOption("noStaticField"))
            MyConfig.getInstance().getMySwitch().setStaticFieldSwitch(false);

        if (mCmd.hasOption("noFragment"))
            MyConfig.getInstance().getMySwitch().setFragmentSwitch(false);
        if (mCmd.hasOption("noLibCode"))
            MyConfig.getInstance().getMySwitch().setLibCodeSwitch(false);
        if (mCmd.hasOption("noWrapperAPI"))
            MyConfig.getInstance().getMySwitch().setWrapperAPISwitch(false);

        if (mCmd.hasOption("noImplicit"))
            MyConfig.getInstance().getMySwitch().setImplicitLaunchSwitch(false);
        if (mCmd.hasOption("noDynamicBC"))
            MyConfig.getInstance().getMySwitch().setDynamicBCSwitch(false);
        if (mCmd.hasOption("summaryStrategy") && mCmd.getOptionValue("summaryStrategy").equals("none"))
            MyConfig.getInstance().getMySwitch().setSummaryStrategy(SummaryLevel.none);
        else if (mCmd.hasOption("summaryStrategy") && mCmd.getOptionValue("summaryStrategy").equals("path"))
            MyConfig.getInstance().getMySwitch().setSummaryStrategy(SummaryLevel.path);
        else if (mCmd.hasOption("summaryStrategy"))
            MyConfig.getInstance().getMySwitch().setSummaryStrategy(SummaryLevel.object);

        if (mCmd.hasOption("noVfgStrategy"))
            MyConfig.getInstance().getMySwitch().setVfgStrategy(false);
        if (mCmd.hasOption("cgAnalyzeGroup"))
            MyConfig.getInstance().getMySwitch().setCgAnalyzeGroupedStrategy(true);
        if (mCmd.hasOption("getAttributeStrategy"))
            MyConfig.getInstance().getMySwitch().setGetAttributeStrategy(true);
        if (mCmd.hasOption("setAttributeStrategy"))
            MyConfig.getInstance().getMySwitch().setSetAttributeStrategy(true);
        if (mCmd.hasOption("scenarioStack")) {
            MyConfig.getInstance().getMySwitch().setScenario_stack(true);
            if (MyConfig.getInstance().getMySwitch().isScenario_stack()) {
                MyConfig.getInstance().getMySwitch().setFunctionExpandAllSwitch(true);
            }
        }
    }

    private static void printHelp(String string) {
        System.out.println(string);
        HelpFormatter formatter = new HelpFormatter();
        System.out.println("Please check the help inforamtion");
        formatter.printHelp("java -jar ICCBot.jar [options]", getOptions());
        System.exit(0);
    }

    /**
     * analyze parameters for evaluation
     */
    private static void setMySwitch() {
        MyConfig.getInstance().getMySwitch().setDummyMainSwitch(false);
        MyConfig.getInstance().getMySwitch().setCallBackSwitch(true);
        MyConfig.getInstance().getMySwitch().setFunctionExpandSwitch(true);

        MyConfig.getInstance().getMySwitch().setAsyncMethodSwitch(true);
        MyConfig.getInstance().getMySwitch().setPolymSwitch(true);

        MyConfig.getInstance().getMySwitch().setAdapterSwitch(true);
        MyConfig.getInstance().getMySwitch().setStringOpSwitch(true);
        MyConfig.getInstance().getMySwitch().setStaticFieldSwitch(true);

        MyConfig.getInstance().getMySwitch().setFragmentSwitch(true);
        MyConfig.getInstance().getMySwitch().setLibCodeSwitch(true);
        MyConfig.getInstance().getMySwitch().setWrapperAPISwitch(true);

        MyConfig.getInstance().getMySwitch().setImplicitLaunchSwitch(true);
        MyConfig.getInstance().getMySwitch().setDynamicBCSwitch(true);

        MyConfig.getInstance().getMySwitch().setSummaryStrategy(SummaryLevel.object);
        MyConfig.getInstance().getMySwitch().setVfgStrategy(true);
        MyConfig.getInstance().getMySwitch().setCgAnalyzeGroupedStrategy(false);
    }

    /**
     * for self testing
     **/
    private static void testConfig() {
        String path = "apk/";
        String name = "com.umetrip.android.msky.app";
        String client = "MainClient";
//		client = "ToolEvaluateClient"; 

		/*
		MyConfig.getInstance().setAppName(name + ".apk");
		MyConfig.getInstance().setAppPath(path + File.separator);
		MyConfig.getInstance().setClient(client);
		MyConfig.getInstance().setGatorClient("GUIHierarchyPrinterClient");
		MyConfig.getInstance().setGatorClient("ActivityTransitionAnalysisClient");
		MyConfig.getInstance().setMaxPathNumber(30);
		MyConfig.getInstance().setMaxFunctionExpandNumber(5); //10?
		MyConfig.getInstance().setMaxObjectSummarySize(100);
		MyConfig.getInstance().setResultWrapperFolder("results/" + File.separator);
		MyConfig.getInstance().setResultFolder(MyConfig.getInstance().getResultWrapperFolder()+ "output" + File.separator);
		MyConfig.getInstance().setTimeLimit(10);
		MyConfig.getInstance().setAndroidJar("lib/platforms");
		 */
        MyConfig myConfig = MyConfig.getInstance();
        myConfig.setAppName(name + ".apk");
        myConfig.setAppPath(path + File.separator);
        myConfig.setClient(client);
//        myConfig.setGatorClient("GUIHierarchyPrinterClient");
//        myConfig.setGatorClient("ActivityTransitionAnalysisClient");
//        myConfig.setMaxPathNumber(100);
//        myConfig.setMaxFunctionExpandNumber(5); //10?
//        myConfig.setMaxObjectSummarySize(100);
        myConfig.setResultWrapperFolder("results/" + File.separator);
        myConfig.setResultFolder(myConfig.getResultWrapperFolder() + "output" + File.separator);
        myConfig.setTimeLimit(1440);
        myConfig.setAndroidJar("lib/platforms");
    }
}