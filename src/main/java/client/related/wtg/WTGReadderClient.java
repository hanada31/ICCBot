package main.java.client.related.wtg;

import java.io.File;
import java.io.IOException;

import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.model.analyzeModel.AppModel;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.BaseClient;
import main.java.client.manifest.ManifestClient;
import main.java.client.obj.model.atg.ATGModel;
import main.java.client.statistic.model.StatisticResult;

import org.dom4j.DocumentException;

/**
 * Analyzer Class
 * 
 * @author yanjw
 * @version 2.0
 */
public class WTGReadderClient extends BaseClient {

	/**
	 * analyze logic for single app
	 * 
	 * @return
	 */
	@Override
	protected void clientAnalyze() {
		if (!MyConfig.getInstance().isManifestAnalyzeFinish()) {
			new ManifestClient().start();
			MyConfig.getInstance().setManifestAnalyzeFinish(true);
		}
		ATGModel model = Global.v().getWtgModel().getWTGAtgModel();
		model.setATGFilePath(ConstantUtils.WTGFOLDETR + Global.v().getAppModel().getAppName() + "_wtg.txt");
		ATGReader reader = new ATGReader(model);
		reader.analyze();
		System.out.println("Successfully analyze with WTGGraphClient.");
	}

	@Override
	public void clientOutput() throws IOException, DocumentException {
		AppModel appModel = Global.v().getAppModel();
		String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
				+ File.separator;
		FileUtils.createFolder(summary_app_dir + ConstantUtils.WTGFOLDETR);

		FileUtils.copyFile(ConstantUtils.WTGFOLDETR + appModel.getAppName() + "_wtg.txt", summary_app_dir
				+ ConstantUtils.WTGFOLDETR + appModel.getAppName() + "_wtg.txt");
		FileUtils.copyFile(ConstantUtils.WTGFOLDETR + appModel.getAppName() + "_wtg.dot", summary_app_dir
				+ ConstantUtils.WTGFOLDETR + appModel.getAppName() + "_wtg.dot");
		FileUtils.copyFile(ConstantUtils.WTGFOLDETR + appModel.getAppName() + "_wtg.pdf", summary_app_dir
				+ ConstantUtils.WTGFOLDETR + appModel.getAppName() + "_wtg.pdf");
	}

}