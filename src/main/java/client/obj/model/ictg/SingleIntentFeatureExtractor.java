package main.java.client.obj.model.ictg;

public class SingleIntentFeatureExtractor {

	public static String getSummaryStr(SingleIntentModel singleIntent) {
		String str = "";
		if (getReceiveStr(singleIntent).length() > 0)
			str += "Receive_";
		if (getNewStr(singleIntent).length() > 0)
			str += "New_";
		if (getUseAttributeStr(singleIntent).length() > 0)
			str += "Use_";
		if (getSetAttributeStr(singleIntent).length() > 0)
			str += "Set_";
		if (getSendStr(singleIntent).length() > 0)
			str += "Send_";
		if (str.length() > 0)
			str = str.substring(0, str.length() - 1);
		return str;
	}

	public static String getUseAttributeStr(SingleIntentModel singleIntent) {
		String str = "";
		if (singleIntent.getGetActionCandidateList().size() > 0)
			str += "action_";
		if (singleIntent.getGetCategoryCandidateList().size() > 0)
			str += "category_";
		if (singleIntent.getGetDataCandidateList().size() > 0)
			str += "data_";
		if (singleIntent.getGetTypeCandidateList().size() > 0)
			str += "type_";
		if (singleIntent.getGetExtrasCandidateList() != null)
			str += "extra_";
		if (str.length() > 0)
			str = str.substring(0, str.length() - 1);
		return str;
	}

	public static String getSetAttributeStr(SingleIntentModel singleIntent) {
		String str = "";
		if (singleIntent.getSetActionValueList().size() > 0)
			str += "action_";
		if (singleIntent.getSetCategoryValueList().size() > 0)
			str += "category_";
		if (singleIntent.getSetDataValueList().size() > 0)
			str += "data_";
		if (singleIntent.getSetTypeValueList().size() > 0)
			str += "type_";
		if (singleIntent.getSetExtrasValueList() != null)
			str += "extra_";
		if (str.length() > 0)
			str = str.substring(0, str.length() - 1);
		return str;
	}

	public static String getSendStr(SingleIntentModel singleIntent) {
		String str = "";
		if (singleIntent.getSendIntent2ICCList().size() > 0)
			str += "toICC_";
		if (str.length() > 0)
			str = str.substring(0, str.length() - 1);
		return str;
	}

	public static String getReceiveStr(SingleIntentModel singleIntent) {
		String str = "";
		if (singleIntent.getReceiveFromOutList().size() > 0)
			str += "getIntent_";
		if (singleIntent.getReceiveFromParaList().size() > 0)
			str += "parameter_";
		if (singleIntent.getReceiveFromFromRetValueList().size() > 0)
			str += "returnValue_";
		if (str.length() > 0)
			str = str.substring(0, str.length() - 1);
		return str;
	}

	public static String getNewStr(SingleIntentModel singleIntent) {
		String str = "";
		if (singleIntent.getCreateList().size() > 0)
			str += "newIntent_";
		if (str.length() > 0)
			str = str.substring(0, str.length() - 1);
		return str;
	}
}
