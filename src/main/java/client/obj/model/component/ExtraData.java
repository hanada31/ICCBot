package main.java.client.obj.model.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import main.java.analyze.utils.SootUtils;
import main.java.analyze.utils.output.PrintUtils;
import main.java.client.obj.model.component.BundleType;
import main.java.client.obj.model.component.ExtraData;

public class ExtraData implements Serializable, Cloneable {
	private static final long serialVersionUID = 4L;
	private Object type;
	private String name;
	private String eaName;
	private String objName;
	private List<String> value;

	public ExtraData(String name) {
		super();
		this.setName(name);
	}

	public ExtraData() {
		this.setName("");
		this.eaName = "";
		this.setObjName("");
		this.setValue(new ArrayList<String>());

	}

	public ExtraData(Object type2, String name2, String eaName2, String objName2, List<String> value2) {
		this.setType(type2);
		this.setName(name2);
		this.eaName = eaName2;
		this.setObjName(objName2);
		this.setValue(new ArrayList<String>());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getType() {
		return type;
	}

	public void setType(Object type) {
		this.type = type;
	}

	public String getObjName() {
		return objName;
	}

	public void setObjName(String objName) {
		this.objName = objName;
	}

	public List<String> getValue() {
		return value;
	}

	public void setValue(List<String> value) {
		this.value = value;
	}

	@Override
	public String toString() {
		String res = "";
		String n = "what";
		if (getName() == null)
			n = "null";
		else
			n = getName();
		String valueStr = "";
		// extra value, only send icc has value, receive icc has candidate
		if (getValue().size() > 0)
			valueStr = ":" + PrintUtils.printList(getValue());

		if (getType() == null) {
			res = "null-" + n + valueStr;
		} else if (getType() instanceof String) {
			if (SootUtils.isParOrSerExtra((String) getType()))
				res = getType() + "@" + getObjName() + "-" + n + valueStr + ",";
			else
				res = getType() + "-" + n + valueStr + ",";
		} else {
			BundleType bt = (BundleType) getType();
			res = bt.getType() + "-" + n + ",(," + bt.toString() + "),";
		}
		return res;
	}
//	
//	@Override
//	public String toString() {
//		String n = "what";
//		if (name == null)
//			n = "null";
//		else
//			n = name;
//		if (type == null) {
//			return "null-" + n + ",";
//		} else if (type instanceof String) {
//			// if(((String) type).contains("Parcelable"))
//			// return type+"@"+objName+"-" +n+",";
//			return type + "-" + n + ",";
//		} else {
//			BundleType bt = (BundleType) type;
//			return bt.type + "-" + n + ",(," + bt.toString() + "),";
//		}
//	}
//	
	

	@Override
	public Object clone() throws CloneNotSupportedException {
		ExtraData ed = new ExtraData(getType(), getName(), eaName, getObjName(), getValue());
		return ed;
	}

}
