package com.iscas.iccbot.client.obj.model.fragment;

import com.iscas.iccbot.analyze.model.analyzeModel.ObjectSummaryModel;
import com.iscas.iccbot.analyze.model.analyzeModel.PathSummaryModel;
import com.iscas.iccbot.analyze.utils.output.PrintUtils;
import soot.Unit;

import java.util.ArrayList;
import java.util.List;

public class FragmentSummaryModel extends ObjectSummaryModel {
    // unit
    private List<Unit> sendFragment2Start;
    private List<Unit> getFragmentFromOut;

    // value
    private List<String> addValueList;
    private List<String> replaceValueList;
    private List<String> setDestinationValueList;

    public FragmentSummaryModel(PathSummaryModel pathSummary) {
        super(pathSummary);
        setSendFragment2Start(new ArrayList<Unit>());
        setGetFragmentFromOut(new ArrayList<Unit>());

        setAddList(new ArrayList<String>());
        setReplaceList(new ArrayList<String>());
        setSetDestinationList(new ArrayList<String>());
    }

    public void copy(FragmentSummaryModel temp) {
        super.copy(temp);
        setSendFragment2Start(temp.getSendFragment2Start());
        setGetFragmentFromOut(temp.getGetFragmentFromOut());

        setAddList(temp.getAddList());
        setReplaceList(temp.getReplaceList());
        setSetDestinationList(temp.getSetDestinationList());
    }

    @Override
    public void merge(ObjectSummaryModel temp) {
        super.merge(temp);
        FragmentSummaryModel temp2 = (FragmentSummaryModel) temp;
        getSendFragment2Start().addAll(temp2.getSendFragment2Start());
        getGetFragmentFromOut().addAll(temp2.getGetFragmentFromOut());

        getAddList().addAll(temp2.getAddList());
        getReplaceList().addAll(temp2.getReplaceList());
        addSetDestinationList(temp2.getSetDestinationList());
    }

    @Override
    public String toHashString() {
        String res = super.toHashString();

        res += sendFragment2Start.size();
        res += addValueList.size();
        res += replaceValueList.size();
        res += PrintUtils.printList(setDestinationValueList);

        return res;
    }

    @Override
    public String toString() {
        String res = super.toString();

        res += "sendFragment2Start:" + PrintUtils.printList(sendFragment2Start) + "\n";
        res += "addList:" + PrintUtils.printList(addValueList) + "\n";
        res += "replaceList:" + PrintUtils.printList(replaceValueList) + "\n";
        res += "ListDestinationList:" + PrintUtils.printList(setDestinationValueList) + "\n";
        return res;
    }

    /**
     * @return the addList
     */
    public List<String> getAddList() {
        return addValueList;
    }

    /**
     * @param List the addList to List
     */
    public void setAddList(List<String> List) {
        this.addValueList = List;
    }

    /**
     * @return the replaceList
     */
    public List<String> getReplaceList() {
        return replaceValueList;
    }

    /**
     * @param replaceList the replaceList to List
     */
    public void setReplaceList(List<String> replaceList) {
        this.replaceValueList = replaceList;
    }

    /**
     * @return the sendFragment2Start
     */
    public List<Unit> getSendFragment2Start() {
        return sendFragment2Start;
    }

    /**
     * @param sendFragment2Start the sendFragment2Start to List
     */
    public void setSendFragment2Start(List<Unit> sendFragment2Start) {
        this.sendFragment2Start = sendFragment2Start;
    }

    /**
     * @return the ListDestinationList
     */
    public List<String> getSetDestinationList() {
        return setDestinationValueList;
    }

    public void addSetDestinationList(String string) {
        if(!setDestinationValueList.contains(string))
            setDestinationValueList.add(string);
    }
    public void addSetDestinationList(List <String> list) {
        for(String string: list){
            if(!setDestinationValueList.contains(string))
                setDestinationValueList.add(string);
        }
    }

    /**
     * @param ListDestinationList the ListDestinationList to List
     */
    public void setSetDestinationList(List<String> ListDestinationList) {
        this.setDestinationValueList = ListDestinationList;
    }

    /**
     * @return the getFragmentFromOut
     */
    public List<Unit> getGetFragmentFromOut() {
        return getFragmentFromOut;
    }

    /**
     * @param getFragmentFromOut the getFragmentFromOut to List
     */
    public void setGetFragmentFromOut(List<Unit> getFragmentFromOut) {
        this.getFragmentFromOut = getFragmentFromOut;
    }

}
