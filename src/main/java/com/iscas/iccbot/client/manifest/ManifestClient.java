package com.iscas.iccbot.client.manifest;

import java.io.File;
import java.io.IOException;

import com.iscas.iccbot.client.soot.SootAnalyzer;
import com.iscas.iccbot.Global;
import com.iscas.iccbot.MyConfig;
import com.iscas.iccbot.analyze.utils.ConstantUtils;
import com.iscas.iccbot.analyze.utils.output.FileUtils;
import com.iscas.iccbot.client.BaseClient;
import org.dom4j.DocumentException;

/**
 * Analyzer Class
 * 
 * @author hanada
 * @version 2.0
 */
public class ManifestClient extends BaseClient {

	@Override
	protected void clientAnalyze() {
		if (!MyConfig.getInstance().isSootAnalyzeFinish()) {
			SootAnalyzer sootAnalyzer = new SootAnalyzer();
			sootAnalyzer.analyze();
		}
		MainfestAnalyzer mainfestAnalyzer = new MainfestAnalyzer();
		mainfestAnalyzer.analyze();

		System.out.println("Successfully analyze with ManifestClient.");
	}

	@Override
	public void clientOutput() throws IOException, DocumentException {
		String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
				+ File.separator;
		FileUtils.createFolder(summary_app_dir + ConstantUtils.MANIFOLDETR);

		/** manifest **/
		ManifestClientOutput.writeManifest(summary_app_dir + ConstantUtils.MANIFOLDETR, ConstantUtils.MANIFEST);
		// String content = Global.v().getAppModel().getAppName() + "\t" +
		// Global.v().getAppModel().getPackageName()
		// + "\t" + Global.v().getAppModel().getComponentMap().size() + "\t"
		// + Global.v().getAppModel().getExportedComponentMap().size() + "\t"
		// + Global.v().getAppModel().getActivityMap().size() + "\n";
		// FileUtils.writeText2File(MyConfig.getInstance().getResultFolder() +
		// "componentNumber.txt", content, true);

	}

}