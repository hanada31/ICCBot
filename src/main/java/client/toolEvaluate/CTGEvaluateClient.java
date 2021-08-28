package main.java.client.toolEvaluate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.model.labeledOracleModel.LabeledOracleModel;
import main.java.analyze.model.labeledOracleModel.LabeledOracleReader;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.GraphUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.BaseClient;
import main.java.client.obj.model.atg.ATGModel;
import main.java.client.obj.target.ictg.ICTGClient;
import main.java.client.obj.target.ictg.ICTGReaderClient;
import main.java.client.related.ic3.IC3ReaderClient;
import main.java.client.related.ic3dial.IC3DialReaderClient;
import main.java.client.related.wtg.WTGReadderClient;
import main.java.client.statistic.model.StatisticResult;

/**
 * Analyzer Class
 * 
 * @author yanjw
 * @version 2.0
 */
public class CTGEvaluateClient extends BaseClient {

	@Override
	protected void clientAnalyze() {

		new ICTGReaderClient().start();
		System.out.println("Successfully analyze with CTGEvaluateClient.");

	}

	@Override
	public void clientOutput() {
		StringBuilder sb = new StringBuilder(MyConfig.getInstance().getMySwithch().toString());
		ICCBotEvaluate(sb);
	}

	/**
	 * Evaluate the completeness/connection rate/false nagetive of ICCBot
	 * 
	 * @param sb
	 */
	private void ICCBotEvaluate(StringBuilder sb) {
		System.out.println();
		ATGModel oracleModel = Global.v().getiCTGModel().getOracleModel();
		ATGModel optModel = Global.v().getiCTGModel().getOptModelwithoutFrag();
		initStringBuilderComplete("ICCBot      ", sb);
		optModel.evaluateCompleteness("ICCBot      ", sb);

		initStringBuilderConnection("ICCBot      ", sb);
		optModel.evaluateConnectivity("ICCBot      ", sb);

		if (oracleModel.getAtgEdges().size() > 0) {
			initStringBuilderFN("ICCBot      ", sb);
			optModel.evaluateFalseNegative("ICCBot      ", oracleModel, sb);
		}
		System.out.println();
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
		sb.append("acrtualEdges\t");
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