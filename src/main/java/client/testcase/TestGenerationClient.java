//package main.java.client.testcase;
//
//import java.io.IOException;
//
//import main.java.MyConfig;
//import main.java.client.BaseClient;
//import main.java.client.statistic.model.StatisticResult;
//import main.java.client.testcase.TestGenerationClient;
//
//import org.dom4j.DocumentException;
//
///**
// * Analyzer Class
// * 
// * @author yanjw
// * @version 2.0
// */
//public class TestGenerationClient extends BaseClient {
//
//	@Override
//	protected void clientAnalyze() {
//		ICCGenerator iccGenerator = new ICCGenerator();
//		iccGenerator.analyze();
//		if (MyConfig.getInstance().isTestGeneration()) {
//			TestGenerator tg = new TestGenerator();
//			tg.analyze();
//		}
//		System.out.println("Successfully analyze with TestGenerationClient.");
//	}
//
//	@Override
//	public void clientOutput() throws IOException, DocumentException {
//		// TODO Auto-generated method stub
//
//	}
//
//}