package main.java.client.statistic.model;

import java.util.ArrayList;
import java.util.List;
import org.dom4j.Element;

public class XmlStatistic {

	// statistic
	private List<Element> allSingleMethodEleList;
	private List<Element> entrySingleMethodEleList;

	private List<Element> allSinglePathEleList;
	private List<Element> entrySinglePathEleList;

	private List<Element> allSingleIntentEleList;
	private List<Element> entrySingleIntentEleList;

	public XmlStatistic() {
		setAllSingleMethodEleList(new ArrayList<Element>());
		setEntrySingleMethodEleList(new ArrayList<Element>());
		setAllSinglePathEleList(new ArrayList<Element>());
		setEntrySinglePathEleList(new ArrayList<Element>());
		setAllSingleIntentEleList(new ArrayList<Element>());
		setEntrySingleIntentEleList(new ArrayList<Element>());
	}

	public List<Element> getEntrySingleMethodEleList() {
		return entrySingleMethodEleList;
	}

	public void setEntrySingleMethodEleList(List<Element> entryNodeEleList) {
		this.entrySingleMethodEleList = entryNodeEleList;
	}

	public void addEntrySingleMethodEleList(Element element) {
		this.entrySingleMethodEleList.add(element);
	}

	public List<Element> getAllSingleMethodEleList() {
		return allSingleMethodEleList;
	}

	public void addAllSingleMethodEleList(Element element) {
		this.allSingleMethodEleList.add(element);
	}

	public void setAllSingleMethodEleList(List<Element> elementList) {
		this.allSingleMethodEleList = elementList;
	}

	public List<Element> getEntrySinglePathEleList() {
		return entrySinglePathEleList;
	}

	public void setEntrySinglePathEleList(List<Element> entryNodeEleList) {
		this.entrySinglePathEleList = entryNodeEleList;
	}

	public void addEntrySinglePathEleList(Element element) {
		this.entrySinglePathEleList.add(element);
	}

	public List<Element> getAllSinglePathEleList() {
		return allSinglePathEleList;
	}

	public void addAllSinglePathEleList(Element element) {
		this.allSinglePathEleList.add(element);
	}

	public void setAllSinglePathEleList(List<Element> elementList) {
		this.allSinglePathEleList = elementList;
	}

	public List<Element> getAllSingleIntentEleList() {
		return allSingleIntentEleList;
	}

	public void setAllSingleIntentEleList(List<Element> allSingleIntentEleList) {
		this.allSingleIntentEleList = allSingleIntentEleList;
	}

	public void addAllSingleIntentEleList(Element element) {
		this.allSingleIntentEleList.add(element);
	}

	public List<Element> getEntrySingleIntentEleList() {
		return entrySingleIntentEleList;
	}

	public void setEntrySingleIntentEleList(List<Element> entrySingleIntentEleList) {
		this.entrySingleIntentEleList = entrySingleIntentEleList;
	}

	public void addEntrySingleIntentEleList(Element element) {
		this.entrySingleIntentEleList.add(element);
	}

}
