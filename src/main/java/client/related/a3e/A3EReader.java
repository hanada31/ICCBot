package main.java.client.related.a3e;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import soot.Scene;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import main.java.Analyzer;
import main.java.Global;
import main.java.analyze.model.analyzeModel.MethodSummaryModel;
import main.java.analyze.model.analyzeModel.UnitNode;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.SootUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.obj.model.atg.AtgEdge;
import main.java.client.obj.model.atg.AtgNode;
import main.java.client.obj.model.component.ActivityModel;
import main.java.client.obj.model.component.BroadcastReceiverModel;
import main.java.client.obj.model.component.BundleType;
import main.java.client.obj.model.component.ComponentModel;
import main.java.client.obj.model.component.ContentProviderModel;
import main.java.client.obj.model.component.Data;
import main.java.client.obj.model.component.ExtraData;
import main.java.client.obj.model.component.IntentFilterModel;
import main.java.client.obj.model.component.ServiceModel;
import main.java.client.obj.model.ctg.IntentSummaryModel;
import main.java.client.related.a3e.model.A3EModel;
import main.java.client.related.ic3.model.IC3Model;
import main.java.client.statistic.model.DoStatistic;
import main.java.client.statistic.model.StatisticResult;

public class A3EReader extends Analyzer {
	A3EModel model;
	protected StatisticResult result;
	protected Map<String, MethodSummaryModel> summaryMap;

	public A3EReader(StatisticResult result) {
		this.result = result;
		summaryMap = new HashMap<String, MethodSummaryModel>();
	}

	@Override
	public void analyze() {
		model = Global.v().getA3eModel();
		System.out.println(ConstantUtils.A3EFOLDETR + appModel.getAppName() + ".g.xml");
		model.seta3eFilePath(ConstantUtils.A3EFOLDETR + appModel.getAppName() + ".g.xml");
		if(obtainATGfromFile())
			try {
				constructModel();
			} catch (DocumentException e) {
				e.printStackTrace();
			}
	}

	private boolean obtainATGfromFile() {
		File file = new File(model.geta3eFilePath());
		if (!file.exists()) {
			model.geta3eAtgModel().setExist(false);
			return false;
		}
		return true;
	}
	private void constructModel() throws DocumentException {
		//1.创建Reader对象
        SAXReader reader = new SAXReader();
        //2.加载xml
        Document document = reader.read(new File(model.geta3eFilePath()));
        //3.获取根节点
        Element rootElement = document.getRootElement();
        Iterator<Element> iterator = rootElement.elementIterator();
        while (iterator.hasNext()){ 
        	String source = "";
            Element activityEle = (Element) iterator.next();
            List<Attribute> attributes = activityEle.attributes();
            for (Attribute attribute : attributes) {
               if(attribute.getName().equals("name"))
            	   source = attribute.getValue().replace("-alias", "");
            }
            Iterator<Element> iterator1 = activityEle.elementIterator();
            while (iterator1.hasNext()){
            	String destination = "";
                Element subActivityEle = (Element) iterator1.next();
                List<Attribute> attributes2 = subActivityEle.attributes();
                for (Attribute attribute : attributes2) {
                   if(attribute.getName().equals("name")){
                	   destination = attribute.getValue().replace("-alias", "");
                	   AtgNode sNode = null, tNode = null;
	       				for (String name : Global.v().getAppModel().getComponentMap().keySet()) {
	       					if (name.endsWith(source))
	       						sNode = new AtgNode(name);
	       					if (name.endsWith(destination))
	       						tNode = new AtgNode(name);
	       				}
	       				if (sNode != null && tNode != null) {
	       					AtgEdge edge = new AtgEdge(sNode, tNode, "", -1, "");
	       					model.geta3eAtgModel().addAtgEdges(sNode.getClassName(), edge);
	       					System.out.println(edge);
	       				}
                   }
                }
            }
        }
	}
}
