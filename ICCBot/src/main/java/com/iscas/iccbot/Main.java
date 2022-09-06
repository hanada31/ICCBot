package com.iscas.iccbot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iscas.iccbot.analyze.utils.SootUtils;
import com.iscas.iccbot.analyze.utils.TimeUtilsofProject;
import com.iscas.iccbot.client.BaseClient;
import com.iscas.iccbot.client.cg.CallGraphClient;
import com.iscas.iccbot.client.manifest.ManifestClient;
import com.iscas.iccbot.client.obj.target.ctg.CTGClient;
import com.iscas.iccbot.client.obj.target.ctg.ICCSpecClient;
import com.iscas.iccbot.client.obj.target.fragment.FragmentClient;
import com.iscas.iccbot.client.soot.IROutputClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Main Class of Android ICC Resolution Tool ICCBot
 *
 * @author hanada
 * @version 2.0
 */
@Slf4j
public class Main {

    /**
     * get commands from args
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Analyze args
        CommandLine mCmd = getCommandLine(args);
        if (mCmd == null) {
            return;
        }
        analyzeArgs(mCmd);

        // start ICCBot
        startAnalyze();

        System.out.println("ICC Resolution Finish...\n");
        System.exit(0);
    }

    /**
     * Get command line
     *
     * @param mCmdArgs Command line arguments
     * @return CommandLine
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
    public static void startAnalyze() {
        log.info("Analyzing " + MyConfig.getInstance().getAppName());
        BaseClient client = getClient();

        TimeUtilsofProject.setTotalTimer(client);
        long startTime = System.currentTimeMillis();

        client.start();

        long endTime = System.currentTimeMillis();
        log.info("---------------------------------------");
        log.info("Analyzing " + MyConfig.getInstance().getAppName() + " Finish...\n");
        log.info(MyConfig.getInstance().getClient() + " time = " + (endTime - startTime) / 1000 + " seconds");
    }

    /**
     * get the client to be analyzed
     * the default client is used for ICC resolution
     *
     * @return
     */
    private static BaseClient getClient() {
        log.info("Using client " + MyConfig.getInstance().getClient());
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
        options.addOption("config", true, "-config: Path to config.json");
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

            formatter.printHelp("java -jar [jarFile] [options] [-path] [-name] [-outputDir] [-client]\n" +
                    "E.g., -path apk\\ -name test.apk -outputDir result -client MainClient", getOptions());
            System.exit(0);
        }

        /** run config **/
        MyConfig myConfig = MyConfig.getInstance();
        
        myConfig.setJimple(true);
        myConfig.setAppName(mCmd.getOptionValue("name", ""));
        myConfig.setAppPath(mCmd.getOptionValue("path", System.getProperty("user.dir")) + File.separator);
        myConfig.setAndroidJar(mCmd.getOptionValue("androidJar", "lib/platforms") + File.separator);
//		myConfig.setAndroidVersion("android-" + mCmd.getOptionValue("version", "23"));
        if (mCmd.hasOption("sootOutput"))
            myConfig.setWriteSootOutput(true);

        int timeLimit = Integer.parseInt(mCmd.getOptionValue("time", "90"));
        myConfig.setTimeLimit(timeLimit);
        myConfig.setMaxPathNumber(Integer.parseInt(mCmd.getOptionValue("maxPathNumber", "100")));
        myConfig.setMaxFunctionExpandNumber(Integer.parseInt(mCmd.getOptionValue("maxFunctionExpandNumber", "10")));
        myConfig.setMaxObjectSummarySize(Integer.parseInt(mCmd.getOptionValue("maxObjectSummarySize", "1000")));
        myConfig.setCallGraphAlgorithm(mCmd.getOptionValue("callgraphAlgorithm", "SPARK"));

        String client = mCmd.getOptionValue("client", "MainClient");
        myConfig.setClient(mCmd.getOptionValue("client", client));

        String gatorClient = mCmd.getOptionValue("gatorClient", "GUIHierarchyPrinterClient");
        myConfig.setGatorClient(mCmd.getOptionValue("gatorClient", gatorClient));

        myConfig.setResultFolder(mCmd.getOptionValue("outputDir", "outputDir") + File.separator);
        String resFolder = mCmd.getOptionValue("outputDir", "results/outputDir");
        if (resFolder.contains("/")) {
            resFolder = resFolder.substring(0, resFolder.lastIndexOf("/"));
            myConfig.setResultWrapperFolder(resFolder + File.separator);
        } else if (resFolder.contains("\\")) {
            resFolder = resFolder.substring(0, resFolder.lastIndexOf("\\"));
            myConfig.setResultWrapperFolder(resFolder + File.separator);
        }

        if (!mCmd.hasOption("name")) {
            printHelp("Please input the apk name use -name.");
        }

        /** analysis config **/
        if (mCmd.hasOption("onlyDummyMain"))
            myConfig.getMySwitch().setDummyMainSwitch(true);
        if (mCmd.hasOption("noCallBackEntry"))
            myConfig.getMySwitch().setCallBackSwitch(false);
        if (mCmd.hasOption("noFunctionExpand"))
            myConfig.getMySwitch().setFunctionExpandSwitch(false);
        if (mCmd.hasOption("noAsyncMethod"))
            myConfig.getMySwitch().setAsyncMethodSwitch(false);
        if (mCmd.hasOption("noPolym"))
            myConfig.getMySwitch().setPolymSwitch(false);

        if (mCmd.hasOption("noAdapter"))
            myConfig.getMySwitch().setAdapterSwitch(false);
        if (mCmd.hasOption("noStringOp"))
            myConfig.getMySwitch().setStringOpSwitch(false);
        if (mCmd.hasOption("noStaticField"))
            myConfig.getMySwitch().setStaticFieldSwitch(false);

        if (mCmd.hasOption("noFragment"))
            myConfig.getMySwitch().setFragmentSwitch(false);
        if (mCmd.hasOption("noLibCode"))
            myConfig.getMySwitch().setLibCodeSwitch(false);
        if (mCmd.hasOption("noWrapperAPI"))
            myConfig.getMySwitch().setWrapperAPISwitch(false);

        if (mCmd.hasOption("noImplicit"))
            myConfig.getMySwitch().setImplicitLaunchSwitch(false);
        if (mCmd.hasOption("noDynamicBC"))
            myConfig.getMySwitch().setDynamicBCSwitch(false);
        if (mCmd.hasOption("summaryStrategy") && mCmd.getOptionValue("summaryStrategy").equals("none"))
            myConfig.getMySwitch().setSummaryStrategy(SummaryLevel.none);
        else if (mCmd.hasOption("summaryStrategy") && mCmd.getOptionValue("summaryStrategy").equals("path"))
            myConfig.getMySwitch().setSummaryStrategy(SummaryLevel.path);
        else if (mCmd.hasOption("summaryStrategy"))
            myConfig.getMySwitch().setSummaryStrategy(SummaryLevel.object);

        if (mCmd.hasOption("noVfgStrategy"))
            myConfig.getMySwitch().setVfgStrategy(false);
        if (mCmd.hasOption("cgAnalyzeGroup"))
            myConfig.getMySwitch().setCgAnalyzeGroupedStrategy(true);
        if (mCmd.hasOption("getAttributeStrategy"))
            myConfig.getMySwitch().setGetAttributeStrategy(true);
        if (mCmd.hasOption("setAttributeStrategy"))
            myConfig.getMySwitch().setSetAttributeStrategy(true);
        if (mCmd.hasOption("scenarioStack")) {
            myConfig.getMySwitch().setScenario_stack(true);
            if (myConfig.getMySwitch().isScenario_stack()) {
                myConfig.getMySwitch().setFunctionExpandAllSwitch(true);
            }
        }

        // Load Analyze Config
        String analyzeConfigPath = "config/config.json";
        if (mCmd.hasOption("config")) {
            analyzeConfigPath = mCmd.getOptionValue("config");
        }
        Path fPath = Paths.get(analyzeConfigPath);
        if (!Files.exists(fPath)) {
            log.error("Failed to load analyze config json: File not exist");
            System.exit(0);
        }

        JSONObject analyzeConfig = null;
        try {
            analyzeConfig = JSON.parseObject(String.join("\n", Files.readAllLines(fPath)));
            myConfig.setAnalyzeConfig(analyzeConfig);
        } catch (IOException e) {
            log.error("Failed to load analyze config json: IOException", e);
            System.exit(0);
        }
        // Initialize SootUtils.excludePackages

        JSONArray excArr = MyConfig.getInstance().getAnalyzeConfig().getJSONArray("SootAnalyzer.excludePackages");
        if (excArr == null) return;
        List<String> excList = excArr.toJavaList(String.class);
        List<String> excPkgList = new ArrayList<>();
        for (String expr : excList) {
            excPkgList.add("<" + expr.replaceAll("\\*", ""));
        }
        SootUtils.setExcludePackages(excPkgList);

        log.debug("Applied analyze config keys: " + analyzeConfig.keySet());
    }

    private static void printHelp(String str) {
        System.out.println(str);
        HelpFormatter formatter = new HelpFormatter();
        System.out.println("Please check the help information");
        formatter.printHelp("java -jar ICCBot.jar [options]", getOptions());
        System.exit(0);
    }
}