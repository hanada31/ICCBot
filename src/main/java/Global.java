package main.java;

import java.util.ArrayList;
import java.util.List;

import soot.Unit;
import main.java.analyze.model.analyzeModel.AppModel;
import main.java.analyze.model.labeledOracleModel.LabeledOracleModel;
import main.java.client.obj.model.fragment.FragmentModel;
import main.java.client.obj.model.ictg.ICTGModel;
import main.java.client.related.ic3.model.IC3Model;
import main.java.client.related.wtg.model.WTGModel;

public class Global {
	private static final Global instance = new Global();
	private AppModel appModel;
	private IC3Model iC3Model;
	private IC3Model iC3DialDroidModel;
	private ICTGModel iCTGModel;
	private WTGModel wtgModel;
	private LabeledOracleModel labeledOracleModel;

	private FragmentModel fragmentModel;
	private List<Unit> instrumentList;

	public int id = 0;

	public static Global v() {
		return instance;
	}

	private Global() {
		appModel = new AppModel();
		iC3Model = new IC3Model();
		iC3DialDroidModel = new IC3Model();
		fragmentModel = new FragmentModel();
		iCTGModel = new ICTGModel();
		setWtgModel(new WTGModel());
		setInstrumentList(new ArrayList<Unit>());
		labeledOracleModel = new LabeledOracleModel();
	}

	/**
	 * @return the appModel
	 */
	public AppModel getAppModel() {
		return appModel;
	}

	/**
	 * @return the iC3Model
	 */
	public IC3Model getiC3Model() {
		return iC3Model;
	}

	/**
	 * @return the iCTGModel
	 */
	public ICTGModel getiCTGModel() {
		return iCTGModel;
	}

	/**
	 * @return the fragmentModel
	 */
	public FragmentModel getFragmentModel() {
		return fragmentModel;
	}

	/**
	 * @return the instrumentList
	 */
	public List<Unit> getInstrumentList() {
		return instrumentList;
	}

	/**
	 * @param instrumentList
	 *            the instrumentList to set
	 */
	public void setInstrumentList(List<Unit> instrumentList) {
		this.instrumentList = instrumentList;
	}

	/**
	 * @param instrumentList
	 *            the instrumentList to set
	 */
	public void addInstrumentList(Unit u) {
		this.instrumentList.add(u);
	}

	/**
	 * @return the wtgModel
	 */
	public WTGModel getWtgModel() {
		return wtgModel;
	}

	/**
	 * @param wtgModel
	 *            the wtgModel to set
	 */
	public void setWtgModel(WTGModel wtgModel) {
		this.wtgModel = wtgModel;
	}

	/**
	 * @return the iC3DialDroidModel
	 */
	public IC3Model getiC3DialDroidModel() {
		return iC3DialDroidModel;
	}

	/**
	 * @param iC3DialDroidModel
	 *            the iC3DialDroidModel to set
	 */
	public void setiC3DialDroidModel(IC3Model iC3DialDroidModel) {
		this.iC3DialDroidModel = iC3DialDroidModel;
	}

	/**
	 * @return the labeledOracleModel
	 */
	public LabeledOracleModel getLabeledOracleModel() {
		return labeledOracleModel;
	}

	/**
	 * @param labeledOracleModel
	 *            the labeledOracleModel to set
	 */
	public void setLabeledOracleModel(LabeledOracleModel labeledOracleModel) {
		this.labeledOracleModel = labeledOracleModel;
	}

}
