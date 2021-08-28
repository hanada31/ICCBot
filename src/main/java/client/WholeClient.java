package main.java.client;

import java.io.IOException;

import main.java.MyConfig;
import main.java.client.WholeClient;
import main.java.client.manifest.ManifestClient;
import main.java.client.obj.target.ictg.ICTGClient;
import main.java.client.related.ic3.IC3ReaderClient;
import main.java.client.statistic.model.StatisticResult;
import main.java.client.testcase.ICCGenerator;
import main.java.client.testcase.TestGenerator;

import org.dom4j.DocumentException;

/**
 * Analyzer Class
 * 
 * @author yanjw
 * @version 2.0
 */
public class WholeClient extends BaseClient {

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

		new ICTGClient().start();

		new IC3ReaderClient().start();

		if (MyConfig.getInstance().isTestGeneration()) {
			ICCGenerator iccGenerator = new ICCGenerator();
			iccGenerator.analyze();

			TestGenerator tg = new TestGenerator();
			tg.analyze();
		}
		System.out.println("Successfully analyze with WholeClient.");
	}

	@Override
	public void clientOutput() throws IOException, DocumentException {

	}

}