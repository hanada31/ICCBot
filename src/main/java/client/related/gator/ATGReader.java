package main.java.client.related.gator;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import main.java.Analyzer;
import main.java.Global;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.obj.model.atg.ATGModel;
import main.java.client.obj.model.atg.AtgEdge;
import main.java.client.obj.model.atg.AtgNode;

public class ATGReader extends Analyzer {
	ATGModel model;

	public ATGReader(ATGModel model) {
		this.model = model;
	}

	@Override
	public void analyze() {
	}
	
	public boolean obtainATGfromFile() {
		File file = new File(model.getATGFilePath());
		if (!file.exists()) {
			model.setExist(false);
			return false;
		}
		return true;
	}
	public void constructModelForGator() {
		List<String> lines = FileUtils.getListFromFile(model.getATGFilePath());
		for (String line : lines) {
			line = line.trim();
			line = line.replace("Node(", "");
			line = line.replace(")", "");
			line = line.replace(";", "");
			line = line.replace(" -> ", "->");
			if (line.split("->").length == 2) {
				String source = line.split("->")[0];
				String target = line.split("->")[1];
				AtgNode sNode = null, tNode = null;
				for (String name : Global.v().getAppModel().getComponentMap().keySet()) {
					if (name.endsWith(source))
						sNode = new AtgNode(name);
					if (name.endsWith(target))
						tNode = new AtgNode(name);
				}
				if (sNode != null && tNode != null) {
					AtgEdge edge = new AtgEdge(sNode, tNode, "", -1, "");
					model.addAtgEdges(sNode.getClassName(), edge);
					System.out.println(edge);
				}
			}
		}
	}

	
	public void constructModelForICCBot() throws DocumentException {
		//1.创建Reader对象
        SAXReader reader = new SAXReader();
        //2.加载xml
        System.out.println(model.getATGFilePath());
        Document document = reader.read(new File(model.getATGFilePath()));
        //3.获取根节点
        Element rootElement = document.getRootElement();
        Iterator<Element> iterator = rootElement.elementIterator();
        while (iterator.hasNext()){ 
        	String source = "";
            Element activityEle = iterator.next();
            List<Attribute> attributes = activityEle.attributes();
            for (Attribute attribute : attributes) {
               if(attribute.getName().equals("name"))
            	   source = attribute.getValue();
            }
            Iterator<Element> iterator1 = activityEle.elementIterator();
            while (iterator1.hasNext()){
            	String destination = "";
                Element subActivityEle = iterator1.next();
                List<Attribute> attributes2 = subActivityEle.attributes();
                for (Attribute attribute : attributes2) {
                   if(attribute.getName().equals("name")){
                	   destination = attribute.getValue();
                	   AtgNode sNode = null, tNode = null;
	       				for (String name : Global.v().getAppModel().getComponentMap().keySet()) {
	       					if (name.endsWith(source))
	       						sNode = new AtgNode(name);
	       					if (name.endsWith(destination))
	       						tNode = new AtgNode(name);
	       				}
	       				if (sNode != null && tNode != null) {
	       					AtgEdge edge = new AtgEdge(sNode, tNode, "", -1, "");
	       					model.addAtgEdges(sNode.getClassName(), edge);
	       					System.out.println("aa  "+edge);
	       				}
                   }
                }
            }
        }
	}
}
