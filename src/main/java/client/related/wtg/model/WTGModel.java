package main.java.client.related.wtg.model;

import main.java.client.obj.model.atg.ATGModel;

public class WTGModel {
	private ATGModel WTGAtgModel;

	public WTGModel() {
		setWTGAtgModel(new ATGModel());
	}

	/**
	 * @return the wTGAtgModel
	 */
	public ATGModel getWTGAtgModel() {
		return WTGAtgModel;
	}

	/**
	 * @param wTGAtgModel
	 *            the wTGAtgModel to set
	 */
	public void setWTGAtgModel(ATGModel wTGAtgModel) {
		WTGAtgModel = wTGAtgModel;
	}
}
