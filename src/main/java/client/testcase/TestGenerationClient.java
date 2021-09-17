package main.java.client.testcase;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import main.java.Global;
import main.java.MyConfig;
import main.java.SummaryLevel;
import main.java.analyze.model.analyzeModel.AppModel;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.BaseClient;
import main.java.client.cg.CallGraphClient;
import main.java.client.cg.DynamicReceiverCGAnalyzer;
import main.java.client.manifest.ManifestClient;
import main.java.client.obj.ObjectAnalyzer;
import main.java.client.obj.model.atg.ATGModel;
import main.java.client.obj.model.component.BundleType;
import main.java.client.obj.model.component.ComponentModel;
import main.java.client.obj.model.ictg.ICCMsg;
import main.java.client.obj.model.ictg.IntentRecieveModel;
import main.java.client.obj.target.fragment.FragmentClient;
import main.java.client.obj.target.ictg.ICTGAnalyzer;
import main.java.client.obj.target.ictg.ICTGClient;
import main.java.client.obj.target.ictg.StaticValueAnalyzer;
import main.java.client.statistic.model.StatisticResult;
import main.java.client.testcase.TestGenerationClient;

import org.dom4j.DocumentException;

import soot.SootClass;
import soot.SootMethod;

/**
 * Analyzer Class
 * 
 * @author hanada
 * @version 2.0
 */
public class TestGenerationClient extends ICTGClient {
	AppModel appModel = Global.v().getAppModel();
	
	@Override
	protected void setMySwitch() {
		MyConfig.getInstance().getMySwithch().setGetAttributeStrategy(true);
		MyConfig.getInstance().getMySwithch().setSetAttributeStrategy(true);
		MyConfig.getInstance().getMySwithch().setFragmentSwitch(false);
		MyConfig.getInstance().getMySwithch().setSummaryStrategy(SummaryLevel.flow);
		MyConfig.getInstance().getMySwithch().setDummyMainSwitch(true);
	}
	
	@Override
	protected void clientAnalyze() {
		super.clientAnalyze();
		
		System.out.println("Start Test Generation...");
		TestGenerationProcess tg = new TestGenerationProcess();
		tg.init();
		tg.createAndroidProject();

		// className : act to be analyzed
		for (String className : Global.v().getAppModel().getComponentMap().keySet()) {
			ICCGenerator generator = new ICCGenerator(className);
			generator.analyze();
			Set<ICCMsg> ICCs = generator.getICCSet();
			if (ICCs != null && ICCs.size() != 0) {
				tg.handleICCMsgs(ICCs, className);
			}
		}
		tg.generateManifest();
		tg.buildProject();

		System.out.println("End Test Generation...");
		System.out.println("Successfully analyze with TestGenerationClient.");
	}


	
	@Override
	public void clientOutput() throws IOException, DocumentException {
		super.clientOutput();
		
		
	}

 }