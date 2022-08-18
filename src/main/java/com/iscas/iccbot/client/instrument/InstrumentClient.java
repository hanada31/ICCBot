package com.iscas.iccbot.client.instrument;

import java.io.File;

import com.iscas.iccbot.client.manifest.ManifestClient;
import com.iscas.iccbot.Global;
import com.iscas.iccbot.MyConfig;
import com.iscas.iccbot.analyze.utils.ConstantUtils;
import com.iscas.iccbot.analyze.utils.output.FileUtils;
import com.iscas.iccbot.client.BaseClient;
import com.iscas.iccbot.client.cg.CallGraphClient;

/**
 * Analyzer Class
 * 
 * @author hanada
 * @version 2.0
 */
public class InstrumentClient extends BaseClient {

	@Override
	protected void clientAnalyze() {
		if (!MyConfig.getInstance().isManifestAnalyzeFinish()) {
			new ManifestClient().start();
			MyConfig.getInstance().setManifestAnalyzeFinish(true);
		}

		if (!MyConfig.getInstance().isCallGraphAnalyzeFinish()) {
			new CallGraphClient().start();
			MyConfig.getInstance().setCallGraphAnalyzeFinish(true);
		}

		InstrumentAnalyzer analyzer = new InstrumentAnalyzer();
		analyzer.analyze();

		System.out.println("Successfully analyze with InstrumentClientClient.");

	}

	@Override
	public void clientOutput() {
		String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
				+ File.separator;
		FileUtils.createFolder(summary_app_dir + ConstantUtils.INSTRUFOLDER);
		/** call graph **/
		InstrumentClientOutput.writeInstrumentFile(summary_app_dir + ConstantUtils.INSTRUFOLDER,
				ConstantUtils.INSTRUFILE);
		String instrumentedApkPath = summary_app_dir + ConstantUtils.INSTRUFOLDER
				+ Global.v().getAppModel().getAppName() + ".apk";
		System.out.println("sign Apk.");
		InstrumentClientOutput.signApk(instrumentedApkPath);
	}

}